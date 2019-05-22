package pw.elka.simulator;

import java.awt.*;
import java.util.LinkedList;
import javafx.application.Platform;
import pw.elka.controllers.FXMLGuiController;

/**
 * Simulator1
 */
public class Simulator {

    boolean serverCrashed = false;
    double crashedTimeEnd = 0;
    private TKTimeLine pastTL;
    private TKTimeLine pendingTL;
    private TKTimeLine crashesTL;
    private double lmd, mi, prevArrivalTime, waitingTime;
    boolean rej;
    private long queueCount, service, eventQuantity;
    private Calculation calculation;
    private GlobalCalculation globalStatistic;
    private final FXMLGuiController controller;

    public Simulator(double lambda, double ro, boolean rej, FXMLGuiController con) {
        this.lmd = lambda;
        this.mi = 8;

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

    public void createEventList(long liczba, int seed, boolean uniform) {
        this.service = 0;
        this.queueCount = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        if (uniform) {
            this.pastTL.generateUniform(liczba, seed);
        } else {
            this.pastTL.generate(liczba, lmd, mi, seed);
        }
        // this.pastTL.print();
    }

    // ToDo: nazwa jest na razie jak u Olka
    public void estimate(int numberOfRepeats, int numberOfEvents, boolean crashes, boolean uniform) throws CloneNotSupportedException {
        globalStatistic.clear();                        // Czyszczenie global statistic
        for (int i = 0; i < numberOfRepeats; ++i) {
            double p = (double) (i + 1) / numberOfRepeats;

            Platform.runLater(() -> {
                this.controller.setProgress(p);
            });

            createEventList(numberOfEvents, i, uniform);             // Utworzenie listy zdarzeń
            if (crashes) {
                this.crashesTL.generateCrashes(pastTL);
                //System.out.println("Wygenerowano zawiechy");
                //this.crashesTL.print();
            }
            servEvent();                                 // Obsługa zdarzeń
            //System.out.print(calculation.printStatistics()); 
            globalStatistic.addStat(calculation);        // Dodanie statystyk
            calculation.clear();                         // Czyszczenie obliczeń
        }
        Platform.runLater(() -> {
            System.out.print(globalStatistic.printStatistics());
        });
        // Wydruk kontrolny 
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
        if (serverCrashed) {
            calculation.addWaitTime(prevArrivalTime, crashedTimeEnd);
            event.set((crashedTimeEnd + event.getTimeOfResidence().doubleValue()),
                    TKEvent.Status.PROCESSING.getStatusText());

            pastTL.put(event);

        } else if (crashesTL.getLength() > 0) {
            TKEvent crash = crashesTL.get();
            if (crash.getTimeOfArrival().doubleValue() < event.getTimeOfArrival().doubleValue() && (crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue()) > event.getTimeOfArrival().doubleValue()) {
                calculation.addWaitTime(prevArrivalTime, crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue());
                event.set((crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue() + event.getTimeOfResidence().doubleValue()),
                        TKEvent.Status.PROCESSING.getStatusText());

                pastTL.put(event);
                serverCrashed = true;
                crashedTimeEnd = crash.getTimeOfArrival().doubleValue() + crash.getTimeOfResidence().doubleValue();
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
        double addTime = 0;
        TKEvent event = pendingTL.get();
        // System.out.println("servPending() Obsługa zdarzenie o czasie obslugi:" + event.getTimeOfResidence());
        if (serverCrashed) {
            waitingTime = calculation.addWaitTime(prevArrivalTime, crashedTimeEnd);
        } else {
            waitingTime = calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        }

        event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(),
                TKEvent.Status.PROCESSING.getStatusText());
        service = 1;
        queueCount -= 1;
        pastTL.put(event);
    }

    public void servServiced() {

        // Koniec obsługi
        TKEvent event = pastTL.get();
        if (!serverCrashed) {
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
                queueCount += 1;
                //System.out.print("que: " +  queueCount);
                pendingTL.put(event);
            }
        } else {
            if (event.getTimeOfArrival().doubleValue() > crashedTimeEnd) {
                serverCrashed = false;
            } else {
                if (event.getEventStatus().getStatusText().equals(TKEvent.Status.PROCESSING.getStatusText())) {
                //System.out.println("servServiced() Koniec obsługi zdarzenia o czasie obslugi:" + event.getTimeOfResidence());
                service = 0;
                calculation.addPocTime(waitingTime, event.getTimeOfResidence().doubleValue() + crashedTimeEnd);
                prevArrivalTime = event.getTimeOfArrival().doubleValue();
            } else if (event.getEventStatus().getStatusText().equals(TKEvent.Status.CREATED.getStatusText())) {//Dodanie zdarzenia do listy zdarzeń oczekujacych 
                //System.out.println("servServiced() Wrzucanie zdarzenia do oczekujacych o czasie obslugi:" + event.getTimeOfResidence());

                calculation.addCasQueue(queueCount);
                calculation.addCasSys(queueCount + service);
                event.set(-1, TKEvent.Status.PENDING.getStatusText());
                queueCount += 1;
                //System.out.print("que: " +  queueCount);
                pendingTL.put(event);
            

            }
        }
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
