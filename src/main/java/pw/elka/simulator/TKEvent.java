package pw.elka.simulator;

/**
 * TKEvent
 */
public class TKEvent {

    private final Number time;
    private final TKEventType type;

    public TKEvent(Number time, TKEventType type) {
        this.time = time;
        this.type = type;
    }

    public Number getTime() {
        return time;
    }

    public TKEventType getType() {
        return type;
    }
}
