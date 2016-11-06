package proyectofinal.autocodes.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverStatusActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.util.ImageUtil;


public class TrackingService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;
    Group group;
    String serverBaseUrl = "http://107.170.81.44:3002";
    LocalBroadcastManager broadcaster;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private final IBinder mBinder = new TrackingBinder();

    @Override
    public void onCreate() {
        Log.i(LogConstants.LOG_TAG, "Tracking Service created");
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        HandlerThread thread = new HandlerThread("TrackingDriverService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mRunning) {
            mRunning = true;
            if(group==null || group.getId()!=((Group) intent.getSerializableExtra("group")).getId()) {
                group = (Group) intent.getSerializableExtra("group");
                Intent resultIntent = new Intent(this, DriverStatusActivity.class);
                //resultIntent.putExtra("GroupId", group.getId());

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(DriverStatusActivity.class);


                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_directions_car)
                                .setContentTitle(group.getName())
                                .setContentIntent(resultPendingIntent)
                                .setContentText("Click para ver estado del conductor");

                startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());

                Message message = mServiceHandler.obtainMessage();
                message.arg1 = startId;
                mServiceHandler.sendMessage(message);

                return START_STICKY;
            }
            }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TrackingBinder extends Binder {
        TrackingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingService.this;
        }
    }
    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }
        public double previousAlcoholMeasure = 0d;
        @Override
        public void handleMessage(Message msg) {
            while(mRunning) {
                Log.i(LogConstants.TRACKING_SERVICE, "Getting driver status...");
                try {
                    Thread.sleep(1000);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, serverBaseUrl + "/group/" + group.getId(), null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d(LogConstants.TRACKING_SERVICE, "/group " + response.toString());
                                        //group.setActive((Integer) response.get("active"));
                                        Intent intent = new Intent("proyectofinal.autocodes.service.ACTIVE_GROUP_STATUS");
                                        Group activeGroup = new Group();
                                        activeGroup.setActive((response.getString("active").equals("1"))?1:0);
                                        activeGroup.setName(response.getString("name"));
                                        activeGroup.setDriverId(response.getString("driver"));
                                        activeGroup.setId(Integer.valueOf(response.getString("group_id")));
                                        //TODO chequear que este get anda, ya que envia un uno, no un true
                                        activeGroup.setBraceletConnected(response.getInt("bracelet_connected"));
                                        if(response.has("driver_bac")) {
                                            activeGroup.setDriverBac(response.getDouble("driver_bac"));
                                            //TODO: Re cabeza papaaaa
                                            if(response.getDouble("driver_bac") >= 0.5d && previousAlcoholMeasure < 0.5d){
                                                NotificationCompat.Builder mBuilder =
                                                        new NotificationCompat.Builder(getApplicationContext())
                                                                .setSmallIcon(R.drawable.car_icon)
                                                                .setContentTitle("Autocodes - Alerta")
                                                                .setContentText("El conductor no se encuentra apto");

                                                int mNotificationId = 001;
                                                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                                            }
                                            previousAlcoholMeasure = response.getDouble("driver_bac");
                                        }
                                        JSONArray users = response.getJSONArray("users");
                                        for(int i = 0 ; i< users.length() ; i++) {
                                            JSONObject user = (JSONObject) users.get(i);
                                            if(user.getString("user_fb_id").equals(response.getString("driver"))){
                                                activeGroup.setDriverName(user.getString("name"));
                                                activeGroup.setDriverAvatar("http://graph.facebook.com/"+user.getString("user_fb_id")+"/picture?type=large");
                                            }
                                        }
                                        intent.putExtra("GROUP_STATUS", activeGroup);
                                        broadcaster.sendBroadcast(intent);
                                    } catch (Exception e) {
                                        Log.e(LogConstants.TRACKING_SERVICE, "Error fetching active group status: " + e.getMessage());
                                    }


                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error.networkResponse!=null){
                                        Log.e(LogConstants.TRACKING_SERVICE, "Couldnt get active group status, err: " + String.valueOf(error.networkResponse.statusCode));
                                        Intent intent = new Intent(getApplicationContext(), TrackingService.class);
                                        stopService(intent);
                                    } else {
                                        Log.e(LogConstants.TRACKING_SERVICE, "Couldnt get active group status, err: " + String.valueOf(error.getMessage()));
                                    }
                                }
                            });
                    AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
                } catch (InterruptedException e) {
                    Log.e(LogConstants.TRACKING_SERVICE, "InterruptedException");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRunning = false;
        Log.i(LogConstants.LOG_TAG, "Tracking Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}