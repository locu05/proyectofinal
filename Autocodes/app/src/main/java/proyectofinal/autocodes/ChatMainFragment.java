package proyectofinal.autocodes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;


/**
 * A chat fragment containing messages view and input form.
 */
public class ChatMainFragment extends Fragment {



    private static RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private static List<Message> mMessages;
    private static List<Message> listaMensajes;
    static LocalBroadcastManager broadcaster;
    private static RecyclerView.Adapter mAdapter;
    //private boolean mTyping = false;
   // private Handler mTypingHandler = new Handler();
    private String mUsername;
    private String mUserGroup;
    private Socket mSocket;
    private static final String TAG = "Bocajuniors";
    private Intent intentServicio = null;
    private Boolean isConnected = true;
    private static String mLastState;
    private static Context contextoActual;


    public ChatMainFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
     Log.d(TAG, "pase por onattach");
        //super.onAttach(activity);
        super.onAttach(context);


        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        Type type = new TypeToken<List<Message>>(){}.getType();
        Gson gson = new Gson();
        String jsonMsg = appSharedPrefs.getString("ObjetoListaMensajes", "");
        listaMensajes = gson.fromJson(jsonMsg, type);

//        Log.d("Boca Juniors", "pase por onattach");
        mAdapter = new ChatMessageAdapter(context, listaMensajes);


       /* Activity a;
        if (context instanceof Activity) {
            a = (Activity) context;
            mAdapter = new ChatMessageAdapter(context, mMessages);
        } */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);


        AutocodesApplication app = (AutocodesApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        listaMensajes = new ArrayList<>();
        mMessages = new ArrayList<>();


       /* mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("chat message", onNewMessage);*/
       // mSocket.on("user joined", onUserJoined);
        //mSocket.on("user left", onUserLeft);
        //mSocket.on("typing", onTyping);
       // mSocket.on("stop typing", onStopTyping);
        //mSocket.connect();


        mUserGroup = ((ChatGroupActivity)getActivity()).intentValues.get(AutocodesIntentConstants.GROUP_ID);
        mUsername =  ((ChatGroupActivity)getActivity()).intentValues.get(AutocodesIntentConstants.USER_NAME);

        Log.d(LogConstants.CHAT_ACTIVITY, "ChatMainFragment: el usuario es: " + mUsername + "y el grupo es: " + mUserGroup);





        filter = new IntentFilter("my-event");
        broadcaster = LocalBroadcastManager.getInstance(this.getActivity());
        broadcaster.unregisterReceiver(mMessageReceiver);
        intentServicio = new Intent(getActivity().getApplicationContext(), ServiceMessage.class);
        getActivity().stopService(intentServicio);

        contextoActual = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat_main, container, false);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       /*comente msocket.disconnect y msocket.off chat message     */
        //mSocket.disconnect();

       mSocket.off("chat message", onNewMessage);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        /*mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
       mUserGroup = data.getStringExtra("userGroup");

       addLog(getResources().getString(R.string.message_welcome));

        //addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


   /* private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }*/

    public void addMessage(String username, String message) {

        Message unMensaje = new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build();
        mMessages.add(unMensaje);
        /*Obtengo SharedPreference*/
        //Context context = getActivity().getApplicationContext();

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(contextoActual);
        Type type = new TypeToken<List<Message>>(){}.getType();
        Gson gson = new Gson();
        String jsonMsg = appSharedPrefs.getString("ObjetoListaMensajes", "");
        listaMensajes = gson.fromJson(jsonMsg, type);
        /*Agrego Mensaje*/
        //listaMensajes.add(unMensaje);
        if(listaMensajes == null) {
            listaMensajes = new ArrayList<Message>();
        }
        agregarMensajeLista(listaMensajes,unMensaje);
//        Log.d("Boca Juniors", "la cantidad de mensajes es "+ listaMensajes.size());
        mAdapter = new ChatMessageAdapter(contextoActual, listaMensajes);

        mAdapter.notifyItemInserted(listaMensajes.size() - 1);
        mAdapter.notifyDataSetChanged();
        mMessagesView.invalidate();
        /*Guardo en sharedPreference*/
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        //Gson gson = new Gson();
        String json = gson.toJson(listaMensajes);
        //prefsEditor.remove("ObjetoListaMensajes");
        prefsEditor.putString("ObjetoListaMensajes", json);

        prefsEditor.commit();

        mMessagesView.setAdapter(mAdapter);



        //String jsonMsg = appSharedPrefs.getString("ObjetoListaMensajes", "");
        scrollToBottom();



    }

    static private IntentFilter filter;

    @Override
    public void onResume() {
        super.onResume();
        // Unregister since the activity is  visible
        mSocket.on("chat message", onNewMessage);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        intentServicio = new Intent(getActivity().getApplicationContext(), ServiceMessage.class);
        getActivity().stopService(intentServicio);
        broadcaster.unregisterReceiver(mMessageReceiver);
        scrollToBottom();


    }



    public void onPause() {
        super.onPause();
        broadcaster.registerReceiver(mMessageReceiver,
                filter);
        Intent intentServicio = new Intent(getActivity().getApplicationContext(), ServiceMessage.class);
        intentServicio.putExtra("usuario",mUsername);
        intentServicio.putExtra("grupo", mUserGroup);
        getActivity().startService(intentServicio);

        mSocket.off("chat message", onNewMessage);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);



    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
        String state =intent.getStringExtra("estadoActual");


            String message = intent.getStringExtra("message");
            String username = intent.getStringExtra("username");
            if (!state.equals(mLastState) ) {
                addMessage(username, message);
                mLastState = state;
                Log.d("receiver", "Got message: " + message);
            }

        }
    };



    /*private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }*/

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        //mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");

        // perform the sending message attempt.
        mSocket.emit("chat message", message, mUserGroup, mUsername);
    }



    /*private void startSignIn() {
        mUsername = null;
        Intent intent = new Intent(getActivity(), ChatLoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }
*/
    /*private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }*/

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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


//                    Log.d("Boca Juniors", "ListenerChatMain: recibi mensaje");
                    addMessage(username, message);

                }
            });
        }
    };

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

   public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);
      if (savedInstanceState != null){
//          Log.d("Boca Juniors", "entre en restore y sinstate no es null");

      }
    }

    private void agregarMensajeLista ( List<Message> listaDeMensajes, Message unMensaje){
        if (listaDeMensajes.size() >= 50 ){
            listaDeMensajes.remove(0);
        }
        listaDeMensajes.add(unMensaje);

    }



}

