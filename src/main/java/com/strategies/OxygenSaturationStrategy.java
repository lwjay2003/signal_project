package com.strategies;

import com.data_management.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {

        @Override
        public boolean checkAlert(PatientRecord record) {
            double saturation = record.getMeasurementValue();
            return saturation < 92;
        }

}
