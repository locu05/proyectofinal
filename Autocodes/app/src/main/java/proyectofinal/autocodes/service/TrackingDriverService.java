package proyectofinal.autocodes.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import com.facebook.AccessToken;

import org.json.JSONObject;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.DriverConfirmDeviceActivity;
import proyectofinal.autocodes.DriverStatusActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;


public class TrackingDriverService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;
    Group group;
    // Binder given to clients
    private final IBinder mBinder = new TrackingDriverBinder();
    LocalBroadcastManager broadcaster;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TrackingDriverBinder extends Binder {
        TrackingDriverService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingDriverService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i(LogConstants.LOG_TAG, "Tracking Driver Service created");
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        // To avoid cpu-blocking, we create a background handler to run our service
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
                if(true) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_directions_car)
                                    .setContentTitle(group.getName())
                                    .setContentText("Pulsera - OK");

                    startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());

                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = mServiceHandler.obtainMessage();
                    message.arg1 = startId;
                    mServiceHandler.sendMessage(message);

                    return START_STICKY;
                }

            }
            }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogConstants.LOG_TAG, "Tracking Driver Service Destroyed");
    }
    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Log.e(LogConstants.TRACKING_DRIVER_SERVICE, "Tracking Device...");
                while(true) {
                    Thread.sleep(5000);
                    Log.i(LogConstants.TRACKING_DRIVER_SERVICE, "Receiving bluetooth data...");
                }

            }catch (Exception e) {
            }
        }

        public void sendResult(Group message) {
//            Intent intent = new Intent("ActiveGroup");
//            if(message != null)
//                intent.putExtra("ActiveGroupMessage", message);
//            broadcaster.sendBroadcast(intent);
        }


    }

}