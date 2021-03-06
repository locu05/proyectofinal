package proyectofinal.autocodes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.adapter.ParticipantAddedArrayAdapter;
import proyectofinal.autocodes.adapter.ParticipantDefaultAdapter;

import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;

public class CreateGroupActivity extends Activity implements OnClickListener {

    String serverBaseUrl = "http://107.170.81.44:3002";

    private long mRequestStartTimeCreateGroup;
    private EditText mSearchField;
    private TextView mXMark;
    private ArrayList<Participant> participantSearcheableList;
    private ArrayList<Participant> participantShowableList;
    private DynamicListView mDynamicListView;
    private Button submitGroup;
    private GridView participantsAddedListView;
    private List<Participant> participantAddedList;
    private EditText groupNameToCreate;
    BaseAdapter participantDefaultAdapter;
    ParticipantAddedArrayAdapter participantAddedArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setUpImageLoader();

        groupNameToCreate = (EditText) findViewById(R.id.groupNameToCreate);
        mSearchField = (EditText) findViewById(R.id.search_field);
        mXMark = (TextView) findViewById(R.id.search_x);
        participantsAddedListView = (GridView) findViewById(R.id.participantAddedView);
        submitGroup = (Button) findViewById(R.id.submitGroup);

        participantsAddedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                participantAddedList.get(position).setIconRes(R.string.fontello_heart_empty);
                participantAddedList.remove(position);
                ((BaseAdapter)participantsAddedListView.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
            }
        });

        participantSearcheableList = new ArrayList<Participant>();
        participantShowableList = new ArrayList<Participant>();
        participantAddedList = new ArrayList<Participant>();


        mXMark.setOnClickListener(this);

        mDynamicListView = (DynamicListView) findViewById(R.id.dynamic_listview);
        populateDynamicList();
        populateParticipantsAddedList();

        getFriendList();

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                participantShowableList.clear();
                for (Participant participant : participantSearcheableList) {
                    if (participant.getName().toLowerCase()
                            .contains(searchText.toLowerCase())) {
                        participantShowableList.add(participant);
                    }
                }

                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
            }
        });

        mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Participant participant = (Participant) adapterView.getItemAtPosition(position);
                participant.setIconRes(R.string.fontello_heart_full);
                if(!participantAddedList.contains(participant)) {
                    participantAddedList.add(participant);
                }
                ((BaseAdapter)participantsAddedListView.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
                mSearchField.setText("");
            }
        });

        submitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(participantAddedList.isEmpty()) {
                    Toast.makeText(CreateGroupActivity.this, "Debe agregar participantes al grupo " +
                            "para continuar", Toast.LENGTH_LONG).show();
                }else if("".equals(groupNameToCreate.getText().toString())) {
                    Toast.makeText(CreateGroupActivity.this, "Debe asignar un nombre al grupo " +
                            "para continuar", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        submitGroup.setEnabled(false);
                        createGroup(participantAddedList, groupNameToCreate.getText().toString(), Profile.getCurrentProfile().getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void createGroup(List<Participant> participantAddedList, String groupName, String id)
            throws JSONException, UnsupportedEncodingException {

        mRequestStartTimeCreateGroup = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        obj.put("name", groupName);
        JSONArray users = new JSONArray();
        for(Participant participant : participantAddedList) {
            users.put(participant.getId());
        }
        users.put(id);
        obj.put("users",users);
        obj.put("admin", id);
        StringEntity entity = new StringEntity(obj.toString());
        Log.e(LogConstants.PREPARING_REQUEST, "Rest call /group: " + obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, serverBaseUrl + "/group", obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeCreateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "/group delete onResponse");
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeCreateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                        submitGroup.setEnabled(true);

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);

    }

    private void populateParticipantsAddedList() {
        participantAddedArrayAdapter = new ParticipantAddedArrayAdapter(this, participantAddedList);

        GridView gridView = (GridView) findViewById(R.id.participantAddedView);
        gridView.setAdapter(participantAddedArrayAdapter);
    }

    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_x:
                mSearchField.setText(null);
                break;
        }
    }

    private void populateDynamicList() {
        participantDefaultAdapter = new ParticipantDefaultAdapter(this,
                participantShowableList, false);
        mDynamicListView.setAdapter(participantDefaultAdapter);
    }

    public void getFriendList(){
        AccessToken at = AccessToken.getCurrentAccessToken();
        GraphRequest req = GraphRequest.newMyFriendsRequest(at, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                FacebookRequestError error = response.getError();
                if(error != null){
                    String err = error.getErrorMessage();
                }
                Log.e(LogConstants.FACEBOOK_RESPONSE, response.getRawResponse());
                try {
                    JSONArray friendList = (JSONArray) objects;
                    for(int i = 0 ; i < friendList.length() ; i ++) {
                        JSONObject friend = (JSONObject) friendList.get(i);
                        Participant participant = new Participant(friend.getString("id"),friend.getString("name"),"http://graph.facebook.com/"+friend.getString("id")+"/picture",  R.string.fontello_heart_empty);
                        participantSearcheableList.add(participant);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                participantShowableList.addAll(participantSearcheableList);
                ((BaseAdapter)participantsAddedListView.getAdapter()).notifyDataSetChanged();
                ((BaseAdapter)mDynamicListView.getAdapter()).notifyDataSetChanged();
            }
        });
        req.executeAsync();
    }
}
