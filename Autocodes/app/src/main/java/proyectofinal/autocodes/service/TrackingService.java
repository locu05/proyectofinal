package proyectofinal.autocodes.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;


public class TrackingService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;


    @Override
    public void onCreate() {
        Log.i(LogConstants.LOG_TAG, "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mRunning) {
            mRunning = true;
            Log.i(LogConstants.LOG_TAG, "Received Start Foreground Intent ");
            Group group = (Group) intent.getSerializableExtra("group");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_directions_car)
                            .setContentTitle(group.getName())
                            .setContentText("Conductor - OK");

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogConstants.LOG_TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}