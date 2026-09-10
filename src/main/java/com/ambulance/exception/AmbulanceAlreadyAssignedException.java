package com.ambulance.exception;

public class AmbulanceAlreadyAssignedException extends RuntimeException {

    public AmbulanceAlreadyAssignedException(String message) {
        super(message);
    }
}
