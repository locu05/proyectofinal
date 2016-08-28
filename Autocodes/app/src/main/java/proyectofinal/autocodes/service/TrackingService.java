package proyectofinal.autocodes.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import proyectofinal.autocodes.DriverStatusActivity;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;


public class TrackingService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;
    Group group;

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
            }

        return super.onStartCommand(intent, flags, startId);
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