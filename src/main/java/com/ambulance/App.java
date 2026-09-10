package com.ambulance;

import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceState;
import com.ambulance.model.AmbulanceType;
import com.ambulance.model.Driver;
import com.ambulance.model.EmergencyRequest;
import com.ambulance.model.EmergencyType;
import com.ambulance.service.EmergencyDispatchService;

public class App {

    public static void main(String[] args) {

        EmergencyDispatchService service =
                new EmergencyDispatchService();

        // -----------------------------
        // Drivers
        // -----------------------------

        Driver d1 =
                new Driver(
                        "D001",
                        "Arun",
                        "9876543210");

        Driver d2 =
                new Driver(
                        "D002",
                        "Rahul",
                        "9876543211");

        Driver d3 =
                new Driver(
                        "D003",
                        "Kumar",
                        "9876543212");

        // -----------------------------
        // Ambulances
        // -----------------------------

        Ambulance a1 =
                new Ambulance(
                        "AMB001",
                        AmbulanceType.BASIC,
                        d1,
                        8);

        Ambulance a2 =
                new Ambulance(
                        "AMB002",
                        AmbulanceType.ADVANCED_LIFE_SUPPORT,
                        d2,
                        5);

        Ambulance a3 =
                new Ambulance(
                        "AMB003",
                        AmbulanceType.ICU,
                        d3,
                        10);

        service.addAmbulance(a1);
        service.addAmbulance(a2);
        service.addAmbulance(a3);

        // -----------------------------
        // Emergency Requests
        // -----------------------------

        EmergencyRequest r1 =
                new EmergencyRequest(
                        "P001",
                        EmergencyType.CRITICAL,
                        "VIT Vellore",
                        "CMC Hospital",
                        15);

        EmergencyRequest r2 =
                new EmergencyRequest(
                        "P002",
                        EmergencyType.HIGH,
                        "Katpadi",
                        "Apollo Hospital",
                        10);

        EmergencyRequest r3 =
                new EmergencyRequest(
                        "P003",
                        EmergencyType.MODERATE,
                        "Gandhi Nagar",
                        "Government Hospital",
                        6);

        // -----------------------------
        // Submit Requests
        // -----------------------------

        System.out.println(
                "===== EMERGENCY DISPATCH =====");

        service.submitEmergency(r1);
        service.submitEmergency(r2);
        service.submitEmergency(r3);

        // -----------------------------
        // Ambulance Status
        // -----------------------------

        System.out.println(
                "\n===== AMBULANCE STATUS =====");

        for (Ambulance ambulance :
                service.getAmbulances()) {

            System.out.println(ambulance);
        }

        // -----------------------------
        // Emergency History
        // -----------------------------

        System.out.println(
                "\n===== EMERGENCY HISTORY =====");

        for (EmergencyRequest request :
                service.getHistory()) {

            System.out.println(request);
        }

        System.out.println(
                "\nWaiting Queue: "
                        + service.getWaitingQueueSize());

        // -----------------------------
        // Ambulance State Changes
        // -----------------------------

        System.out.println(
                "\n===== STATE TRANSITION =====");

        service.updateAmbulanceState(
                "AMB002",
                AmbulanceState.EN_ROUTE);

        service.updateAmbulanceState(
                "AMB002",
                AmbulanceState.PATIENT_PICKED_UP);

        service.updateAmbulanceState(
                "AMB002",
                AmbulanceState.HOSPITAL_ARRIVED);

        service.updateAmbulanceState(
                "AMB002",
                AmbulanceState.AVAILABLE);

        System.out.println(
                "\nAMB002 is now available.");
    }
}
