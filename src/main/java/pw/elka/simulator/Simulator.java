package pw.elka.simulator;

import java.awt.*;
import java.util.LinkedList;
import javafx.application.Platform;
import pw.elka.controllers.FXMLGuiController;

/**
 * Klasa reprezentująca symulator
 * @author Jan Jakubik & Oskar Misiewicz
 */
public class Simulator {

    boolean serverCrashed = false;
    double crashedTimeEnd = 0;
    private final TKTimeLine eventTL;
    private final TKTimeLine pendingTL;
    private final TKTimeLine crashesTL;
    private final double lambda;
    private final double mi;
    private double prevArrivalTime, waitingTime;
    private long service;
    private final Calculation calculation;
    private final GlobalCalculation globalCalculation;
    private final FXMLGuiController controller;

    public Simulator(double lambda, FXMLGuiController con) {
        this.lambda = lambda;
        this.mi = 8;
        this.service = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        this.eventTL = new TKTimeLine();
        this.pendingTL = new TKTimeLine();
        this.crashesTL = new TKTimeLine();
        this.calculation = new Calculation();
        this.globalCalculation = new GlobalCalculation();
        this.controller = con;

    }

    public void createEventList(long liczba, int seed, boolean uniform) {
        this.service = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        if (uniform) {
            this.eventTL.generateUniform(liczba, seed);
        } else {
            this.eventTL.generate(liczba, lambda, mi, seed);
        }
    }

    public void run(int numberOfRepeats, int numberOfEvents, boolean crashes, boolean uniform) throws CloneNotSupportedException {
        globalCalculation.clear();                        // Czyszczenie global statistic
        for (int i = 0; i < numberOfRepeats; ++i) {
            double p = (double) (i + 1) / numberOfRepeats;

            Platform.runLater(() -> {
                this.controller.setProgress(p);
            });

            createEventList(numberOfEvents, i, uniform);             // Utworzenie listy zdarzeń
            if (crashes) {
                this.crashesTL.generateCrashes(eventTL, i%2 + 1);
            }
            servEvent();                                 // Obsługa zdarzeń

            globalCalculation.addStat(calculation);        // Dodanie statystyk
            calculation.clear();                         // Czyszczenie obliczeń
        }
        Platform.runLater(() -> {
            System.out.print(globalCalculation.printStatistics());
        });

    }

    public void servEvent() {
        while (this.eventTL.getLength() > 0 || this.pendingTL.getLength() > 0) {
            if (this.service == 0) {
                if (pendingTL.getLength() > 0) {
                    servPending();
                } else if (eventTL.getLength() > 0) {
                    serviceCreated();
                }
            } else {
                if (eventTL.getLength() > 0) {
                    servServiced();
                }
            }
        }
    }

    public void serviceCreated() {
        // Nowe zdarzenia
        TKEvent event = eventTL.get(); // Pobranie zdarzenia
        waitingTime = 0;

        if (crashesTL.getLength() > 0) {
            TKEvent crash = crashesTL.get();
            if (crash.getTimeOfArrival().doubleValue() < event.getTimeOfArrival().doubleValue() && crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue() > event.getTimeOfArrival().doubleValue()) {
                calculation.waitingTime(prevArrivalTime, crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue());
                event.set((crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue()), TKEvent.Status.CREATED.getStatusText());

            } else if (crash.getTimeOfArrival().doubleValue() > event.getTimeOfArrival().doubleValue() && event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue() < crash.getTimeOfArrival().doubleValue()) {
                calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
                event.set((crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue()), TKEvent.Status.PROCESSING.getStatusText());

            } else {
                crashesTL.put(crash);
                calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
                event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()), TKEvent.Status.PROCESSING.getStatusText());
            }
        } else {
            calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
            event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()), TKEvent.Status.PROCESSING.getStatusText());
        }
        service = 1;
        eventTL.put(event);
    }

    // Zdarzenia w buforze
    public void servPending() {
        double addTime = 0;
        TKEvent event = pendingTL.get();
        // System.out.println("servPending() Obsługa zdarzenie o czasie obslugi:" + event.getTimeOfResidence());
        if (serverCrashed) {
            waitingTime = calculation.waitingTime(prevArrivalTime, crashedTimeEnd);
        } else {
            waitingTime = calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        }
        event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(), TKEvent.Status.PROCESSING.getStatusText());
        service = 1;
        eventTL.put(event);
    }

    public void servServiced() {

        TKEvent event = eventTL.get();
        if (event.getEventStatus().getStatusText().equals(TKEvent.Status.PROCESSING.getStatusText())) {
            service = 0;
            calculation.processingTime(waitingTime, event.getTimeOfResidence().doubleValue());
            prevArrivalTime = event.getTimeOfArrival().doubleValue();
        } else if (event.getEventStatus().getStatusText().equals(TKEvent.Status.CREATED.getStatusText())) {
            event.set(-1, TKEvent.Status.PENDING.getStatusText());
            pendingTL.put(event);
        }
        if (event.getTimeOfArrival().doubleValue() > crashedTimeEnd) {
            serverCrashed = false;
        }

    }

    public void printGlobalCalculation(int numElem) {
        this.globalCalculation.printStatistics();
    }

    public String getCsv() {
        return globalCalculation.printCSV();
    }

    public void compute() {
        globalCalculation.compute();
    }
}
