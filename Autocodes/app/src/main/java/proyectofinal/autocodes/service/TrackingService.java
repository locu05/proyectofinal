package proyectofinal.autocodes.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import proyectofinal.autocodes.DriverStatusActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;


public class TrackingService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;
    Group group;
    String serverBaseUrl = "http://107.170.81.44:3002";

    @Override
    public void onCreate() {
        Log.i(LogConstants.LOG_TAG, "Tracking Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mRunning) {
            mRunning = true;
            if(group==null || group.getId()!=((Group) intent.getSerializableExtra("group")).getId()) {
                group = (Group) intent.getSerializableExtra("group");
                Intent resultIntent = new Intent(this, DriverStatusActivity.class);
                resultIntent.putExtra("GroupId", group.getId());

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


                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return START_STICKY;
            }
            }

        return super.onStartCommand(intent, flags, startId);
    }


    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(LogConstants.TRACKING_SERVICE, "Getting driver status...");
            while(true) {
                try {
                    Thread.sleep(10000);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, serverBaseUrl + "/group/" + group.getId(), null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.e(LogConstants.JSON_RESPONSE, "/user " + response.toString());
                                        //group.setActive((Integer) response.get("active"));

                                    } catch (Exception e) {
                                        Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Error fetching the active groups: " + e.getMessage());
                                    }


                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
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


                } catch (InterruptedException e) {
                    Log.e("ServiceError", "InterruptedException");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogConstants.LOG_TAG, "Tracking Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}