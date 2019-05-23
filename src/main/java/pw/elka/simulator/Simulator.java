package pw.elka.simulator;

import javafx.application.Platform;
import pw.elka.controllers.FXMLGuiController;

/**
 * Klasa reprezentująca symulator
 *
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

    public void createEventList(long liczba, boolean uniform) {
        this.service = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        if (uniform) {
            this.eventTL.generateUniform(liczba);
        } else {
            this.eventTL.generate(liczba, lambda, mi);
        }
    }

    public void run(int numberOfRepeats, int numberOfEvents, boolean crashes, boolean uniform) throws CloneNotSupportedException {
        globalCalculation.clear();                        // Czyszczenie global statistic
        for (int i = 0; i < numberOfRepeats; ++i) {
            double p = (double) (i + 1) / numberOfRepeats;

            Platform.runLater(() -> {
                this.controller.setProgress(p);
            });

            createEventList(numberOfEvents, uniform);             // Utworzenie listy zdarzeń
            if (crashes) {
                this.crashesTL.generateCrashes(eventTL);
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
                event.set((crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue() + event.getTimeOfResidence().doubleValue()), TKEvent.Status.PROCESSING.getStatusText());
                crashesTL.put(crash);
            } else if (crash.getTimeOfArrival().doubleValue() > event.getTimeOfArrival().doubleValue() && event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue() < crash.getTimeOfArrival().doubleValue()) {
                double delta = (crash.getTimeOfArrival().doubleValue() - event.getTimeOfArrival().doubleValue());
                double deltafull = crash.getTimeOfResidence().doubleValue() + delta;
                calculation.waitingTime(prevArrivalTime, (event.getTimeOfArrival().doubleValue() + deltafull));
                event.set((event.getTimeOfArrival().doubleValue() + deltafull + (event.getTimeOfResidence().doubleValue() - delta)), TKEvent.Status.PROCESSING.getStatusText());
                crashesTL.put(crash);
            } else if (crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue() < event.getTimeOfArrival().doubleValue()) {
                calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
                event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()), TKEvent.Status.PROCESSING.getStatusText());
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

            waitingTime = calculation.waitingTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
            event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(), TKEvent.Status.PROCESSING.getStatusText());
            eventTL.put(event);
        service = 1;
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
