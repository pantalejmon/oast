package pw.elka.simulator;

import java.awt.Event;
import java.util.Comparator;
import java.util.LinkedList;

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

    public void put(Number time, TKEventType type) {
        this.timeLine.add(new TKEvent(time, type));
        this.timeLine.sort((e1,e2)-> {
            return (int)Math.ceil(e1.getTime().doubleValue() - e2.getTime().doubleValue());
        });
    }
    
    public TKEvent get() {
        TKEvent min = this.timeLine.element();
        //Teoretycznie sie to nie przyda
        for(TKEvent t : this.timeLine) {
            if(t.getTime().doubleValue() < min.getTime().doubleValue()){
                min = t;
            }
        }
        this.timeLine.remove(min);
        return min;
    }
}