package proyectofinal.autocodes;

import android.util.Log;

import java.util.Date;

import proyectofinal.autocodes.constant.LogConstants;

/**
 * Created by locu on 7/9/16.
 */
public class ActiveNotificationTimer {

    private long startTime;
    private long elapsedTime;

    private static ActiveNotificationTimer instance = null;
    public static long TIME_TO_ALLOW_NOTIFICACION = 30*1000;

    public static ActiveNotificationTimer getInstance() {
        if(instance == null) {
            instance = new ActiveNotificationTimer();
            instance.setStartTime();
        }
        return instance;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setStartTime(){
        long start = (new Date()).getTime();
        Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Setting start time to: "  + start);
        startTime = start;
    }

    public void setElapsedTime() {
        long elapsed = (new Date()).getTime() - startTime;
        Log.d(LogConstants.PULL_AND_ANALIZE_DATA_SERVICE, "Updating elapsed time to: "  + elapsed/1000 + " seg");
        elapsedTime = elapsed;
    }
}
