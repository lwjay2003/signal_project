package com.data_management;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FileDataReader implements DataReader {
    //read and parse data from file
    //store data in DataStorage class
    private final String directoryPath;//directory path of the file

    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // TODO Implement this method
        try(BufferedReader br = new BufferedReader(new FileReader(directoryPath))){
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int patineId = Integer.parseInt(parts[0]);
                    double measurementValue = Double.parseDouble(parts[1]);
                    String recordType = parts[2];
                    long timestamp = Long.parseLong(parts[3]);
                    dataStorage.addPatientData(patineId, measurementValue, recordType, timestamp);
                }else{
                    System.out.println("Invalid line: " + line);
                }

            }
        }
    }

}
