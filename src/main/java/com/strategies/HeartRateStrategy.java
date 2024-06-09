package com.strategies;

import com.data_management.PatientRecord;

public class HeartRateStrategy  implements AlertStrategy{
    @Override
    public boolean checkAlert(PatientRecord record) {
        double heartRate = record.getMeasurementValue();
        return heartRate < 50 || heartRate > 100;
    }

}



