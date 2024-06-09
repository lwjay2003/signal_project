package com.alerts;

public abstract class AlertFactory {
    public abstract Alert createAlert(int patientId, String condition, long timestamp);
}
