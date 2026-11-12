
package backend.service;

import backend.model.Coordinates;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import backend.model.Theater;
import backend.model.TransportMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

@Service
public class AzureMapsService {

    private final RestClient restClient = RestClient.create();

    public Coordinates geocodeAddress(String address) {

        String key = System.getenv("AZURE_MAPS_KEY");

        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                    "Azure Maps key is not configured.");
        }

        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("atlas.microsoft.com")
                        .path("/search/address/json")
                        .queryParam("api-version", "1.0")
                        .queryParam("query", address)
                        .queryParam("subscription-key", key)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode firstResult = response.path("results").path(0);

        if (firstResult.isMissingNode()
                || firstResult.path("position").isMissingNode()) {
            throw new IllegalArgumentException(
                    "No matching location was found.");
        }

        double latitude = firstResult
                .path("position").path("lat").asDouble();

        double longitude = firstResult
                .path("position").path("lon").asDouble();

        return new Coordinates(latitude, longitude);
    }

    public List<Theater> findNearbyTheaters(double latitude, double longitude) {

        String key = System.getenv("AZURE_MAPS_KEY");

        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("atlas.microsoft.com")
                        .path("/search/poi/json")
                        .queryParam("api-version", "1.0")
                        .queryParam("query", "movie theater")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("radius", 30000)
                        .queryParam("limit", 5)
                        .queryParam("countrySet", "US")
                        .queryParam("subscription-key", key)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        List<Theater> theaters = new ArrayList<>();

        for (JsonNode result : response.path("results")) {

            JsonNode poi = result.path("poi");
            JsonNode position = result.path("position");

            if (poi.isMissingNode() || position.isMissingNode()) {
                continue;
            }

            theaters.add(new Theater(
                    poi.path("name").asString(),
                    position.path("lat").asDouble(),
                    position.path("lon").asDouble()));
        }

        return theaters;
    }

    // The newer Route Matrix API documents driving and walking, but not biking.
    // Azure Maps Route Matrix.
    public double getTravelTimeMinutes(
            Coordinates origin,
            Theater destination,
            TransportMode travelMode) {

        String key = System.getenv("AZURE_MAPS_KEY");

        Map<String, Object> originFeature = Map.of(
                "type", "Feature",
                "geometry", Map.of(
                        "type", "MultiPoint",
                        "coordinates", List.of(
                                List.of(origin.getLongitude(), origin.getLatitude()))),
                "properties", Map.of("pointType", "origins"));

        Map<String, Object> destinationFeature = Map.of(
                "type", "Feature",
                "geometry", Map.of(
                        "type", "MultiPoint",
                        "coordinates", List.of(
                                List.of(destination.getLongitude(), destination.getLatitude()))),
                "properties", Map.of("pointType", "destinations"));

        Map<String, Object> requestBody = Map.of(
                "type", "FeatureCollection",
                "features", List.of(originFeature, destinationFeature),
                "travelMode", switch (travelMode) {
                    case DRIVING -> "driving";
                    case WALKING -> "walking";
                });

        JsonNode response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("atlas.microsoft.com")
                        .path("/route/matrix")
                        .queryParam("api-version", "2025-01-01")
                        .queryParam("subscription-key", key)
                        .build())
                .contentType(MediaType.parseMediaType("application/geo+json"))
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        double seconds = response
                .path("properties")
                .path("matrix")
                .path(0)
                .path("durationInSeconds")
                .asDouble();

        return seconds / 60.0;
    }
}
