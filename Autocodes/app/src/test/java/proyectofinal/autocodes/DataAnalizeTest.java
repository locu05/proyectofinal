package proyectofinal.autocodes;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import proyectofinal.autocodes.service.DataAnalizer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by locu on 14/9/16.
 */
public class DataAnalizeTest {

    DataAnalizer dataAnalizer;
    private static final int PULSE_AVERAGE_COUNT = 10;

    @Before
    public void before(){
        dataAnalizer = new DataAnalizer();
    }

    @Test
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pulseQuiet20_9() throws IOException {
        URL url = getClass().getResource("/pulseQuiet20-9.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Integer pulse = null;
            Integer quantum = null;
            while ((line = br.readLine()) != null) {
                    if(line.startsWith("P") && pulse == null) {
                        pulse = Integer.valueOf(line.split(":")[1]);
                    } else if (line.startsWith("Q") && quantum == null){
                        quantum = Integer.valueOf(line.split(":")[1]);
                    }
                    if(pulse != null && quantum != null) {
                        dataAnalizer.addPulse(pulse,quantum);
                        pulse = null;
                        quantum = null;
                    }

            }
        }
        assertEquals(-1,dataAnalizer.getPulseEventCount());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pulseQuiet20_9_2() throws IOException {
        URL url = getClass().getResource("/pulseQuiet20-9-2.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Integer pulse = null;
            Integer quantum = null;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("P") && pulse == null) {
                    pulse = Integer.valueOf(line.split(":")[1]);
                } else if (line.startsWith("Q") && quantum == null){
                    quantum = Integer.valueOf(line.split(":")[1]);
                }
                if(pulse != null && quantum != null) {
                    dataAnalizer.addPulse(pulse,quantum);
                    pulse = null;
                    quantum = null;
                }

            }
        }
        assertEquals(-1,dataAnalizer.getPulseEventCount());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pulseQuiet20_9_3() throws IOException {
        URL url = getClass().getResource("/pulseQuiet20-9-3.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Integer pulse = null;
            Integer quantum = null;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("P") && pulse == null) {
                    pulse = Integer.valueOf(line.split(":")[1]);
                } else if (line.startsWith("Q") && quantum == null){
                    quantum = Integer.valueOf(line.split(":")[1]);
                }
                if(pulse != null && quantum != null) {
                    dataAnalizer.addPulse(pulse,quantum);
                    pulse = null;
                    quantum = null;
                }

            }
        }
        assertEquals(-1,dataAnalizer.getPulseEventCount());
    }


    @Test
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pulseMoved20_9_2() throws IOException {
        URL url = getClass().getResource("/pulseMoved20-9.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Integer pulse = null;
            Integer quantum = null;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("P") && pulse == null) {
                    pulse = Integer.valueOf(line.split(":")[1]);
                } else if (line.startsWith("Q") && quantum == null){
                    quantum = Integer.valueOf(line.split(":")[1]);
                }
                if(pulse != null && quantum != null) {
                    dataAnalizer.addPulse(pulse,quantum);
                    pulse = null;
                    quantum = null;
                }

            }
        }
        assertEquals(-1,dataAnalizer.getPulseEventCount());
    }

    @Test
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pulsePostMoved20_9_2() throws IOException {
        URL url = getClass().getResource("/pulsePostMoved20-9.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Integer pulse = null;
            Integer quantum = null;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("P") && pulse == null) {
                    pulse = Integer.valueOf(line.split(":")[1]);
                } else if (line.startsWith("Q") && quantum == null){
                    quantum = Integer.valueOf(line.split(":")[1]);
                }
                if(pulse != null && quantum != null) {
                    dataAnalizer.addPulse(pulse,quantum);
                    pulse = null;
                    quantum = null;
                }

            }
        }
        assertEquals(-1,dataAnalizer.getPulseEventCount());
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void temperatureAnalize() throws IOException {
        URL url = getClass().getResource("/temperatureLighter.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                dataAnalizer.addTemperature(Float.valueOf(line.split(";")[0]));
                if(i==10) {
                    assertTrue(dataAnalizer.isTemperatureEvent());
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void NoAlcoholAnalize() throws IOException {
        URL url = getClass().getResource("/noAlcohol.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(0.0,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewAlcoholAnalize() throws IOException {
        URL url = getClass().getResource("/aFewAlcohol.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(0.4,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewAlcohol9_10_Analize() throws IOException {
        URL url = getClass().getResource("/aFewAlcohol-9-10.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(0.4,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewAlcohol9_17_Analize() throws IOException {
        URL url = getClass().getResource("/aFewAlcohol-9-17.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(0.4,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewPlusAlcoholAnalize() throws IOException {
        URL url = getClass().getResource("/aFewPlusAlcohol.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(1.0,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewPlusAlcohol2Analize() throws IOException {
        URL url = getClass().getResource("/aFewPlusAlcohol2.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(1.0,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void aFewPlusAlcohol3Analize() throws IOException {
        URL url = getClass().getResource("/aFewPlusAlcohol3.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(1.0,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void HighAlcoholAnalize() throws IOException {
        URL url = getClass().getResource("/highAlcohol.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(1.5,dataAnalizer.getBac());
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Test
    public void HighAlcoholAnalize_9_17() throws IOException {
        URL url = getClass().getResource("/highAlcohol-9-17.txt");
        File file = new File(url.getPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataAnalizer.addAlcohol(Integer.valueOf(line));
            }
            assertEquals(1.5,dataAnalizer.getBac());
        }
    }
}
