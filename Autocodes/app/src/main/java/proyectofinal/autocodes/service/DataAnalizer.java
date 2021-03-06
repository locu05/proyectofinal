package proyectofinal.autocodes.service;

import android.util.Log;

import com.google.common.collect.EvictingQueue;

import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

import proyectofinal.autocodes.constant.LogConstants;

/**
 * Created by locu on 14/9/16.
 */
public class DataAnalizer {

    public static final String TEMPERATURE_VALUE = "38.0";
    public static final double NO_ALCOHOL = 0;
    public static final double ALCOHOL_0_1 = 120;
    public static final double ALCOHOL_0_2 = 160;
    public static final double ALCOHOL_0_5 = 200;
    public static final double ALCOHOL_1_2 = 400;
    public static final double ALCOHOL_1_4 = 470;
    public static final double ALCOHOL_1_6 = 570;
    public static final int ALCOHOL_QUEUE_SIZE = 13;

    public static final int PULSE_EVENT_COUNT_MAX = 10;
    public static final String PULSE_VALUE = "8";
    public static final String QUANTUM_VALUE = "350";
    public static final int PULSE_QUEUE_SIZE = 3;
    public static final int QUANTUM_QUEUE_SIZE = 3;
    public static final double deltax = 1;
    private static NavigableMap<Double, Double> bacTable;

    private int averagePulseQuantum;

    static {
        bacTable = new TreeMap<Double, Double>();
        bacTable.put(NO_ALCOHOL, Double.valueOf(0));
        bacTable.put(ALCOHOL_0_1, Double.valueOf(0.1));
        bacTable.put(ALCOHOL_0_2, Double.valueOf(0.2));
        bacTable.put(ALCOHOL_0_5, Double.valueOf(0.5));
        bacTable.put(ALCOHOL_1_2, Double.valueOf(1.2));
        bacTable.put(ALCOHOL_1_4, Double.valueOf(1.4));
        bacTable.put(ALCOHOL_1_6, Double.valueOf(1.6));
    }

    public static <T> T getFromQueue(Queue<T> queue, int index){
        if(index>=queue.size()){
            throw new IndexOutOfBoundsException("index="+index+",size="+queue.size());
        }
        Queue<T> queueCopy = new LinkedList<T>(queue);
        for(int i=0; i<index; i++){
            queueCopy.remove();
        }
        return queueCopy.peek();
    }

    private boolean temperatureEvent = false;
    private boolean pulseEvent = false ;
    private int pulseEventCount = 0;
    private double bac = 0;
    private EvictingQueue<Integer> pulseQueue;
    private EvictingQueue<Integer> quantumQueue;
    private EvictingQueue<Integer> alcoholQueue;

    public DataAnalizer() {
        quantumQueue = EvictingQueue.create(QUANTUM_QUEUE_SIZE);
        pulseQueue = EvictingQueue.create(PULSE_QUEUE_SIZE);
        alcoholQueue = EvictingQueue.create(ALCOHOL_QUEUE_SIZE);
    }

    public void addTemperature(Float temp) {
        if(temp > Float.valueOf(TEMPERATURE_VALUE)) {
            Log.d(LogConstants.ALGORITHM, "Temparature detected!");
            temperatureEvent = true;
        }
    }

    /**
     * In order to call this class, you should make sure that
     * the values passed as parameters are valid
     * (More than NO_ALCOHOL_VALUE
     * @param alcohol
     */
    public void addAlcohol(Integer alcohol) {
        if(alcoholQueue.size()<ALCOHOL_QUEUE_SIZE){
            alcoholQueue.add(alcohol);
        } else {
            int averageValue = 0;
            for(Integer value : alcoholQueue) {
                averageValue = averageValue + value;
            }
            averageValue = averageValue/ALCOHOL_QUEUE_SIZE;
            alcoholQueue.clear();
            bac = bacTable.floorEntry(Double.valueOf(averageValue)).getValue();
            Log.d(LogConstants.ALGORITHM, "Alcohol bac calculated: " + bac);
        }

    }

    public void addPulse(int pulse, int quantum) {
        if(quantumQueue.size()<QUANTUM_QUEUE_SIZE+1){
            quantumQueue.add(quantum);
        }
        if(quantumQueue.size()==QUANTUM_QUEUE_SIZE) {
            int one = getFromQueue(quantumQueue,2);
            int two = getFromQueue(quantumQueue,1);
            int three = getFromQueue(quantumQueue, 0);
            averagePulseQuantum = (one + two + three)/QUANTUM_QUEUE_SIZE;
        }
        if(pulseQueue.size()<PULSE_QUEUE_SIZE+1) {
            pulseQueue.add(pulse);
            if(pulseQueue.size()==PULSE_QUEUE_SIZE) {
                Double firstDerivative = firstDerivative();
                Double secondDerivative = secondDerivative();
                if(Math.abs(secondDerivative)>=Double.valueOf(PULSE_VALUE)){
                    if(averagePulseQuantum < Integer.valueOf(QUANTUM_VALUE)) {
                        Log.d(LogConstants.ALGORITHM, "Arritmia Detected!");
                        pulseEventCount++;
                        Log.d(LogConstants.ALGORITHM, "Actual value of arritmias: " + pulseEventCount);
                    }
                }
            }
        }
    }

    private Double secondDerivative() {
        int fplush = getFromQueue(pulseQueue,2);
        int fh = getFromQueue(pulseQueue,1);
        int fminush = getFromQueue(pulseQueue, 0);
        double squareh = deltax*deltax;

        double secondDerivative = ((fplush - (2*fh) + fminush)/squareh);
//        System.out.println("Second Derivative is: " + secondDerivative);
        return secondDerivative;
    }

    private Double firstDerivative() {
        int fplush = getFromQueue(pulseQueue,2);
        int fminush = getFromQueue(pulseQueue,0);
        double deltaxmultipledtwo = 2*deltax;
        double firstDerivative = ((fplush- fminush)/deltaxmultipledtwo);
//        System.out.println("First Derivative is: " + firstDerivative);
        return firstDerivative;
    }

    public boolean isTemperatureEvent() {
        return temperatureEvent;
    }

    public int getPulseEventCount() {
        return pulseEventCount;
    }

    public double getBac() {
        return bac;
    }

    public void setPulseEventCount(int pulseEventCount) {
        this.pulseEventCount = pulseEventCount;
    }

    public void setTemperatureEvent(boolean temperatureEvent) {
        this.temperatureEvent = temperatureEvent;
    }
}
