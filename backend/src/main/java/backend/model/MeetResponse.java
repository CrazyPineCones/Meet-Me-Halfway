package backend.model;

import java.util.List;

public class MeetResponse {

    private List<TheaterResponse> theaters;

    public List<TheaterResponse> getTheaters() {
        return theaters;
    }

    public void setTheaters(List<TheaterResponse> theaters) {
        this.theaters = theaters;
    }
}