package dao;

public class Data {
    private String BusID;
    private String destination;
    private String loading_bay;
    private String timeOfArrival;
    private String timeOfDeparture;
    private String duration;
    private String violation;

    public Data(String BusID, String destination, String loading_bay,
                String timeOfArrival, String timeOfDeparture, String duration, String violation) {
        this.BusID = BusID;
        this.loading_bay = loading_bay;
        this.destination = destination;
        this.timeOfArrival = timeOfArrival;
        this.timeOfDeparture = timeOfDeparture;
        this.duration = duration;
        this.violation = violation;
    }

    public String getLoading_bay() {
        return loading_bay;
    }

    public void setLoading_bay(String loading_bay) {
        this.loading_bay = loading_bay;
    }

    public String getBusID() {
        return BusID;
    }

    public void setBusID(String busID) {
        BusID = busID;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimeOfArrival() {
        return timeOfArrival;
    }

    public void setTimeOfArrival(String timeOfArrival) {
        this.timeOfArrival = timeOfArrival;
    }

    public String getTimeOfDeparture() {
        return timeOfDeparture;
    }

    public void setTimeOfDeparture(String timeOfDeparture) {
        this.timeOfDeparture = timeOfDeparture;
    }


    public String getDuration() {
        return duration;
    }

    public String getViolation() { return violation; }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
