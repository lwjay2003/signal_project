package alert;
import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.FileDataReader;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AlertGeneratorTest {
    private DataStorage storage;
    private AlertGenerator alertGenerator;
    private List<Alert> alerts;

    @Test
    void setUp() {
        storage = Mockito.mock(DataStorage.class);
        alerts = new ArrayList<>();

         alertGenerator = new AlertGenerator(storage) {

            @Override
            protected void triggerAlert(Alert alert) {
                alerts.add(alert);
            }
        };



        // Add mock data to the storage
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 130.0, "BloodPressure", 1714376789050L));
        records.add(new PatientRecord(1, 140.0, "BloodPressure", 1714376789060L));
        records.add(new PatientRecord(1, 150.0, "BloodPressure", 1714376789070L));
        records.add(new PatientRecord(1, 160.0, "BloodPressure", 1714376789080L));
        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);
    }
    @Test
    void testBloodPressureTrendAlerts() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        Patient patient = new Patient(1);
        alertGenerator.evaluateData(patient);

        assertEquals(1, alerts.size(), "Expected one increasing blood pressure trend alert");
        Alert alert = alerts.get(0);
        assertEquals(1, alert.getPatientId());
        assertEquals("IncreasingBloodPressureTrend", alert.getCondition());
    }
    @Test
    void testCriticalBloodPressureThresholdAlert() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 190.0, "BloodPressure", 1714376789050L));
        records.add(new PatientRecord(1, 60.0, "BloodPressure", 1714376789051L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertEquals(2, alerts.size(), "Expected two critical blood pressure threshold alerts");
        assertEquals("CriticalBloodPressureThreshold", alerts.get(0).getCondition());
        assertEquals("CriticalBloodPressureThreshold", alerts.get(1).getCondition());
    }
    @Test
    void testLowBloodSaturationAlerts() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 91.0, "BloodSaturation", 1714376789050L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);
        Patient patient = new Patient(1);

        alertGenerator.evaluateData(patient);

        assertEquals(1, alerts.size(), "Expected one low blood saturation alert");
        Alert alert = alerts.get(0);
        assertEquals(1, alert.getPatientId());
        assertEquals("LowBloodSaturation", alert.getCondition());
    }
    @Test
    void testRapidBloodSaturationDropAlerts() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 96.0, "BloodSaturation", 1714376789050L));
        records.add(new PatientRecord(1, 90.0, "BloodSaturation", 1714376789060L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertEquals(1, alerts.size(), "Expected one rapid drop in blood saturation alert");
        Alert alert = alerts.get(0);
        assertEquals(1, alert.getPatientId());
        assertEquals("RapidDropInBloodSaturation", alert.getCondition());
    }
@Test
    void testHypotensiveHypoxemiaAlert() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85.0, "BloodPressure", 1714376789050L));
        records.add(new PatientRecord(1, 91.0, "BloodSaturation", 1714376789051L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertEquals(1, alerts.size(), "Expected one hypotensive hypoxemia alert");
        Alert alert = alerts.get(0);
        assertEquals(1, alert.getPatientId());
        assertEquals("HypotensiveHypoxemiaAlert", alert.getCondition());
    }
    @Test
    void testIrregularHeartBeatAlerts() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 70.0, "HeartRate", 1714376789050L));
        records.add(new PatientRecord(1, 70.0, "HeartRate", 1714376789055L));
        records.add(new PatientRecord(1, 70.0, "HeartRate", 1714376789070L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertEquals(1, alerts.size(), "Expected one irregular heartbeat alert");
        Alert alert = alerts.get(0);
        assertEquals(1, alert.getPatientId());
        assertEquals("IrregularHeartBeat", alert.getCondition());
    }
    @Test
    void testAbnormalHeartRateAlerts() {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 45.0, "HeartRate", 1714376789050L));
        records.add(new PatientRecord(1, 110.0, "HeartRate", 1714376789051L));

        when(storage.getRecords(eq(1), anyLong(), anyLong())).thenReturn(records);

        alertGenerator.evaluateData(patient);

        assertEquals(2, alerts.size(), "Expected two abnormal heart rate alerts");
        assertEquals("AbnormalHeartRate", alerts.get(0).getCondition());
        assertEquals("AbnormalHeartRate", alerts.get(1).getCondition());
    }

}
