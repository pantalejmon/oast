package pw.elka.simulator;

import java.awt.Event;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

/**
 * TimeLine
 */
public class TKTimeLine {

    private LinkedList<TKEvent> timeLine;
    private int seed;

    public TKTimeLine() {
        timeLine = new LinkedList<>();
    }

    public void put(TKEvent t) {
        this.timeLine.add(t);
        this.timeLine.sort((e1, e2) -> {
            return (int) Math.ceil(e1.getTimeOfArrival().doubleValue() - e2.getTimeOfArrival().doubleValue());
        });
    }

    public TKEvent get() {
        TKEvent min = this.timeLine.element();
        //Teoretycznie sie to nie przyda
        for (TKEvent t : this.timeLine) {
            if (t.getTimeOfArrival().doubleValue() < min.getTimeOfArrival().doubleValue()) {
                min = t;
            }
        }
        this.timeLine.remove(min);
        //System.out.println("Zwracam AT: " + min.getTimeOfArrival().doubleValue() + " RT: " + min.getTimeOfResidence().doubleValue() + " Status: " + min.getEventStatus().getStatusText());
        return min;
    }

    public int getLength() {
        return this.timeLine.size();
    }

    public void generate(long n, double lmd, double mi, double seed) {
        double at = 0, rt = 0;
        this.timeLine.clear(); // Czyszcze liste
        Random rand = new Random((long) seed);
        for (int i = 0; i < n; ++i) {
            rt = exponentialGenerator(rand, mi);
            this.timeLine.add(new TKEvent(at, rt));
            at += exponentialGenerator(rand, lmd);
        }
    }
    
    public void generateUniform( long n ,double seed) {
        double at = 0, rt = 0;
        this.timeLine.clear(); // Czyszcze liste
        Random rand = new Random((long) seed);
        for (int i = 0; i < n; ++i) {
            rt = rand.nextDouble() * (0.15 - 0.1)+0.1;
            this.timeLine.add(new TKEvent(at, rt));
            at += rand.nextDouble() * (0.15 - 0.1)+0.1;
        }
    }
    
    public void generateCrashes(TKTimeLine events, double seed) {
        double at = 0, rt = 0;
        this.timeLine.clear(); // Czyszcze liste
        //System.out.println("Generuje crashe seed: " + seed);
        Random rand = new Random((long) seed);
        while(at < (events.getLast().getTimeOfArrival().doubleValue() + events.getLast().getTimeOfResidence().doubleValue() )){
            double t = 0.02857142857142857;
            rt = exponentialGenerator(rand, t);
            //System.out.println("Generuje crashe");
            t = 0.025;
            at += exponentialGenerator(rand, t);
            this.timeLine.add(new TKEvent(at,rt , TKEvent.Status.CRASHED));
        }
    }

    public double exponentialGenerator(Random rand, double param) {

        //System.out.println("rand: " + rand.nextDouble());
        return Math.log(1 - rand.nextDouble()) / (-param);
    }

    public void print() {

        System.out.println("Number of events: " + this.timeLine.size());
        for (TKEvent t : this.timeLine) {
            System.out.println("AT: " + t.getTimeOfArrival().doubleValue() + " RT: " + t.getTimeOfResidence().doubleValue() + " Status: " + t.getEventStatus().getStatusText());
        }
        System.out.println("____________________________");
    }
    public TKEvent getLast() {
        return this.timeLine.getLast();
    }
}
