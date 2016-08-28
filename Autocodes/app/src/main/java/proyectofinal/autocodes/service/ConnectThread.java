package proyectofinal.autocodes.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import proyectofinal.autocodes.constant.LogConstants;

/**
 * Created by lucas on 28/08/16.
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        Log.e("BTDEV", "STARTED CREATING THREAD");

        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("DEADBEEF-0000-0000-0000-000000001234"));
        } catch (IOException e) {
            Log.e("BTDEV", "Except: " + e.getMessage());
        }
        mmSocket = tmp;
        Log.e("BTDEV", "CREATEDTHREAD");
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        //mBluetoothAdapter.cancelDiscovery();
        Log.e("BTDEV", "RUNNING");

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            Log.e("BTDEV", "CONNECTED");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
        Log.e("BTDEV", "FINISHED");
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}