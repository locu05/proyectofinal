package proyectofinal.autocodes.service;

/**
 * Created by locu on 6/11/16.
 */

public class ActiveTestTimer {

    private static long time;

    public static long getTime() {
        return time;
    }

    public static void setTime(long aTime) {
        time = aTime;
    }

    public static boolean isOk(long now){
        if((now - time) > 60000){
            return true;
        } else {
            return false;
        }
    }

}
