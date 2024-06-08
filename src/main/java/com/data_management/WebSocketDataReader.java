package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class WebSocketDataReader implements DataReader {

    private final WebSocketClient webSocketClient;
    private final DataStorage dataStorage;

    // Constructor to initialize WebSocketDataReader with server URI and DataStorage
    public WebSocketDataReader(String serverUri, DataStorage dataStorage) throws URISyntaxException {
        this.dataStorage = dataStorage;

        // Initialize WebSocketClient with the provided server URI
        this.webSocketClient = new WebSocketClient(new URI(serverUri)) {
            // Callback method invoked when the WebSocket connection is established
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Connected to WebSocket server");
            }
            // Callback method invoked when a message is received from the WebSocket server
            @Override
            public void onMessage(String message) {
                processMessage(message);
            }
            // Callback method invoked when the WebSocket connection is closed
            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Disconnected from WebSocket server");
            }
            // Callback method invoked when an error occurs in the WebSocket connection
            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
    }

    // this method is used to read the data from the WebSocket server
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.webSocketClient.connect();
    }

    // this method processes the message received from the WebSocket server
    public void processMessage(String message) {
        // Assuming the message format is: patientId,timestamp,label,data
        String[] parts = message.split(",");
        if (parts.length != 4) {
            System.err.println("Invalid message format: " + message);
            return;
        }
        try {
            int patientId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            String recordType = parts[2];
            double measurementValue = Double.parseDouble(parts[3]);

            // Store the data in DataStorage, such as dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing message: " + message);
            e.printStackTrace();
        }
    }
}