package com.strategies;

import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class ECGStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(PatientRecord record) {
        double heartRate = record.getMeasurementValue();
        return heartRate < 50 || heartRate > 100;
    }
    public boolean checkIrregularBeat(List<PatientRecord> records) {
        List<Long> timestamps = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("HeartRate")) {
                heartRates.add(record.getMeasurementValue());
                timestamps.add(record.getTimestamp());
            }
        }

        for (int i = 1; i < timestamps.size(); i++) {
            long interval = timestamps.get(i) - timestamps.get(i - 1);
            if (interval != 0) {
                if (Math.abs(interval - 60000 / heartRates.get(i)) > 200) {
                    return true;
                }
            }
        }
        return false;
    }

}
