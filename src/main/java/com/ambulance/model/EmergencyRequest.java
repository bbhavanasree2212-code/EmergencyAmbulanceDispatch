package com.ambulance.model;

public class EmergencyRequest {

    private String patientId;
    private EmergencyType emergencyType;
    private String pickupLocation;
    private String destinationHospital;
    private double estimatedDistance;
    private EmergencyStatus status;
    private Ambulance assignedAmbulance;
    private double estimatedArrivalTime;

    public EmergencyRequest(
            String patientId,
            EmergencyType emergencyType,
            String pickupLocation,
            String destinationHospital,
            double estimatedDistance) {

        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.pickupLocation = pickupLocation;
        this.destinationHospital = destinationHospital;
        this.estimatedDistance = estimatedDistance;
        this.status = EmergencyStatus.WAITING;
    }

    public String getPatientId() {
        return patientId;
    }

    public EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDestinationHospital() {
        return destinationHospital;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public EmergencyStatus getStatus() {
        return status;
    }

    public Ambulance getAssignedAmbulance() {
        return assignedAmbulance;
    }

    public double getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public void setAssignedAmbulance(Ambulance assignedAmbulance) {
        this.assignedAmbulance = assignedAmbulance;
    }

    public void setEstimatedArrivalTime(double estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    @Override
    public String toString() {
        return "Patient ID: " + patientId +
                " | Emergency: " + emergencyType +
                " | Pickup: " + pickupLocation +
                " | Hospital: " + destinationHospital +
                " | Distance: " + estimatedDistance + " km" +
                " | Status: " + status;
    }
}
