package pw.elka.simulator;

import java.awt.*;
import java.util.LinkedList;
import javafx.application.Platform;
import pw.elka.controllers.FXMLGuiController;

/**
 * Simulator1
 */
public class Simulator {

    private TKTimeLine pastTL;
    private TKTimeLine pendingTL;
    private TKTimeLine crashesTL;
    private double lmd, mi, prevArrivalTime, waitingTime;
    boolean rej;
    private long queueLen, queueCount, service, eventQuantity;
    private Calculation calculation;
    private GlobalCalculation globalStatistic;
    private final FXMLGuiController controller;

    public Simulator(double lambda, double ro, long ql, boolean rej, FXMLGuiController con) {
        this.lmd = lambda;
        this.mi = 1 / ro;
        this.queueLen = ql;
        this.queueCount = 0;
        this.service = 0;
        this.eventQuantity = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        this.rej = rej;
        this.pastTL = new TKTimeLine();
        this.pendingTL = new TKTimeLine();
        this.crashesTL = new TKTimeLine();
        this.calculation = new Calculation();
        this.globalStatistic = new GlobalCalculation();
        this.controller = con;
    }

    public void createEventList(long liczba, int seed) {
        this.service = 0;
        this.queueCount = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        this.pastTL.generate(liczba, lmd, mi, seed);
    }

    // ToDo: nazwa jest na razie jak u Olka
    public void estimate(int numberOfRepeats, int numberOfEvents, boolean crashes) throws CloneNotSupportedException {
        globalStatistic.clear();                        // Czyszczenie global statistic
        for (int i = 0; i < numberOfRepeats; ++i) {
            double p = (double) (i + 1) / numberOfRepeats;

            Platform.runLater(() -> {
                this.controller.setProgress(p);
            });
            createEventList(numberOfEvents, i);             // Utworzenie listy zdarzeń
            if (crashes) {
                this.crashesTL.generateCrashes(pastTL);
                System.out.println("Wygenerowano zawiechy");
                //this.crashesTL.print();
            }
            servEvent();                                 // Obsługa zdarzeń
            //System.out.print(calculation.printStatistics()); 
            globalStatistic.addStat(calculation);        // Dodanie statystyk
            calculation.clear();                         // Czyszczenie obliczeń
        }
        //System.out.print(globalStatistic.printStatistics());    // Wydruk kontrolny 
        //System.out.print(globalStatistic.printCSV()); 
    }

    public void servEvent() {
        //System.out.println("Rozpoczynam obliczanie");
        //System.out.println("Ilosc zdarzen:" + this.pastTL.getLength());
        while (this.pastTL.getLength() > 0 || this.pendingTL.getLength() > 0) {
            if (this.service == 0) {
                if (pendingTL.getLength() > 0) {
                    servPending();
                } else if (pastTL.getLength() > 0) {
                    servCreated();
                }
            } else {
                if (pastTL.getLength() > 0) {
                    servServiced();
                }
            }
        }
    }

    public void servCreated() {
        // Nowe zdarzenia
        TKEvent event = pastTL.get(); // Pobranie zdarzenia
        //System.out.println("servCreated() zdarzenia o czasie obslugi:" + event.getTimeOfResidence());
        waitingTime = 0;
        calculation.addCasQueue(queueCount);
        calculation.addCasSys(queueCount);
        if (crashesTL.getLength() > 0) {
            TKEvent crash = crashesTL.get();
            if (crash.getTimeOfArrival().doubleValue() < event.getTimeOfArrival().doubleValue() && (crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue()) > event.getTimeOfArrival().doubleValue()) {
                calculation.addWaitTime(prevArrivalTime, crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue());
                event.set((crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue() + event.getTimeOfResidence().doubleValue()),
                        TKEvent.Status.PROCESSING.getStatusText());

                pastTL.put(event);
            } else {
                crashesTL.put(crash);
                calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
                event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()),
                        TKEvent.Status.PROCESSING.getStatusText());
            }
        } else {
            calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
            event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()),
                    TKEvent.Status.PROCESSING.getStatusText());
        }
        service = 1;
        pastTL.put(event);
    }

    // Zdarzenia w buforze
    public void servPending() {
        TKEvent event = pendingTL.get();
        // System.out.println("servPending() Obsługa zdarzenie o czasie obslugi:" + event.getTimeOfResidence());
        waitingTime = calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(),
                TKEvent.Status.PROCESSING.getStatusText());
        service = 1;
        queueCount -= 1;
        pastTL.put(event);
    }

    public void servServiced() {

        // Koniec obsługi
        TKEvent event = pastTL.get();
        if (event.getEventStatus().getStatusText().equals(TKEvent.Status.PROCESSING.getStatusText())) {
            //System.out.println("servServiced() Koniec obsługi zdarzenia o czasie obslugi:" + event.getTimeOfResidence());
            service = 0;
            calculation.addPocTime(waitingTime, event.getTimeOfResidence().doubleValue());
            prevArrivalTime = event.getTimeOfArrival().doubleValue();
        } else if (event.getEventStatus().getStatusText().equals(TKEvent.Status.CREATED.getStatusText())) {//Dodanie zdarzenia do listy zdarzeń oczekujacych 
            //System.out.println("servServiced() Wrzucanie zdarzenia do oczekujacych o czasie obslugi:" + event.getTimeOfResidence());
            calculation.addCasQueue(queueCount);
            calculation.addCasSys(queueCount + service);
            event.set(-1, TKEvent.Status.PENDING.getStatusText());
            if (queueCount >= queueLen) {
                calculation.addBuffGT();
                if (rej) {
                    calculation.addRejCount();
                }
            }
            queueCount += 1;
            //System.out.print("que: " +  queueCount);
            pendingTL.put(event);
        }
    }

    public void printList() {
        pastTL.print();
    }

    public void printStat(int numElem) {
        this.calculation.printStatistics();
    }

    public void printGlobStat(int numElem) {
        this.globalStatistic.printStatistics();
    }

    public String getCsv() {
        return globalStatistic.printCSV();
    }

    public void compute() {
        globalStatistic.compute();
    }
}
