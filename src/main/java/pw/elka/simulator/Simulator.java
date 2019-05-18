package pw.elka.simulator;

import java.awt.*;
import java.util.LinkedList;

/**
 * Simulator1
 */
public class Simulator {

    private TKTimeLine pastTL;
    private TKTimeLine pendingTL;
    private double lmd, mi, prevArrivalTime, waitingTime;
    boolean rej;
    private long queueLen, queueCount, service, eventQuantity;
    private Calculation calculation, globalStatistic;

    public Simulator(double ro, long ql, boolean rej) {
        this.lmd = 1;
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
        this.calculation = new Calculation();
        this.globalStatistic = new Calculation();
    }

    public void createEventList(long liczba, double seed) {
        this.service = 0;
        this.queueCount = 0;
        this.prevArrivalTime = 0;
        this.waitingTime = 0;
        this.pastTL.generate(liczba, lmd, mi, seed);
    }

    // ToDo: nazwa jest na razie jak u Olka
    public void estimate(int numberOfRepeats, int numberOfEvents) {
        for (int i = 0; i < numberOfRepeats; ++i) {
            createEventList(numberOfEvents, i);
            servEvent();
            globalStatistic.addStat(calculation);
            calculation.clear();
        }
    }

    public void servEvent() {
        while (this.pastTL.getLength() > 0 || this.pendingTL.getLength() > 0) {
            if (this.service == 0) {
                if (pendingTL.getLength() > 0) {
                    servPending();
                } else if (pastTL.getLength() > 0) {
                    servCreated();
                }
            } else {
                if (pastTL.getLength() > 0) servServiced();
            }
        }
    }

    public void servCreated() {
        TKEvent event = pastTL.get();
        waitingTime = 0;
        calculation.addCasQueue(queueCount);
        calculation.addCasSys(queueCount);
        calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        service = 1;
        event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()),
                TKEvent.Status.PROCESSING.getStatusText());
        pastTL.put(event);
    }

    public void servPending() {
        TKEvent event = pendingTL.get();
        waitingTime = calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(),
                TKEvent.Status.PROCESSING.getStatusText());
        service = 1;
        queueCount -= 1;
        pastTL.put(event);
    }

    public void servServiced() {
        TKEvent event = pastTL.get();
        if (event.getEventStatus().getStatusText().equals(TKEvent.Status.PROCESSING.getStatusText())) {
            service = 0;
            calculation.addPocTime(waitingTime, event.getTimeOfResidence().doubleValue());
            prevArrivalTime = event.getTimeOfArrival().doubleValue();
        } else if (event.getEventStatus().getStatusText().equals(TKEvent.Status.CREATED.getStatusText())) {
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
            pendingTL.put(event);
        }
    }

    public void printList() {
        pastTL.print();
    }

    public void printStat(int numElem) {
        // ToDo: uzupełnić = inaczej to implementujemy? - nasza metoda nie przyjmuje parametrów, u olka tak

    }

    public void printGlobStat(int numElem) {
        // ToDo: uzupełnić = inaczej to implementujemy?
    }
}

