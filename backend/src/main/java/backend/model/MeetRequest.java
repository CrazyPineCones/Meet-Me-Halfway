package backend.model;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class MeetRequest {

    @NotEmpty
    private List<LocationRequest> locations;

    private RankingPreference rankingPreference = RankingPreference.GEOGRAPHIC;

    private Integer maxTravelTimeMinutes;

    public List<LocationRequest> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationRequest> locations) {
        this.locations = locations;
    }

    public RankingPreference getRankingPreference() {
        return rankingPreference;
    }

    public void setRankingPreference(RankingPreference rankingPreference) {
        this.rankingPreference = rankingPreference;
    }

    public Integer getMaxTravelTimeMinutes() {
        return maxTravelTimeMinutes;
    }

    public void setMaxTravelTimeMinutes(Integer maxTravelTimeMinutes) {
        this.maxTravelTimeMinutes = maxTravelTimeMinutes;
    }
}