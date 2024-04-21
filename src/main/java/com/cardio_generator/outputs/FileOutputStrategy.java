package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// Corrected class name to UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    // Change variable name to lowerCamelCase and private visibility
    private String baseDirectory;

    // Changed to private visibility and corrected naming to UPPER_CASE for constants
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    // Constructor name change to match class name and parameter to lowerCamelCase
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory, variable name changed to lowerCamelCase
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        // change variable name to lowerCamelCase
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            // More specific exception handling could be implemented here if desired
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
