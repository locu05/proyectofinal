package proyectofinal.autocodes.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverConfirmDeviceActivity;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;

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
    private int i = 0;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            Log.e(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Started pull and analize service");
            Message message = mServiceHandler.obtainMessage();
            message.arg1 = startId;
            mServiceHandler.sendMessage(message);

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
                i = 0;
                while(true) {
                    pollTrash();
                    pollPulse();
                    pollTemperature();
                }

            } catch (Exception e) {
                Log.e(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, e.getMessage());
            }

        }

}

    private void pollTemperature() {

    }

    private void pollPulse() {
    }

    private void pollTrash() {
        String bac = "0.0";
        String pulledValue = (String) DeviceDataHolder.getInstance().getTrash().poll();
        if(pulledValue!=null){
            i++;
            Log.e(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Trash Polled: " + pulledValue + " index: " + i);
            if(i == 10) {
                bac = "0.6";
                callService(bac);
            } else if (i == 30) {
                bac = "1.5";
                callService(bac);
                i = 0;
            }

        }

        }

    private void callService(String bac) {
        Log.i("DEV", "ENTRO ACA");
        try {
            mRequestStartTimeUpdateBac = System.currentTimeMillis();
            JSONObject obj = new JSONObject();
            obj.put("groupid", DeviceDataHolder.getInstance().getGroupId());
            obj.put("bac", Float.valueOf(bac));
            Log.e(LogConstants.PREPARING_REQUEST, "Rest call /group/driverBac: " + obj.toString());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, serverBaseUrl + "/group/driverBac", obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateBac;
                            Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            Log.e(LogConstants.SERVER_RESPONSE, "/group/driverBac onResponse");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateBac;
                            Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            if(error!=null){
                                Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                            }
                        }
                    });

            AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

