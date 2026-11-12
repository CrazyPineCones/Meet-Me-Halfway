package backend.model;

// biking tabled for now, as Azure Maps Route Matrix API does not support biking. See https://learn.microsoft.com/en-us/azure/azure-maps/routes-matrix?tabs=java#supported-transport-modes
public enum TransportMode {
    DRIVING,
    WALKING
}