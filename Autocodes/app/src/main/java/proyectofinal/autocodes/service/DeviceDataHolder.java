package proyectofinal.autocodes.service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by locu on 31/8/16.
 */
public class DeviceDataHolder {

    private static DeviceDataHolder instance = null;

    public static DeviceDataHolder getInstance() {
        if(instance == null) {
            instance = new DeviceDataHolder();
        }
        return instance;
    }

    private ArrayBlockingQueue pulseList;
    private ArrayBlockingQueue temperatureList;
    private ArrayBlockingQueue alcoholList;
    private ArrayBlockingQueue trash;

    public DeviceDataHolder() {
        pulseList = new ArrayBlockingQueue<String>(1000);
        temperatureList = new ArrayBlockingQueue<String>(1000);
        alcoholList = new ArrayBlockingQueue<String>(1000);
        trash = new ArrayBlockingQueue<String>(1000);
    }

    public ArrayBlockingQueue getPulseList() {
        return pulseList;
    }

    public ArrayBlockingQueue getTemperatureList() {
        return temperatureList;
    }

    public ArrayBlockingQueue getAlcoholList() {
        return alcoholList;
    }

    public ArrayBlockingQueue getTrash() {
        return trash;
    }
}
