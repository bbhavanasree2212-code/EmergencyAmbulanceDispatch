package com.ambulance.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.ambulance.exception.AmbulanceUnavailableException;
import com.ambulance.exception.InvalidEmergencyRequestException;
import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceState;
import com.ambulance.model.EmergencyRequest;
import com.ambulance.model.EmergencyStatus;

public class EmergencyDispatchService {

    private final List<Ambulance> ambulances = new ArrayList<>();
    private final List<EmergencyRequest> history = new ArrayList<>();

    private final PriorityQueue<EmergencyRequest> waitingQueue =
            new PriorityQueue<>(
                    Comparator.comparingInt(
                            r -> r.getEmergencyType().getPriority()
                    )
            );

    private final AmbulanceAllocationService allocationService =
            new AmbulanceAllocationService();

    public void addAmbulance(Ambulance ambulance) {
        if (ambulance == null) {
            throw new IllegalArgumentException("Ambulance cannot be null.");
        }

        ambulances.add(ambulance);
    }

    public void submitEmergency(EmergencyRequest request) {

        validateRequest(request);

        history.add(request);

        Ambulance ambulance =
                allocationService.findBestAmbulance(request, ambulances);

        if (ambulance == null) {

            waitingQueue.offer(request);

            System.out.println("No suitable ambulance available.");
            System.out.println("Request added to waiting queue.");

            return;
        }

        dispatch(request, ambulance);
    }

    private void dispatch(
            EmergencyRequest request,
            Ambulance ambulance) {

        if (!ambulance.isAvailable()) {

            throw new AmbulanceUnavailableException(
                    "Ambulance " +
                    ambulance.getAmbulanceId() +
                    " is already assigned."
            );
        }

        ambulance.setState(AmbulanceState.DISPATCHED);

        request.setAssignedAmbulance(ambulance);

        request.setStatus(EmergencyStatus.DISPATCHED);

        // Assumption:
        // Average ambulance speed = 40 km/h
        // Time = Distance / Speed * 60
        double arrivalTime =
                ambulance.getCurrentDistance() / 40.0 * 60.0;

        request.setEstimatedArrivalTime(arrivalTime);

        System.out.println(
                "Ambulance " +
                ambulance.getAmbulanceId() +
                " assigned to Patient " +
                request.getPatientId()
        );

        System.out.printf(
                "Estimated arrival time: %.2f minutes%n",
                arrivalTime
        );
    }

    public void updateAmbulanceState(
            String ambulanceId,
            AmbulanceState newState) {

        Ambulance ambulance = findAmbulance(ambulanceId);

        ambulance.setState(newState);

        EmergencyRequest activeRequest =
                history.stream()
                        .filter(r ->
                                r.getAssignedAmbulance() == ambulance)
                        .filter(r ->
                                r.getStatus() != EmergencyStatus.COMPLETED)
                        .findFirst()
                        .orElse(null);

        if (activeRequest != null) {

            switch (newState) {

                case EN_ROUTE:
                    activeRequest.setStatus(
                            EmergencyStatus.EN_ROUTE
                    );
                    break;

                case PATIENT_PICKED_UP:
                    activeRequest.setStatus(
                            EmergencyStatus.PATIENT_PICKED_UP
                    );
                    break;

                case HOSPITAL_ARRIVED:
                    activeRequest.setStatus(
                            EmergencyStatus.HOSPITAL_ARRIVED
                    );
                    break;

                case AVAILABLE:
                    activeRequest.setStatus(
                            EmergencyStatus.COMPLETED
                    );
                    break;

                default:
                    break;
            }
        }

        if (newState == AmbulanceState.AVAILABLE) {
            allocateWaitingRequest();
        }
    }

    private void allocateWaitingRequest() {

        if (waitingQueue.isEmpty()) {
            return;
        }

        EmergencyRequest selectedRequest =
                waitingQueue.peek();

        Ambulance ambulance =
                allocationService.findBestAmbulance(
                        selectedRequest,
                        ambulances
                );

        if (ambulance != null) {

            waitingQueue.poll();

            dispatch(
                    selectedRequest,
                    ambulance
            );
        }
    }

    private Ambulance findAmbulance(
            String ambulanceId) {

        return ambulances.stream()
                .filter(a ->
                        a.getAmbulanceId()
                                .equals(ambulanceId))
                .findFirst()
                .orElseThrow(() ->
                        new AmbulanceUnavailableException(
                                "Ambulance not found: "
                                + ambulanceId
                        )
                );
    }

    private void validateRequest(
            EmergencyRequest request) {

        if (request == null) {

            throw new InvalidEmergencyRequestException(
                    "Emergency request cannot be null."
            );
        }

        if (request.getPatientId() == null ||
                request.getPatientId().isBlank()) {

            throw new InvalidEmergencyRequestException(
                    "Patient ID is required."
            );
        }

        if (request.getEmergencyType() == null) {

            throw new InvalidEmergencyRequestException(
                    "Emergency type is required."
            );
        }

        if (request.getPickupLocation() == null ||
                request.getPickupLocation().isBlank()) {

            throw new InvalidEmergencyRequestException(
                    "Pickup location is required."
            );
        }

        if (request.getDestinationHospital() == null ||
                request.getDestinationHospital().isBlank()) {

            throw new InvalidEmergencyRequestException(
                    "Destination hospital is required."
            );
        }

        if (request.getEstimatedDistance() <= 0) {

            throw new InvalidEmergencyRequestException(
                    "Distance must be greater than zero."
            );
        }
    }

    public List<EmergencyRequest> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int getWaitingQueueSize() {
        return waitingQueue.size();
    }

    public List<Ambulance> getAmbulances() {
        return Collections.unmodifiableList(ambulances);
    }
}