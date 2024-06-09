package com.strategies;
import com.data_management.PatientRecord;
public interface AlertStrategy {
    boolean checkAlert(PatientRecord record);
}
