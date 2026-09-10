package com.ambulance.model;

public class Driver {

    private String driverId;
    private String name;
    private String phoneNumber;

    public Driver(String driverId, String name, String phoneNumber) {
        this.driverId = driverId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return driverId + " - " + name + " - " + phoneNumber;
    }
}
