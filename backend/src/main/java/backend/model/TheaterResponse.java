package backend.model;


import java.util.List;

public class TheaterResponse {

    private String name;
    private double averageMinutes;
    private double distanceFromMidpointMiles;
    private List<TravelTimeResponse> travelTimes;

    public TheaterResponse(
            String name,
            double averageMinutes,
            double distanceFromMidpointMiles,
            List<TravelTimeResponse> travelTimes) {

        this.name = name;
        this.averageMinutes = averageMinutes;
        this.distanceFromMidpointMiles = distanceFromMidpointMiles;
        this.travelTimes = travelTimes;
    }

    public String getName() {
        return name;
    }

    public double getAverageMinutes() {
        return averageMinutes;
    }

    public double getDistanceFromMidpointMiles() {
        return distanceFromMidpointMiles;
    }

    public List<TravelTimeResponse> getTravelTimes() {
        return travelTimes;
    }
}

    // getters and setters