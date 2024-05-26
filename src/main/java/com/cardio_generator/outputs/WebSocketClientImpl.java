package com.cardio_generator.outputs;

import com.data_management.DataStorage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketClientImpl extends WebSocketClient {
    private DataStorage dataStorage;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientImpl.class);


    public WebSocketClientImpl(URI serverUri, DataStorage dataStorage) throws URISyntaxException {
        super(serverUri);
        this.dataStorage = dataStorage;
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
    private void processData(String message) {
        // Parse the message and store it in DataStorage
        String[] data = message.split(",");
        int patientId = Integer.parseInt(data[0]);
        long timestamp = Long.parseLong(data[1]);
        String label = data[2];
        String value = data[3];

        dataStorage.addPatientData(patientId, timestamp, label, Long.parseLong(value));
    }
    public static void main(String[] args) throws URISyntaxException {

        URI serverUri = new URI("ws://localhost:8887");
        DataStorage dataStorage = new DataStorage();
        WebSocketClientImpl client = new WebSocketClientImpl(serverUri, dataStorage);
        client.connect();

    }
}
