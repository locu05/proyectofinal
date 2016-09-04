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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;

import org.json.JSONObject;

import java.util.List;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverConfirmDeviceActivity;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by locu on 26/8/16.
 */
public class FetchActiveGroupService extends Service {

    String serverBaseUrl = "http://107.170.81.44:3002";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;
    private boolean mRunning;
    private long mRequestStartTimeGetActiveGroup;
    private String userId;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("FetchActiveGroupService",
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
        if(intent == null){
            return START_NOT_STICKY;
        }
        if (!mRunning) {
            mRunning = true;
            Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Started fetch active group service");
            userId = (String) intent.getStringExtra("userId");
            // call a new service handler. The service ID can be used to identify the service
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
                Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Fetching active groups...");
                int count = 0;
                boolean groupActive = false;
                while(true) {
                    Thread.sleep(10000);
                    Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Calling: /getActiveGroup/" + userId);
                    if(!groupActive) {
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.GET, serverBaseUrl + "/getActiveGroup/" + userId, null, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGetActiveGroup;
                                            Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                                            Log.e(LogConstants.JSON_RESPONSE, "/user " + response.toString());
                                            Intent intentTrackingService = new Intent(getApplicationContext(), TrackingService.class);
                                            Group group = new Group();
                                            group.setId((Integer) response.get("group_id"));
                                            group.setName((String) response.get("name"));
                                            group.setActive((Integer) response.get("active"));
                                            group.setDriverId((String) response.get("driver_id"));
                                            intentTrackingService.putExtra("group", group);
                                            if(group.getDriverId().equals(AccessToken.getCurrentAccessToken().getUserId())) {
                                                if(!isMyServiceRunning(TrackingDriverService.class)) {
                                                    if(!DriverConfirmDeviceActivity.running) {
                                                        Intent intentDriverConfirmDevice = new Intent(getApplicationContext(), DriverConfirmDeviceActivity.class);
                                                        intentDriverConfirmDevice.putExtra("group", group);
                                                        intentDriverConfirmDevice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        getApplicationContext().startActivity(intentDriverConfirmDevice);
                                                    }
                                                }
                                            } else {
                                                startService(intentTrackingService);
                                                sendResult(group);
                                            }

                                        } catch (Exception e) {
                                            Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Error fetching the active groups: " + e.getMessage());
                                        }


                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGetActiveGroup;
                                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                                        if(error.networkResponse!=null){
                                            Log.i(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));
                                            Log.i(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "There are no active groups...");
                                            Intent intent = new Intent(getApplicationContext(), TrackingService.class);
                                            stopService(intent);
                                        } else {
                                            Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.getMessage()));
                                        }


                                    }
                                });

                        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
                    }
                }
            } catch (Exception e) {
                Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, e.getMessage());
            }

        }

        public void sendResult(Group message) {
//            Intent intent = new Intent("ActiveGroup");
//            if(message != null)
//                intent.putExtra("ActiveGroupMessage", message);
//            broadcaster.sendBroadcast(intent);
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

}}
