import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.getInstance().runDataStorage(); // Assuming a runDataStorage method is needed
        } else {
            HealthDataSimulator.getInstance().runSimulation(args);
        }
    }
}
