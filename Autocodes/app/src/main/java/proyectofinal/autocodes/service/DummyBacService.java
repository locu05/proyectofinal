package proyectofinal.autocodes.service;

import android.app.Service;
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

import org.json.JSONObject;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by locu on 26/8/16.
 */
public class DummyBacService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    LocalBroadcastManager broadcaster;
    private boolean mRunning;
    private Group group;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("DummyBacService",
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
        if (!mRunning) {
            mRunning = true;
            Log.i(LogConstants.DUMMY_BAC_SERVICE, "Started dummy bac service");
            if(group==null || group.getId()!=((Group) intent.getSerializableExtra("group")).getId()) {
                group = (Group) intent.getSerializableExtra("group");
            }
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
                DeviceDataHolder.getInstance().setGroupId(group.getId());
                while(true){
                    Thread.sleep(1000);
                    DeviceDataHolder.getInstance().getTrash().add("DummyTrash");
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
