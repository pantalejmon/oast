package pw.elka.simulator;

import java.awt.Event;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

/**
 * TimeLine
 */
public class TKTimeLine {

    /**
     *
     */
    private LinkedList<TKEvent> timeLine;

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
        return min;
    }

    public int getLength() {
        return this.timeLine.size();
    }

    public void generate(long n, double lmd, double mi, double seed) {
        double at = 0, rt = 0;
        this.timeLine.clear(); // Czyszcze liste
        for (int i = 0; i < n; ++i) {
            rt = exponentialGenerator((long) seed, mi);
            this.timeLine.push(new TKEvent(at, rt));
            at += exponentialGenerator((long) seed, lmd);
        }
    }

    public double exponentialGenerator(long seed, double param) {
        Random rand = new Random(seed);
        return Math.log(1 - rand.nextDouble()) / (-param);
    }
    
    public void print() {
        for(TKEvent t : timeLine){
            System.out.println("AT: " + t.getTimeOfArrival().doubleValue() + " RT: " + t.getTimeOfResidence().doubleValue());
        }
        System.out.println("____________________________");
    }
}
