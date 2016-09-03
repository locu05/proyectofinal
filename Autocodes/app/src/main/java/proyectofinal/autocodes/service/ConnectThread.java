package proyectofinal.autocodes.service;

import android.app.Notification;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import proyectofinal.autocodes.AutocodesApplication;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.constant.MessageConstants;
import proyectofinal.autocodes.model.Group;

/**
 * Created by lucas on 28/08/16.
 */
public class ConnectThread extends Thread {
    private static final int MESSAGE_LENGTH = 5;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Handler handler;
    private Group group;
    private long mRequestStartTimeUpdateDevice;
    String serverBaseUrl = "http://107.170.81.44:3002";

    public ConnectThread(BluetoothDevice device, TrackingDriverService.ServiceHandler mServiceHandler, Group group) {
        this.group = group;
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Creating thread ConnectThread...");
        handler = mServiceHandler;

        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            Log.e("BTDEV", "Except: " + e.getMessage());
        }
        mmSocket = tmp;
        Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Created thread ConnectThread");
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        //mBluetoothAdapter.cancelDiscovery();
        Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Running thread ConnectThread");

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Device successfully connected!");
            notifyDeviceConnectedToServer(1);
        } catch (IOException connectException) {
            Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Error connecting to the device: " + connectException.getMessage());
            notifyDeviceConnectedToServer(0);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Error closing the socket?: " + closeException.getMessage());
            }
            Message message = handler.obtainMessage(MessageConstants.ERROR_CONNECTING_DEVICE, connectException);
            message.sendToTarget();
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    private void notifyDeviceConnectedToServer(int i) {
        try {
            mRequestStartTimeUpdateDevice = System.currentTimeMillis();
            JSONObject obj = new JSONObject();
            obj.put("groupid", group.getId());
            obj.put("bracelet_status", i);
            Log.e(LogConstants.PREPARING_REQUEST, "Rest call /group/braceletStatus " + obj.toString());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, serverBaseUrl + "/group/braceletStatus", obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateDevice;
                            Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            Log.e(LogConstants.SERVER_RESPONSE, "/group/braceletStatus onResponse");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateDevice;
                            Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            if(error!=null){
                                Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                            }
                        }
                    });

            AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void manageConnectedSocket(final BluetoothSocket mmSocket) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                int i = 0;
                    try {
                        InputStream is = mmSocket.getInputStream();

                        BufferedReader r = new BufferedReader(new InputStreamReader(mmSocket.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        String str = null;
                        while ((str = r.readLine()) != null) {
                            if(str.startsWith("T")) {

                            } else if (str.startsWith("P")) {

                            } else if (str.startsWith("A")) {

                            } else {
                                if(DeviceDataHolder.getInstance().getTrash().size()==999) {
                                    DeviceDataHolder.getInstance().getTrash().clear();
                                    DeviceDataHolder.getInstance().getTrash().add(str);
                                } else {
                                    DeviceDataHolder.getInstance().getTrash().add(str);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                cancel();
            }
        });
        t1.start();
    }

    public void cancel() {
        try {
            Log.e(LogConstants.BLUETOOTH_CONNECTION_THREAD, "Closing bluetooth connection");
            mmSocket.close();
        } catch (IOException e) { }
    }
}