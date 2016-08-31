package proyectofinal.autocodes.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.R;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.constant.MessageConstants;
import proyectofinal.autocodes.model.Group;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;


public class TrackingDriverService extends Service {
    boolean mRunning = false;
    public static int ONGOING_NOTIFICATION_ID = 1564150;
    Group group;
    private final IBinder mBinder = new TrackingDriverBinder();
    LocalBroadcastManager broadcaster;
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private int REQUEST_ENABLE_BT = 56465131;
    private Handler mHandler;

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

    public final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what== MessageConstants.ERROR_CONNECTING_DEVICE) {
                Toast.makeText(AutocodesApplication.getInstance().getApplicationContext(), "No se puede conectar con el dispositivo", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AutocodesApplication.getInstance().getApplicationContext(), TrackingDriverService.class);
                AutocodesApplication.getInstance().getApplicationContext().stopService(intent);
            } else {
                try {
                    Log.e(LogConstants.TRACKING_DRIVER_SERVICE, "Tracking Device...");
                    BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        // Device does not support Bluetooth
                        Log.e(LogConstants.LOG_TAG, "Device does not support bluetooth");
                        Toast.makeText(getApplicationContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
                        return;
                    }
                 /*   if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }*/
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    Log.i("Paired devices", "" + pairedDevices.size());
                    // If there are paired devices
                    if (pairedDevices.size() > 0) {
                        // Loop through paired devices
                        // Add the name and address to an array adapter to show in a ListView
                        boolean foundDevice = false;
                        for(BluetoothDevice  dev: pairedDevices){
                            if(dev.getName().trim().equals("HC-06")){
                                Log.i(LogConstants.TRACKING_DRIVER_SERVICE, "Starting thread with: ");
                                Log.i(LogConstants.TRACKING_DRIVER_SERVICE, dev.getName() + "\n" + dev.getAddress());
                                ConnectThread ct = new ConnectThread(dev,mServiceHandler);
                                ct.start();
                                Intent intentPullAndAnalizeDataService = new Intent(getApplicationContext(),
                                        PullAndAnalizeDataService.class);
                                startService(intentPullAndAnalizeDataService);
                                foundDevice = true;
                            }
                        }
                        if(!foundDevice) {
                            Toast.makeText(getApplicationContext(), "No se puede encontrar el dispositivo," +
                                    " por favor asegurese de haber sincronizado el dispositivo.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), TrackingDriverService.class);
                            stopService(intent);
                        };
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

}