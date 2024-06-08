package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
/**
        * WebSocketClientImpl is an implementation of the WebSocketClient.
        * It connects to a WebSocket server, receives messages, and processes them.
        * The processed data is then stored in DataStorage.
        */
public class WebSocketClientImpl extends WebSocketClient {
    private final DataStorage dataStorage;

    /**
     * Constructs a new WebSocketClientImpl.
     *
     * @param serverUri   The URI of the WebSocket server to connect to.
     * @param dataStorage The DataStorage instance to store the processed data.
     * @throws URISyntaxException If the serverUri is not a valid URI.
     */
    public WebSocketClientImpl(String serverUri, DataStorage dataStorage) throws URISyntaxException {
        super(new URI(serverUri));
        this.dataStorage = dataStorage;
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to WebSocket server");
    }

    @Override
    public void onMessage(String message) {
        try {
            processMessage(message);
        } catch (Exception e) {
            System.err.println("Error processing message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Callback method invoked when the WebSocket connection is closed.
     *
     * @param code   The status code as defined by the WebSocket protocol.
     * @param reason A string explaining why the connection is closed.
     * @param remote Indicates whether the closure was initiated by the remote host.
     */

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
    }
    /**
     * Callback method invoked when an error occurs in the WebSocket connection.
     *
     * @param e The exception thrown during the error.
     */
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    /**
     * Method to process the message received from the WebSocket server.
     * The message is expected to be in the format "patientId,timestamp,recordType,measurementValue".
     * It parses the message, validates the format, and stores the data in DataStorage.
     *
     * @param message The message received from the WebSocket server.
     */
    private void processMessage(String message) {
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

            // Store the data in DataStorage
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing message: " + message);
            e.printStackTrace();
        }
    }
}
