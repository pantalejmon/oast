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
    private Calculation calculation;
 
    public Simulator(double ro, long ql,boolean rej) {
        this.lmd = 1;
        this.mi = 1/ro;
        this.queueLen = ql;
        this.queueCount = 0;
        this.service = 0;
        this.eventQuantity = 0;
        this.prevArrivalTime =0;
        this.waitingTime = 0;
        this.rej = rej;
        this.calculation = new Calculation();
    }
    
    public void createEventList(long liczba, double seed) {
        this.service = 0;
        this.queueCount = 0;
        this.prevArrivalTime =0;
        this.waitingTime = 0;
        this.pastTL.generate(liczba, lmd, mi, seed); 
    }
    
    public void servEvent() {
        while(this.pastTL.getLength() > 0 || this.pendingTL.getLength() > 0) {
            if(this.service == 0) {
               // TODO: Uzupełnić
                
            }
        }
    }
    
    public void servCreated() {
        // TODO: Uzupełnić
        TKEvent event = pastTL.get();
        waitingTime = 0;
        calculation.addCasQueue(queueLen);
        calculation.addCasSys(queueLen);
        calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        service = 1;
        event.set((event.getTimeOfArrival().doubleValue() + event.getTimeOfResidence().doubleValue()), "pending");
        pastTL.put(event);
    }
    
    public void servPending() {
        // TODO: Uzupełnić
        TKEvent event = pendingTL.get();
        waitingTime = calculation.addWaitTime(prevArrivalTime, event.getTimeOfArrival().doubleValue());
        event.set(prevArrivalTime + event.getTimeOfResidence().doubleValue(), "pending");
        service = 1;
        queueCount -= 1;
        pastTL.put(event);
    }
    
    public void servServiced() {
        // TODO: Uzupełnić
        TKEvent event = pastTL.get();

    }
}

