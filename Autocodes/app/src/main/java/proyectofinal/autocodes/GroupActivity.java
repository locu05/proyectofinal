package proyectofinal.autocodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.adapter.ParticipantArrayAdapter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.service.TrackingService;


public class GroupActivity extends AppCompatActivity {

    TextView errorMsg;
    Context context;
    List<Participant> participantList;
    TextView groupStatus;
    Map<String,String> intentValues = new HashMap<String,String>();
    ListView listView;
    TextView textView;
    String serverBaseUrl = "http://107.170.81.44:3002";
    Button activateGroup;
    Button deactivateGroup;
    Group group;

    @Override
    protected void onResume() {
        super.onResume();
        participantList.clear();
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intentValues.put(AutocodesIntentConstants.GROUP_ID, extras.getString(AutocodesIntentConstants.GROUP_ID));
            intentValues.put(AutocodesIntentConstants.GROUP_NAME, extras.getString(AutocodesIntentConstants.GROUP_NAME));
        }
        getGroupInformation(intentValues.get(AutocodesIntentConstants.GROUP_ID));
        Log.e(LogConstants.INTENT_VALUES_DEBUG, "Setting group name: " + intentValues.get(AutocodesIntentConstants.GROUP_NAME));
        textView.setText(intentValues.get(AutocodesIntentConstants.GROUP_NAME));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setUpImageLoader();
        participantList = new ArrayList<Participant>();
        group = new Group();
        setContentView(R.layout.activity_group);
        ParticipantArrayAdapter participantAdapter = new ParticipantArrayAdapter(context, participantList);
        textView = (TextView) findViewById(R.id.groupName);
        listView = (ListView) findViewById(R.id.groupView);
        listView.setAdapter(participantAdapter);
        activateGroup = (Button) findViewById(R.id.activateGroup);
        deactivateGroup = (Button) findViewById(R.id.deactivateGroup);
        groupStatus = (TextView) findViewById(R.id.groupStatus);

        deactivateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deactivateGroup(intentValues.get(AutocodesIntentConstants.GROUP_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        activateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Participant driver = null;
                for(Participant p : participantList) {
                    if(p.isDriver()){
                        driver = p;
                    }
                }
                if(driver!=null){
                    try {
                        activateGroup(intentValues.get(AutocodesIntentConstants.GROUP_ID), driver.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debe seleccionar un conductor designado" +
                            " para activar el grupo", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void deactivateGroup(String groupId) throws JSONException, UnsupportedEncodingException {
        Log.e(LogConstants.BEHAVIOUR_LOG, "Deactivating group: " + groupId);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "Post /group/deactivate with values " + jsonRequest.toString());
        StringEntity entity = new StringEntity(jsonRequest.toString());
        client.post(getApplicationContext(), serverBaseUrl + "/group/deactivate", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LogConstants.SERVER_RESPONSE, "Status Code from /group/deactivate post " + statusCode);
                Intent intent = new Intent(context, TrackingService.class);
                stopService(intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Couldn't get specified resource", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void activateGroup(String groupId, String driverId) throws JSONException, UnsupportedEncodingException {
        Log.e(LogConstants.BEHAVIOUR_LOG, "Activating group: " + groupId + " with driver id: " + driverId);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        jsonRequest.put("driver", driverId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "Post /group/activate with values " + jsonRequest.toString());
        StringEntity entity = new StringEntity(jsonRequest.toString());
        client.post(getApplicationContext(), serverBaseUrl + "/group/activate", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LogConstants.SERVER_RESPONSE, "Status Code from /group/activate post " + statusCode);
                Intent intent = new Intent(context, TrackingService.class);
                intent.putExtra("group", group);
                startService(intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Couldn't get specified resource", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }

    public void getGroupInformation(String groupId){
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "/group with groupId: " + groupId);
        client.get(serverBaseUrl + "/group/" + groupId,null ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    Log.e(LogConstants.SERVER_RESPONSE, "" + statusCode);
                    Log.e(LogConstants.JSON_RESPONSE, obj.toString());
                    JSONArray users = obj.getJSONArray("users");
                    for(int i = 0 ; i< users.length() ; i++) {
                       JSONObject user = (JSONObject) users.get(i);
                       Participant p = new Participant(user.getString("user_fb_id"), user.getString("name"),"http://graph.facebook.com/"+user.getString("user_fb_id")+"/picture?type=large",  R.string.fontello_heart_empty);
                        if(user.getString("user_fb_id").equals(obj.getString("driver"))){
                            p.setDriver(true);
                        }
                       participantList.add(p);
                    }
                    group.setActive((obj.getString("active").equals("1"))?1:0);
                    group.setName(obj.getString("name"));
                    group.setDriverId(obj.getString("driver"));
                    group.setId(Integer.valueOf(obj.getString("group_id")));
                    groupStatus.setText((group.getActive()==1)?"Activo":"Inactivo");

                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                    if(group.getActive()==1){
                        activateGroup.setVisibility(View.INVISIBLE);
                        deactivateGroup.setVisibility(View.VISIBLE);
                    } else {
                        activateGroup.setVisibility(View.VISIBLE);
                        deactivateGroup.setVisibility(View.INVISIBLE);
                    }



                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
}}
