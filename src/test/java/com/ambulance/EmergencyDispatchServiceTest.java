package com.ambulance;

import com.ambulance.exception.InvalidEmergencyRequestException;
import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceState;
import com.ambulance.model.AmbulanceType;
import com.ambulance.model.Driver;
import com.ambulance.model.EmergencyRequest;
import com.ambulance.model.EmergencyStatus;
import com.ambulance.model.EmergencyType;
import com.ambulance.service.EmergencyDispatchService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmergencyDispatchServiceTest {

    @Test
    void testCriticalEmergencyGetsSuitableAmbulance() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        Driver driver =
                new Driver(
                        "D001",
                        "Arun",
                        "9876543210");

        Ambulance ambulance =
                new Ambulance(
                        "AMB001",
                        AmbulanceType.ICU,
                        driver,
                        5);

        service.addAmbulance(ambulance);

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.CRITICAL,
                        "Location A",
                        "Hospital A",
                        10);

        service.submitEmergency(request);

        assertEquals(
                "AMB001",
                request.getAssignedAmbulance()
                        .getAmbulanceId());

        assertEquals(
                EmergencyStatus.DISPATCHED,
                request.getStatus());
    }

    @Test
    void testAmbulanceCannotBeAssignedTwice() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        Driver driver =
                new Driver(
                        "D001",
                        "Arun",
                        "9876543210");

        Ambulance ambulance =
                new Ambulance(
                        "AMB001",
                        AmbulanceType.BASIC,
                        driver,
                        5);

        service.addAmbulance(ambulance);

        EmergencyRequest request1 =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.NORMAL,
                        "Location A",
                        "Hospital A",
                        10);

        EmergencyRequest request2 =
                new EmergencyRequest(
                        "P002",
                        EmergencyType.NORMAL,
                        "Location B",
                        "Hospital B",
                        10);

        service.submitEmergency(request1);
        service.submitEmergency(request2);

        assertEquals(
                1,
                service.getWaitingQueueSize());
    }

    @Test
    void testWaitingRequestGetsAllocated() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        Driver driver =
                new Driver(
                        "D001",
                        "Arun",
                        "9876543210");

        Ambulance ambulance =
                new Ambulance(
                        "AMB001",
                        AmbulanceType.BASIC,
                        driver,
                        5);

        service.addAmbulance(ambulance);

        EmergencyRequest request1 =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.NORMAL,
                        "Location A",
                        "Hospital A",
                        10);

        EmergencyRequest request2 =
                new EmergencyRequest(
                        "P002",
                        EmergencyType.NORMAL,
                        "Location B",
                        "Hospital B",
                        10);

        service.submitEmergency(request1);
        service.submitEmergency(request2);

        assertEquals(
                1,
                service.getWaitingQueueSize());

        service.updateAmbulanceState(
                "AMB001",
                AmbulanceState.AVAILABLE);

        assertEquals(
                0,
                service.getWaitingQueueSize());
    }

    @Test
    void testInvalidRequest() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        EmergencyRequest request =
                new EmergencyRequest(
                        "",
                        EmergencyType.HIGH,
                        "Location A",
                        "Hospital A",
                        10);

        assertThrows(
                InvalidEmergencyRequestException.class,
                () -> service.submitEmergency(request));
    }

    @Test
    void testEstimatedArrivalTime() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        Driver driver =
                new Driver(
                        "D001",
                        "Arun",
                        "9876543210");

        Ambulance ambulance =
                new Ambulance(
                        "AMB001",
                        AmbulanceType.BASIC,
                        driver,
                        20);

        service.addAmbulance(ambulance);

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.NORMAL,
                        "Location A",
                        "Hospital A",
                        10);

        service.submitEmergency(request);

        assertEquals(
                30.0,
                request.getEstimatedArrivalTime());
    }
}
