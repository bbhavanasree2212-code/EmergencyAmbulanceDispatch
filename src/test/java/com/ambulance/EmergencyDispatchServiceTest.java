package com.ambulance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.ambulance.exception.InvalidEmergencyRequestException;
import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceState;
import com.ambulance.model.AmbulanceType;
import com.ambulance.model.Driver;
import com.ambulance.model.EmergencyRequest;
import com.ambulance.model.EmergencyStatus;
import com.ambulance.model.EmergencyType;
import com.ambulance.service.EmergencyDispatchService;

public class EmergencyDispatchServiceTest {

    // 1. Critical emergency should get ICU/ALS ambulance
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
                request.getAssignedAmbulance().getAmbulanceId());

        assertEquals(
                EmergencyStatus.DISPATCHED,
                request.getStatus());
    }

    // 2. One ambulance cannot be assigned to two active emergencies
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

        assertEquals(
                "AMB001",
                request1.getAssignedAmbulance().getAmbulanceId());

        assertNull(
                request2.getAssignedAmbulance());
    }

    // 3. Waiting request should get ambulance when it becomes available
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

        assertEquals(
                EmergencyStatus.DISPATCHED,
                request2.getStatus());
    }

    // 4. Invalid patient ID should be rejected
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

    // 5. Estimated arrival time should be calculated correctly
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

        // 20 km / 40 km/h * 60 = 30 minutes
        assertEquals(
                30.0,
                request.getEstimatedArrivalTime());
    }

    // 6. Zero distance should be rejected
    @Test
    void testZeroDistanceIsInvalid() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.HIGH,
                        "Location A",
                        "Hospital A",
                        0);

        assertThrows(
                InvalidEmergencyRequestException.class,
                () -> service.submitEmergency(request));
    }

    // 7. Negative distance should be rejected
    @Test
    void testNegativeDistanceIsInvalid() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.HIGH,
                        "Location A",
                        "Hospital A",
                        -5);

        assertThrows(
                InvalidEmergencyRequestException.class,
                () -> service.submitEmergency(request));
    }

    // 8. Missing pickup location should be rejected
    @Test
    void testMissingPickupLocation() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.MODERATE,
                        "",
                        "Hospital A",
                        10);

        assertThrows(
                InvalidEmergencyRequestException.class,
                () -> service.submitEmergency(request));
    }

    // 9. Missing destination hospital should be rejected
    @Test
    void testMissingDestinationHospital() {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.MODERATE,
                        "Location A",
                        "",
                        10);

        assertThrows(
                InvalidEmergencyRequestException.class,
                () -> service.submitEmergency(request));
    }

    // 10. Critical emergency should not receive only a Basic ambulance
    @Test
    void testCriticalEmergencyWaitsWhenOnlyBasicAmbulanceIsAvailable() {

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

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.CRITICAL,
                        "Location A",
                        "Hospital A",
                        10);

        service.submitEmergency(request);

        assertEquals(
                1,
                service.getWaitingQueueSize());

        assertNull(
                request.getAssignedAmbulance());

        assertEquals(
                EmergencyStatus.WAITING,
                request.getStatus());
    }

    // 11. Ambulance state changes should update emergency status
    @Test
    void testAmbulanceStateTransitions() {

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
                        AmbulanceType.ADVANCED_LIFE_SUPPORT,
                        driver,
                        5);

        service.addAmbulance(ambulance);

        EmergencyRequest request =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.HIGH,
                        "Location A",
                        "Hospital A",
                        10);

        service.submitEmergency(request);

        service.updateAmbulanceState(
                "AMB001",
                AmbulanceState.EN_ROUTE);

        assertEquals(
                EmergencyStatus.EN_ROUTE,
                request.getStatus());

        service.updateAmbulanceState(
                "AMB001",
                AmbulanceState.PATIENT_PICKED_UP);

        assertEquals(
                EmergencyStatus.PATIENT_PICKED_UP,
                request.getStatus());

        service.updateAmbulanceState(
                "AMB001",
                AmbulanceState.HOSPITAL_ARRIVED);

        assertEquals(
                EmergencyStatus.HOSPITAL_ARRIVED,
                request.getStatus());
    }
}