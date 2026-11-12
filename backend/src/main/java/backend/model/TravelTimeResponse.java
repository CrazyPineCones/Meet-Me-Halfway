package backend.model;

public class TravelTimeResponse {

    private String address;
    private double minutes;

    public TravelTimeResponse(String address, double minutes) {
        this.address = address;
        this.minutes = minutes;
    }

    public String getAddress() {
        return address;
    }

    public double getMinutes() {
        return minutes;
    }
}