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

import org.json.JSONObject;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverConfirmDeviceActivity;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by locu on 26/8/16.
 */
public class PullAndAnalizeDataService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;
    private boolean mRunning;

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
                int i = 0;
                while(true) {
                    pollTrash(i);
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

    private void pollTrash(int i) {
        i++;
        String pulledValue = (String) DeviceDataHolder.getInstance().getTrash().poll();
        if(pulledValue!=null){
            Log.e(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Trash Polled: " + pulledValue);
        }
        if(i == 25) {
            i = 0;
            //Pegarle al server y notificar que hay bardo
        }
    }
}
