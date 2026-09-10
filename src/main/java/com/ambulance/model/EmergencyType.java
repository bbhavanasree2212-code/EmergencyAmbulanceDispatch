package com.ambulance.model;

public enum EmergencyType {

    CRITICAL(1),
    HIGH(2),
    MODERATE(3),
    NORMAL(4);

    private final int priority;

    EmergencyType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
