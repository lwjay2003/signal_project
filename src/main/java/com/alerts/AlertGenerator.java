package com.alerts;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private static DataStorage dataStorage;
    private static List<Alert> alerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage, List<Alert> alerts) {
        if (dataStorage == null) {
            throw new IllegalArgumentException("DataStorage cannot be null");
        }
        AlertGenerator.dataStorage = dataStorage;
        AlertGenerator.alerts = alerts;
    }




    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public static List<Alert> evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), 0,System.currentTimeMillis());
        checkBloodPressure(patient, records);
        checkBloodSaturationAlerts(patient, records);
        checkHypotensiveHypoxemiaAlerts(patient, records);
        checkECGDataAlerts(patient, records);
        return alerts;


    }



    /**
     * Checks the ECG data for alerts related to abnormal heart rates and irregular beat patterns.
     * Abnormal heart rates are defined as below 50 or above 100 beats per minute. Irregular beat patterns
     * are identified based on the time intervals between successive heartbeats, with a tolerance of 200 milliseconds
     * from the expected interval calculated based on the average beats per minute.
     *
     * @param patient The patient whose ECG data is being checked.
     * @param records A list of patient records containing heart rate data and associated timestamps.
     */
   private static void checkECGDataAlerts(Patient patient, List<PatientRecord> records) {
        List<Long> timestamps = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("HeartRate")) {
                heartRates.add(record.getMeasurementValue());
                timestamps.add(record.getTimestamp());

                // Abnormal Heart Rate Alert
                double heartRate = record.getMeasurementValue();
                if (heartRate < 50 || heartRate > 100) {
                    triggerAlert(new Alert(patient.getPatientId(), "AbnormalHeartRate", record.getTimestamp()));
                }
            }
        }

        // Check for Irregular Beat Patterns
       for (int i = 1; i < timestamps.size(); i++) {
           long interval = timestamps.get(i) - timestamps.get(i - 1);
           if (interval != 0){
               if (Math.abs(interval - 60000 / heartRates.get(i)) > 200) { // 200 ms tolerance for irregularity
                   triggerAlert(new Alert(patient.getPatientId(), "IrregularHeartBeat", timestamps.get(i)));
               }
           }

       }
   }



    /**
     * Checks for concurrent hypotension and hypoxemia within the patient's records.
     * Hypotension is identified by a systolic blood pressure below 90 mmHg. Hypoxemia is defined as
     * a blood saturation level below 92%. An alert is triggered if both conditions are found in the
     * same set of records.
     *
     * @param patient The patient whose records are being evaluated.
     * @param records A list of patient records that includes blood pressure and blood saturation measurements.
     */
    private static void checkHypotensiveHypoxemiaAlerts(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodPressure")) {
                double systolic = record.getMeasurementValues()[0]; // Use the first value for systolic

                // Check if there's a corresponding low saturation record
                for (PatientRecord saturationRecord : records) {
                    if (saturationRecord.getRecordType().equals("BloodSaturation")) {
                        double saturation = saturationRecord.getMeasurementValue();


                        if (systolic < 90 && saturation < 92) {
                            triggerAlert(new Alert(record.getPatientId(), "HypotensiveHypoxemiaAlert", record.getTimestamp()));
                        }
                    }
                }
            }
        }
    }



    /**
     * Checks for alerts related to blood saturation levels from patient records. It generates alerts for
     * low blood saturation when the level falls below 92%. Additionally, it checks for rapid drops in saturation,
     * triggering an alert if the saturation decreases by 5% or more within a 10-minute window.
     *
     * @param patient The patient whose blood saturation is being monitored.
     * @param records A list of patient records that include blood saturation measurements.
     */
    private static void checkBloodSaturationAlerts(Patient patient, List<PatientRecord> records) {
        Double lastSaturation = null;
        long lastTimestamp = -1;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodSaturation")) {
                double saturation = record.getMeasurementValue();

                //low saturation alert
                if (saturation < 92) {
                    triggerAlert(new Alert(record.getPatientId(), "LowBloodSaturation", record.getTimestamp()));
                }
                //rapid saturation drop alert
                if (lastSaturation != null && (lastSaturation - saturation) >=  5 && (record.getTimestamp() - lastTimestamp)<= 600000) {
                    triggerAlert(new Alert(record.getPatientId(), "RapidBloodSaturationDrop", record.getTimestamp()));
                }
                lastSaturation = saturation;
                lastTimestamp = record.getTimestamp();
            }
        }
    }



    /**
     * Monitors blood pressure records for significant trends and critical threshold breaches.
     * It tracks consecutive increases or decreases in systolic and diastolic pressure, triggering alerts if there
     * are three successive measurements differing by more than 10 mmHg in either direction.
     * Additionally, it checks for critical blood pressure values and triggers alerts when systolic
     * pressure exceeds 180 mmHg, drops below 90 mmHg, or when diastolic pressure exceeds 120 mmHg or
     * falls below 60 mmHg.
     *
     * @param patient The patient whose blood pressure is being monitored.
     * @param records A list of patient records containing blood pressure measurements.
     */
    private static void checkBloodPressure(Patient patient, List<PatientRecord> records) {
        Double lastSystolic = null;
        Double lastDiastolic = null;
        int systolicIncreaseCount = 0;
        int systolicDecreaseCount = 0;
        int diastolicIncreaseCount = 0;
        int diastolicDecreaseCount = 0;

        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodPressure")) {

                double[] bpValues = record.getMeasurementValues();
                double systolic = bpValues[0];
                double diastolic = bpValues[1];
                // Check for systolic trends
                if (lastSystolic != null ) {
                    if (systolic - lastSystolic > 10) {
                        systolicIncreaseCount++;
                        systolicDecreaseCount = 0;
                    } else if (lastSystolic - systolic > 10) {
                        systolicDecreaseCount++;
                        systolicIncreaseCount = 0;
                    } else {
                        systolicIncreaseCount = 0;
                        systolicDecreaseCount = 0;
                    }

                    if (systolicIncreaseCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "IncreasingSystolicBloodPressureTrend", record.getTimestamp()));
                        systolicIncreaseCount = 0;  // Reset after alerting
                    }

                    if (systolicDecreaseCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "DecreasingSystolicBloodPressureTrend", record.getTimestamp()));
                        systolicDecreaseCount = 0;  // Reset after alerting
                    }
                }

                // Check for diastolic trends
                if (lastDiastolic != null) {
                    if (diastolic - lastDiastolic > 10) {
                        diastolicIncreaseCount++;
                        diastolicDecreaseCount = 0;
                    } else if (lastDiastolic - diastolic > 10) {
                        diastolicDecreaseCount++;
                        diastolicIncreaseCount = 0;
                    } else {
                        diastolicIncreaseCount = 0;
                        diastolicDecreaseCount = 0;
                    }

                    if (diastolicIncreaseCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "IncreasingDiastolicBloodPressureTrend", record.getTimestamp()));
                        diastolicIncreaseCount = 0;  // Reset after alerting
                    }

                    if (diastolicDecreaseCount >= 3) {
                        triggerAlert(new Alert(record.getPatientId(), "DecreasingDiastolicBloodPressureTrend", record.getTimestamp()));
                        diastolicDecreaseCount = 0;  // Reset after alerting
                    }
                }

                // Check for critical blood pressure thresholds
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

            protected static void triggerAlert(Alert alert) {
                alerts.add(alert);
                System.out.println("Alert triggered: " + alert.toString());
            }

        }


