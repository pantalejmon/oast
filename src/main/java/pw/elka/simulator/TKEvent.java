package pw.elka.simulator;

/**
 * TKEvent
 */
public class TKEvent {

    public enum Status {
        CREATED("created"),
        PENDING("pending"),
        PROCESSING("processing"),
        CRASHED("crashed");

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
    private Status eventStatus;

    public TKEvent(Number timeOfArrival, Number timeOfResidence, String eventStatus) {
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
        this.eventStatus = Status.fromString(eventStatus);
    }

    public TKEvent(double timeOfArrival, double timeOfResidence) {
        this.eventStatus = Status.CREATED;
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
        //System.out.println("Zdarznie o czasie przyjscia: "+ timeOfArrival + " Czasie obslugi: " + timeOfResidence);
    }

    public TKEvent(double timeOfArrival, double timeOfResidence, Status status) {
        this.eventStatus = status;
        this.timeOfArrival = timeOfArrival;
        this.timeOfResidence = timeOfResidence;
        //System.out.println("Zdarznie o czasie przyjscia: "+ timeOfArrival + " Czasie obslugi: " + timeOfResidence);
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

    public boolean earlierThan(TKEvent e) {
        return this.timeOfArrival.longValue() < e.getTimeOfArrival().longValue();
    }

    public void set(Number arrival, String status) {
        this.eventStatus = Status.fromString(status);
        this.timeOfArrival = (arrival.doubleValue() > 0) ? arrival : this.timeOfArrival;
    }

}
