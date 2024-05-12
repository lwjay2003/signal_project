package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {
    private DataStorage storage;
    @Test
    void setUp() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);
    }

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader


        // Test if two records are added
            List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
            assertEquals(2, records.size()); // Check if two records are retrieved
            assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
            assertEquals(200.0, records.get(1).getMeasurementValue()); // Validate second record
    }
    @Test
    void testGetRecordsWithNoData() {
        List<PatientRecord> records = storage.getRecords(2, 1714376789050L, 1714376789051L);
        assertTrue(records.isEmpty());
    }
    @Test
    void testAddPatientDataNewPatient() {
        storage.addPatientData(2, 150.0, "WhiteBloodCells", 1714376789052L);
        List<PatientRecord> records = storage.getRecords(2, 1714376789050L, 1714376789053L);
        assertEquals(1, records.size());
        assertEquals(150.0, records.get(0).getMeasurementValue());
    }

}
