package pw.elka.simulator;

import java.util.LinkedList;

/**
 * Simulator1
 */
public class Simulator {

    private TKTimeLine pastTL;
    private TKTimeLine pendingTL;
    private double lmd, mi,prevArrivalTime, waitingTime;
    boolean rej;
    private long queueLen, queueCount, service, eventQuantity;
 
    public Simulator(double ro, long ql,boolean rej) {
        this.lmd = 1;
        this.mi = 1/ro;
        this.queueLen = ql;
        this.service = 0;
        this.eventQuantity = 0;
        this.prevArrivalTime =0;
        this.waitingTime = 0;
        this.rej = rej;
    }
    
    public void createEventList(long liczba, double seed) {
        this.service = 0;
        this.queueLen = 0;
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
    }
    
    public void servPending() {
        // TODO: Uzupełnić
    }
    
    public void servServiced() {
        // TODO: Uzupełnić
    }
}

