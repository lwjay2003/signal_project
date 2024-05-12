package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link # triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        List<PatientRecord> records = dataStorage.getRecords(0, System.currentTimeMillis(), System.currentTimeMillis());
        checkBloodPressure(patient, records);

    }
    private void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        // Implementation goes here
        Double lastSystolic = null;
        Double lastDiastolic = null;
        int increasedCount = 0;
        int decreasedCount = 0;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodPressure")) {

                String[] bpValues = record.getMeasurementValue().split("/");
                double systolic = Double.parseDouble(bpValues[0]);
                double diastolic = Double.parseDouble(bpValues[1]);

                //trend alert for systolic
                if (lastSystolic != null ) {
                    if (systolic - lastSystolic > 10) {
                        increasedCount++;
                        decreasedCount = 0;
                    } else if (lastDiastolic - diastolic > 10) {
                        decreasedCount++;
                        increasedCount = 0;
                    } else {
                        increasedCount = 0;
                        decreasedCount = 0;
                    }
                    if (increasedCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "IncreasingBloodPressureTrend", record.getTimestamp()));
                    }

                    if (decreasedCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "DecreasingBloodPressureTrend", record.getTimestamp()));
                    }
                }
                    if (systolic > 180 || systolic < 90 || diastolic > 120 || diastolic < 60) {
                        triggerAlert(new Alert(record.getPatientId(), "CriticalBloodPressureThreshold", record.getTimestamp()));
                    }

                    lastSystolic = systolic;
                    lastDiastolic = diastolic;
                }
            }
        }



            /**
             * Triggers an alert for the monitoring system. This method can be extended to
             * notify medical staff, log the alert, or perform other actions. The method
             * currently assumes that the alert information is fully formed when passed as
             * an argument.
             *
             * @param alert the alert object containing details about the alert condition
             */
            private void triggerAlert(Alert alert) {
             System.out.println("Alert triggered: " + alert.toString());
            }


        }


