package proyectofinal.autocodes.service;

import android.app.Service;
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

import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by locu on 26/8/16.
 */
public class FetchActiveGroupService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Started fetch active group service");
        String userId = (String) intent.getStringExtra("userId");
        Toast.makeText(this, "onStartCommand " + userId, Toast.LENGTH_SHORT).show();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

//    protected void showToast(final String msg){
//        //gets the main thread
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                // run this code in the main thread
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
                while(true) {
                    count++;
                    Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Api call");
                    Thread.sleep(3000);
                    if(count == 5) {
                        count=0;
                        sendResult("Se activo un grupo");
                    }
                }
            } catch (Exception e) {
                Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, e.getMessage());
            }

        }

        public void sendResult(String message) {
            Intent intent = new Intent("ActiveGroup");
            if(message != null)
                intent.putExtra("ActiveGroupMessage", message);
            broadcaster.sendBroadcast(intent);
        }
}}
