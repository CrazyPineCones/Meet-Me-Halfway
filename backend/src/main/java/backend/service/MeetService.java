package backend.service;

import backend.model.Coordinates;
import backend.model.LocationRequest;
import backend.model.MeetRequest;
import backend.model.MeetResponse;
import backend.model.MeetingPoint;
import backend.model.RankingPreference;
import backend.model.TheaterResponse;
import backend.service.AzureMapsService;
import org.springframework.stereotype.Service;
import backend.model.MeetingPoint;
import backend.model.Theater;
import java.util.ArrayList;
import backend.model.TravelTimeResponse;
import java.util.List;
import java.util.Comparator;

@Service
public class MeetService {

    private final AzureMapsService azureMapsService;

    public MeetService(AzureMapsService azureMapsService) {
        this.azureMapsService = azureMapsService;
    }

    public MeetResponse findTheaters(MeetRequest request) {

        // Eventually:
        // 1. Get locations from request
        // 2. Send locations to Azure Maps
        // 3. Find nearby movie theaters
        // 4. Calculate travel times
        // 5. Rank theaters

        if (request.getLocations() == null || request.getLocations().size() < 2) {
            throw new IllegalArgumentException("At least two locations are required.");
        }

        if (request.getMaxTravelTimeMinutes() != null
                && request.getMaxTravelTimeMinutes() <= 0) {
            throw new IllegalArgumentException(
                    "Maximum travel time must be greater than zero.");
        }

        List<LocationRequest> locations = request.getLocations();

        RankingPreference preference = request.getRankingPreference() == null
                ? RankingPreference.GEOGRAPHIC
                : request.getRankingPreference();

        for (LocationRequest location : locations) {

            if (location.getAddress() == null || location.getAddress().isBlank()) {
                throw new IllegalArgumentException("Address cannot be blank.");
            }

            if (location.getMode() == null) {
                throw new IllegalArgumentException("Transportation mode is required.");
            }

            System.out.println("Address: " + location.getAddress());
            System.out.println("Mode: " + location.getMode());
        }

        // For now, just mock the results
        // List<TheaterResponse> theaters = List.of(
        // new TheaterResponse("AMC Northlake", 24.5),
        // new TheaterResponse("Regal Stonecrest", 28.0));

        // MeetResponse response = new MeetResponse();
        // response.setTheaters(theaters);

        List<Coordinates> coordinatesList = new java.util.ArrayList<>();

        for (LocationRequest location : locations) {
            Coordinates coordinates = azureMapsService.geocodeAddress(location.getAddress());

            coordinatesList.add(coordinates);

            System.out.println(
                    location.getAddress() + " -> " +
                            coordinates.getLatitude() + ", " +
                            coordinates.getLongitude());
        }

        double averageLatitude = coordinatesList.stream()
                .mapToDouble(Coordinates::getLatitude)
                .average()
                .orElseThrow();

        double averageLongitude = coordinatesList.stream()
                .mapToDouble(Coordinates::getLongitude)
                .average()
                .orElseThrow();

        MeetingPoint meetingPoint = new MeetingPoint(averageLatitude, averageLongitude);

        System.out.println(
                "Meeting point: " +
                        meetingPoint.getLatitude() + ", " +
                        meetingPoint.getLongitude());

        List<Theater> nearbyTheaters = azureMapsService.findNearbyTheaters(
                meetingPoint.getLatitude(),
                meetingPoint.getLongitude());

        if (preference == RankingPreference.GEOGRAPHIC) {
            nearbyTheaters.sort(
                    Comparator.comparingDouble(theater -> calculateDistance(
                            meetingPoint.getLatitude(),
                            meetingPoint.getLongitude(),
                            theater.getLatitude(),
                            theater.getLongitude())));
        }

        System.out.println("Nearby theaters found: " + nearbyTheaters.size());

        for (Theater theater : nearbyTheaters) {
            System.out.println(
                    theater.getName() + " -> " +
                            theater.getLatitude() + ", " +
                            theater.getLongitude());
        }

        List<TheaterResponse> theaterResponses = new ArrayList<>();

        for (Theater theater : nearbyTheaters) {

            double totalMinutes = 0;
            boolean exceedsMaxTime = false;

            List<TravelTimeResponse> travelTimes = new ArrayList<>();

            for (int i = 0; i < locations.size(); i++) {

                double minutes = azureMapsService.getTravelTimeMinutes(
                        coordinatesList.get(i),
                        theater,
                        locations.get(i).getMode());

                if (request.getMaxTravelTimeMinutes() != null
                        && minutes > request.getMaxTravelTimeMinutes()) {
                    exceedsMaxTime = true;
                    break;
                }

                totalMinutes += minutes;

                travelTimes.add(
                        new TravelTimeResponse(
                                locations.get(i).getAddress(),
                                minutes));
            }

            if (exceedsMaxTime) {
                continue;
            }

            double averageMinutes = totalMinutes / locations.size();

            double distanceKm = calculateDistance(
                    meetingPoint.getLatitude(),
                    meetingPoint.getLongitude(),
                    theater.getLatitude(),
                    theater.getLongitude());

            double distanceMiles = distanceKm * 0.621371;

            TheaterResponse theaterResponse = new TheaterResponse(
                    theater.getName(),
                    averageMinutes,
                    distanceMiles,
                    travelTimes);

            theaterResponses.add(theaterResponse);
        }
        if (preference == RankingPreference.TIME) {
            theaterResponses.sort(
                    Comparator.comparingDouble(TheaterResponse::getAverageMinutes));
        }

        MeetResponse response = new MeetResponse();
        response.setTheaters(theaterResponses);

        return response;
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}