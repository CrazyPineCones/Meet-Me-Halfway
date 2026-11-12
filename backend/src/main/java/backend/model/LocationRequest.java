package backend.model;

import jakarta.validation.constraints.NotBlank;

public class LocationRequest {

    @NotBlank
    private String address;


    private TransportMode mode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TransportMode getMode() {
        return mode;
    }

    public void setMode(TransportMode mode) {
        this.mode = mode;
    }
}