package proyectofinal.autocodes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;

public class ChatGroupActivity extends AppCompatActivity {

    Map<String,String> intentValues = new HashMap<String,String>();
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intentValues.put(AutocodesIntentConstants.GROUP_ID, extras.getString(AutocodesIntentConstants.GROUP_ID));
            //intentValues.put(AutocodesIntentConstants.GROUP_NAME, extras.getString(AutocodesIntentConstants.GROUP_NAME));
            //intentValues.put(AutocodesIntentConstants.USER_ID, extras.getString(AutocodesIntentConstants.USER_ID));
            intentValues.put(AutocodesIntentConstants.USER_NAME, extras.getString(AutocodesIntentConstants.USER_NAME));
        }


        //Log.i(LogConstants.CHAT_ACTIVITY,intentValues.get(AutocodesIntentConstants.USER_ID));
        Log.i(LogConstants.CHAT_ACTIVITY,intentValues.get(AutocodesIntentConstants.USER_NAME));
        Log.i(LogConstants.CHAT_ACTIVITY,intentValues.get(AutocodesIntentConstants.GROUP_ID));
        //Log.i(LogConstants.CHAT_ACTIVITY,intentValues.get(AutocodesIntentConstants.GROUP_NAME));
        //To get the values execute intentValues.get(AutocodesIntentConstants.CONSTANTE);

        AutocodesApplication app = (AutocodesApplication) getApplication();
        mSocket = app.getSocket();

        Log.d(LogConstants.CHAT_ACTIVITY, "Starting... ChatMainActivity");
        mSocket.emit("register room", intentValues.get(AutocodesIntentConstants.USER_NAME), intentValues.get(AutocodesIntentConstants.GROUP_ID));

       /* Intent intent;

        intent = new Intent(ChatGroupActivity.this, ChatMainActivity.class);

        intent.putExtra("username", intentValues.get(AutocodesIntentConstants.USER_ID));
        intent.putExtra("userGroup", intentValues.get(AutocodesIntentConstants.GROUP_ID));

        startActivity(intent);
        finish();*/

        setContentView(R.layout.activity_chat_group);



    }



    //public String getValueUsername() {
       // return mUsername;
    //}

}
