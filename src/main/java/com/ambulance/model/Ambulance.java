package com.ambulance.model;

public class Ambulance {

    private String ambulanceId;
    private AmbulanceType type;
    private AmbulanceState state;
    private Driver driver;
    private double currentDistance;

    public Ambulance(
            String ambulanceId,
            AmbulanceType type,
            Driver driver,
            double currentDistance) {

        this.ambulanceId = ambulanceId;
        this.type = type;
        this.driver = driver;
        this.currentDistance = currentDistance;
        this.state = AmbulanceState.AVAILABLE;
    }

    public String getAmbulanceId() {
        return ambulanceId;
    }

    public AmbulanceType getType() {
        return type;
    }

    public AmbulanceState getState() {
        return state;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getCurrentDistance() {
        return currentDistance;
    }

    public void setState(AmbulanceState state) {
        this.state = state;
    }

    public void setCurrentDistance(double currentDistance) {
        this.currentDistance = currentDistance;
    }

    public boolean isAvailable() {
        return state == AmbulanceState.AVAILABLE;
    }

    @Override
    public String toString() {
        return ambulanceId +
                " | Type: " + type +
                " | State: " + state +
                " | Driver: " + driver.getName() +
                " | Distance: " + currentDistance + " km";
    }
}
