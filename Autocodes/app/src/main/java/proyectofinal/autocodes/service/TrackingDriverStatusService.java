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
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by locu on 2/9/16.
 */
public class TrackingDriverStatusService extends Service {

    String serverBaseUrl = "http://107.170.81.44:3002";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;
    private boolean mRunning;
    private long mRequestStartTimeGetActiveGroup;
    private String groupId;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        HandlerThread thread = new HandlerThread("TrackingDriverStatusService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            Log.i(LogConstants.TRACKING_DRIVER_STATUS_SERVICE, "Started tracking driver status service");
            groupId = (String) intent.getStringExtra(AutocodesIntentConstants.GROUP_ID);
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

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i(LogConstants.TRACKING_DRIVER_STATUS_SERVICE, "Fetching driver status...");
                while(true) {
                    Thread.sleep(10000);
                    Log.e(LogConstants.TRACKING_DRIVER_STATUS_SERVICE, "Calling: //" + groupId);
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, serverBaseUrl + "//" + groupId, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        //ACA LANZAR LO DE LA NOTIFICACION SI EL ALCOHOL ESTA SARPADO
                                        //
                                    } catch (Exception e) {

                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGetActiveGroup;
                                    Log.i(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                                    if(error.networkResponse!=null){
                                        Log.i(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));
                                    } else {
                                        Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.getMessage()));
                                    }
                                }
                            });

                    AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
                }
            } catch (Exception e) {
                Log.e(LogConstants.TRACKING_DRIVER_STATUS_SERVICE, e.getMessage());
            }

        }

        public void sendResult(Group message) {
//            Intent intent = new Intent("ActiveGroup");
//            if(message != null)
//                intent.putExtra("ActiveGroupMessage", message);
//            broadcaster.sendBroadcast(intent);
        }

}}
