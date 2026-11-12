
package backend.model;

public class MeetingPoint {

    private double latitude;
    private double longitude;

    public MeetingPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
