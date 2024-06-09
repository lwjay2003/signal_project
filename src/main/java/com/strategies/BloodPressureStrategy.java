package com.strategies;

import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(PatientRecord record) {
        double[] bpValues = record.getMeasurementValues();
        double systolic = bpValues[0];
        double diastolic = bpValues[1];
        return systolic > 180 || systolic < 90 || diastolic > 120 || diastolic < 60;
    }
}

