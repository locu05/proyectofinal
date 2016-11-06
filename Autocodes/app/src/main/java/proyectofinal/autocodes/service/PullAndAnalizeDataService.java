package proyectofinal.autocodes.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Date;

import proyectofinal.autocodes.ActiveNotificationActivity;
import proyectofinal.autocodes.ActiveNotificationTimer;
import proyectofinal.autocodes.ActiveTestActivity;
import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverConfirmDeviceActivity;
import proyectofinal.autocodes.SettingsActivity;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;

/**
 * Created by locu on 26/8/16.
 */
public class PullAndAnalizeDataService extends Service {

    String serverBaseUrl = "http://107.170.81.44:3002";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;
    private boolean mRunning;
    private long mRequestStartTimeUpdateBac;
    private int countPulse;
    private int countTemperature;
    private int groupId;
    private static DataAnalizer dataAnalizer;

    public static DataAnalizer getDataAnalizer() {
        return dataAnalizer;
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("PullAndAnalizeDataService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mRunning = false;
        dataAnalizer = new DataAnalizer();
        ActiveTestTimer.setTime(System.currentTimeMillis());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            countPulse = 0;
            countTemperature = 0;
            Log.i(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Started pull and analize service");
            Message message = mServiceHandler.obtainMessage();
            message.arg1 = startId;
            mServiceHandler.sendMessage(message);
            groupId = intent.getIntExtra(AutocodesIntentConstants.GROUP_ID, 0);
            return START_NOT_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                while(true) {
                    pollTrash();
                    pollPulse();
                    pollTemperature();
                    pollAlcohol();
                    handleEvents();
                    Thread.sleep(200);
                }

            } catch (Exception e) {
                Log.e(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, e.getMessage());
            }

        }

}

    private void pollAlcohol() {
        String pulledValue = (String) DeviceDataHolder.getInstance().getAlcoholList().poll();
        if(pulledValue!=null){
            if(DeviceDataHolder.getInstance().isActiveAlcoholTest()) {
                Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Alcohol Polled: " + pulledValue);
                dataAnalizer.addAlcohol(Integer.valueOf(pulledValue));
            }
        }
    }

    private void handleEvents() {
        //MOCK
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean mockBluetooth = sharedPref.getBoolean(SettingsActivity.MOCK_BLUETOOTH, false);
        if(mockBluetooth) {
        if(countTemperature > 19 && countPulse > 19) {
            countTemperature = 0;
            countPulse = 0;
            Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Handling pulse and temperature events");
            if (!ActiveTestActivity.running) {
                Intent activeTestActivity = new Intent(getApplicationContext(), ActiveTestActivity.class);
                activeTestActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activeTestActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getApplicationContext().startActivity(activeTestActivity);
            }
        }}
        //////////////
        else {
            if(dataAnalizer.getPulseEventCount() > Integer.valueOf(sharedPref.getString(SettingsActivity.ARRITMIA_COUNT, "40")) && dataAnalizer.isTemperatureEvent()) {
                Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Handling pulse and temperature events with" +
                        " arritmia count = " + sharedPref.getString(SettingsActivity.ARRITMIA_COUNT, "40"));
                if(ActiveTestTimer.isOk(System.currentTimeMillis())) {
                    if(!ActiveTestActivity.running) {
                        Intent activeTestActivity = new Intent(getApplicationContext(), ActiveTestActivity.class);
                        activeTestActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activeTestActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        getApplicationContext().startActivity(activeTestActivity);
                        dataAnalizer.setPulseEventCount(0);
                        dataAnalizer.setTemperatureEvent(false);
                        ActiveTestTimer.setTime(System.currentTimeMillis());
                    }
                }

            }
        }

//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//            Boolean mockBluetooth = sharedPref.getBoolean(SettingsActivity.MOCK_BLUETOOTH, false);
//            if(mockBluetooth) {
//                callService(sharedPref.getString(SettingsActivity.BAC_VALUE, "0.0"));
//                if(Double.valueOf(sharedPref.getString(SettingsActivity.BAC_VALUE, "0.0")) > 0.5d){
//                    if(!ActiveNotificationActivity.running) {
//                        if(isMyServiceRunning(TrackingDriverService.class)) {
//                            if(ActiveNotificationTimer.getInstance().getElapsedTime() > ActiveNotificationTimer.TIME_TO_ALLOW_NOTIFICACION) {
//                                ActiveNotificationTimer.getInstance().setStartTime();
//                                ActiveNotificationTimer.getInstance().setElapsedTime();
//                                Intent intentActiveNotification = new Intent(getApplicationContext(), ActiveNotificationActivity.class);
//                                intentActiveNotification.putExtra(AutocodesIntentConstants.GROUP_ID, String.valueOf(groupId));
//                                intentActiveNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intentActiveNotification.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                getApplicationContext().startActivity(intentActiveNotification);
//                            } else {
//                                ActiveNotificationTimer.getInstance().setElapsedTime();
//                            }
//
//                        }
//
//                    }
//                }
//            }
    }

    private void pollTemperature() throws InterruptedException {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean mockBluetooth = sharedPref.getBoolean(SettingsActivity.MOCK_BLUETOOTH, false);
        if(mockBluetooth) {
            if(sharedPref.getBoolean(SettingsActivity.SIMULATE_TEMPERATURE,false)) {
                countTemperature++;
                Thread.sleep(500);
                if(countTemperature == 20) {
                    sharedPref.edit().putBoolean(SettingsActivity.SIMULATE_TEMPERATURE, false);
                }
            }
        } else {
            String pulledValue = (String) DeviceDataHolder.getInstance().getTemperatureList().poll();
            if(pulledValue!=null){
                Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Temperature Polled: " + pulledValue);
                dataAnalizer.addTemperature(Float.valueOf(pulledValue));
            }
        }

    }

    private void pollPulse() throws InterruptedException {
        ///// MOCK
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean mockBluetooth = sharedPref.getBoolean(SettingsActivity.MOCK_BLUETOOTH, false);
        if(mockBluetooth) {
            if(sharedPref.getBoolean(SettingsActivity.SIMULATE_PULSE,false)) {
                countPulse++;
                Thread.sleep(500);
                if(countPulse == 20) {
                    sharedPref.edit().putBoolean(SettingsActivity.SIMULATE_PULSE, false);
                }
            }
        }
        ///////
        else {
            String pulledPulseValue = (String) DeviceDataHolder.getInstance().getPulseList().take();
            Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Pulse Polled: " + pulledPulseValue);
            String pulledQuantumValue = (String) DeviceDataHolder.getInstance().getQuantumList().take();
            Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Pulse Polled: " + pulledQuantumValue);
            dataAnalizer.addPulse(Integer.valueOf(pulledPulseValue), Integer.valueOf(pulledQuantumValue));
        }
    }

    private void pollTrash() {
        String pulledValue = (String) DeviceDataHolder.getInstance().getTrash().poll();
        if(pulledValue!=null){
            Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Trash Polled: " + pulledValue);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

