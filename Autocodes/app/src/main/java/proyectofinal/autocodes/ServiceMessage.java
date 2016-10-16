package proyectofinal.autocodes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;


/**
 * Created by hfilannino on 7/9/2016.
 */
public class ServiceMessage extends Service {

    private static final String TAG = "ServiceMessage";
    private boolean isRunning  = false;
    private Socket mSocket;
    private Emitter.Listener onNewMessage;
    private static String state="false";
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    Intent resultIntent;
    PendingIntent resultPendingIntent;
    TaskStackBuilder stackBuilder;
    private String mUserGroup;
    private String mUsername;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        //mSocket.on("chat message", onNewMessage);
        AutocodesApplication app = (AutocodesApplication) getApplication();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mSocket = app.getSocket();


        isRunning = true;

        //ArrayList<MessageParcel> testing = this.getIntent().getParcelableArrayListExtra("extraextra");
        //mBuilder.setSmallIcon(R.drawable.notification_icon);
        mBuilder.setContentTitle("Tenés un mensaje nuevo");
        mBuilder.setSmallIcon(R.drawable.car_icon);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        resultIntent = new Intent(this, ChatGroupActivity.class);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatGroupActivity.class);

// Adds the Intent that starts the Activity to the top of the stack





    }



    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
    super.onDestroy();
        isRunning = false;
        mSocket.off("chat message", onNewMessage);

        Log.i(TAG, "Service onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUsername = intent.getStringExtra("usuario");
        mUserGroup =  intent.getStringExtra("grupo");
        Log.d("Boca Juniors", " Service: usuario emisor es: "+ mUserGroup + mUsername);

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (isRunning){
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                            String groupName;
                        try {
                            username = data.getString("username");
                            message = data.getString("message");

                        } catch (JSONException e) {
                            return;
                        }

                            sendMessage(username, message);

                            resultIntent.putExtra(AutocodesIntentConstants.USER_NAME, mUsername);
                            resultIntent.putExtra(AutocodesIntentConstants.GROUP_ID, mUserGroup);
                            mBuilder.setContentText(username + ": "+ message);

                            stackBuilder.addNextIntent(resultIntent);
                            resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);

                            mNotificationManager.notify(1, mBuilder.build());

                        Log.d("Boca Juniors", "Service: recibi mensaje de: "+ username + ": "+ message);
                    }
                    }

                }).start();
            }
        };

        mSocket.on("chat message", onNewMessage);
        return Service.START_NOT_STICKY;
    }

    /*private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");


                    } catch (JSONException e) {
                        return;
                    }


                    Log.d("Boca Juniors", "recibi mensaje");
                }
            });
        }
    };*/


    // Send an Intent with an action named "my-event".
    private void sendMessage(String usuario,String mensaje) {
        // add data
        Log.d("Boca Juniors", "ServiceMessage: entro");
        Intent intent = new Intent("my-event");

        intent.putExtra("message", mensaje);
        intent.putExtra("username", usuario);
        if (state.equals("false")){
            intent.putExtra("estadoActual", state);
            state= "true";}
            else{
            intent.putExtra("estadoActual", state);
            state="false";
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }


}
