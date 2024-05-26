package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketDataReader implements DataReader {
    public WebSocketClient client;
    public DataStorage dataStorage;

    public WebSocketDataReader(String serverUri) throws URISyntaxException {
        this.client = new WebSocketClient(new URI(serverUri)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Connected to WebSocket server");
            }

            @Override
            public void onMessage(String message) {
                processData(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
    }
    @Override
    public void readData(DataStorage storage) throws IOException {
        this.dataStorage = storage;
        client.connect();
    }

    @Override
    public void stopReading() {
        if (client != null && client.isOpen()) {
            client.close();
        }
    }

    private void processData(String message) {
        String[] data = message.split(",");
        int patientId = Integer.parseInt(data[0]);
        long timestamp = Long.parseLong(data[1]);
        String label = data[2];
        double value = Double.parseDouble(data[3]);

        dataStorage.addPatientData(patientId, value, label, timestamp);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        DataStorage dataStorage = new DataStorage();
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080");
        reader.readData(dataStorage);

    }
}

