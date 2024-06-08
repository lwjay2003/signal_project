package com.alerts;

// Represents an alert
public class Alert {
    private final int patientId;
    private final String condition;
    private final long timestamp;


    public Alert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "patientId=" + patientId +
                ", condition='" + condition + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
