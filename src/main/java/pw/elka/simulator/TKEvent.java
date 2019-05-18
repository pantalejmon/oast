package pw.elka.simulator;

/**
 * TKEvent
 */
public class TKEvent {
    
    enum Status {
        CREATED("created"),
        PENDING("pending"),
        PROCESSING("processing");
        
        private String statusText;
        
        private Status(String statusText) {
            this.statusText = statusText;
        }
        
        public String getStatusText() {
            return statusText;
        }

        public static Status fromString(String text) {
            for (Status b : Status.values()) {
                if (b.statusText.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }
    
    private Number timeOfArrival; // Chyba nie ma co dawac tu final
    private final Number timeOfResidence;
    // ToDo: Dodać możliwe wartości statusu i ewentualnie zmienić jego typ
    private Status eventStatus;
    private  TKEventType type;
    
    public TKEvent(Number timeOfArrival, Number timeOfResidence, String eventStatus, TKEventType type) {
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
        this.eventStatus = Status.fromString(eventStatus);
        this.type = type;
    }

    public TKEvent(Number timeOfArrival, Number timeOfResidence) {
        this.eventStatus = Status.CREATED;
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
    }
    
    
    public Number getTimeOfArrival() {
        return timeOfArrival;
    }
    
    public Number getTimeOfResidence() {
        return timeOfResidence;
    }

    public Status getEventStatus() {
        return eventStatus;
    }
   
    public TKEventType getType() {
        return type;
    }
    
    public boolean earlierThan(TKEvent e) {
        return this.timeOfArrival.longValue() < e.getTimeOfArrival().longValue();
    }
    
    public void set(Number arrival, String status) {
        this.eventStatus = Status.fromString(status);
        this.timeOfArrival = (arrival.doubleValue() > 0) ?  arrival : this.timeOfArrival;
    }            

}
