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
    private ArrayBlockingQueue quantumList;
    private ArrayBlockingQueue trash;
    private Integer groupId;
    private boolean activeAlcoholTest;

    public DeviceDataHolder() {
        pulseList = new ArrayBlockingQueue<String>(1000);
        quantumList = new ArrayBlockingQueue<String>(1000);
        temperatureList = new ArrayBlockingQueue<String>(1000);
        alcoholList = new ArrayBlockingQueue<String>(1000);
        trash = new ArrayBlockingQueue<String>(1000);
        activeAlcoholTest = false;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public ArrayBlockingQueue getQuantumList() {
        return quantumList;
    }

    public void setQuantumList(ArrayBlockingQueue quantumList) {
        this.quantumList = quantumList;
    }

    public boolean isActiveAlcoholTest() {
        return activeAlcoholTest;
    }

    public void setActiveAlcoholTest(boolean activeAlcoholTest) {
        this.activeAlcoholTest = activeAlcoholTest;
    }
}
