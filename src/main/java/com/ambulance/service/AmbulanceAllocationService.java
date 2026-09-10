package com.ambulance.service;

import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceType;
import com.ambulance.model.EmergencyRequest;
import com.ambulance.model.EmergencyType;

import java.util.Comparator;
import java.util.List;

public class AmbulanceAllocationService {

    public Ambulance findBestAmbulance(
            EmergencyRequest request,
            List<Ambulance> ambulances) {

        return ambulances.stream()
                .filter(Ambulance::isAvailable)
                .filter(a -> isSuitableType(
                        a.getType(),
                        request.getEmergencyType()))
                .min(Comparator.comparingDouble(
                        Ambulance::getCurrentDistance))
                .orElse(null);
    }

    private boolean isSuitableType(
            AmbulanceType ambulanceType,
            EmergencyType emergencyType) {

        if (emergencyType == EmergencyType.CRITICAL) {

            return ambulanceType == AmbulanceType.ICU
                    || ambulanceType == AmbulanceType.ADVANCED_LIFE_SUPPORT;
        }

        if (emergencyType == EmergencyType.HIGH) {

            return ambulanceType == AmbulanceType.ADVANCED_LIFE_SUPPORT
                    || ambulanceType == AmbulanceType.ICU
                    || ambulanceType == AmbulanceType.BASIC;
        }

        return true;
    }
}
