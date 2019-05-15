package pw.elka.simulator;

/**
 * TKEvent
 */
public class TKEvent {

    private final Number timeOfArrival;
    private final Number timeOfResidence;
    // ToDo: Dodać możliwe wartości statusu i ewentualnie zmienić jego typ
    private final String eventStatus;
    private final TKEventType type;

    public TKEvent(Number timeOfArrival, Number timeOfResidence, String eventStatus, TKEventType type) {
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
        this.eventStatus = eventStatus;
        this.type = type;
    }

    public Number getTimeOfArrival() {
        return timeOfArrival;
    }

    public Number getTimeOfResidence() {
        return timeOfResidence;
    }

    public String getEventStatus() { return eventStatus; }

    public TKEventType getType() {
        return type;
    }

    // ToDo: settery jak u Olka
}
