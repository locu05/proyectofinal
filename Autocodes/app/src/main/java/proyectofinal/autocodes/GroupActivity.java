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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.adapter.ParticipantArrayAdapter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.service.TrackingDriverService;
import proyectofinal.autocodes.service.TrackingService;


public class GroupActivity extends AppCompatActivity {

    private long mRequestStartTimeDeactivateGroup;
    private long mRequestStartTimeActivateGroup;
    private long mRequestStartTimeGroupInformation;
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
    Button driverStatusBtn;
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
        driverStatusBtn = (Button) findViewById(R.id.viewDriverStatusBtn);

        driverStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent k = new Intent(context, DriverStatusActivity.class);
                k.putExtra("GroupId", Integer.valueOf(intentValues.get(AutocodesIntentConstants.GROUP_ID)));
                startActivity(k);
            }
        });


        deactivateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deactivateGroup(intentValues.get(AutocodesIntentConstants.GROUP_ID), deactivateGroup);
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
                        activateGroup(intentValues.get(AutocodesIntentConstants.GROUP_ID), driver.getId(), activateGroup);
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

    private void deactivateGroup(String groupId, final Button deactivateGroup) throws JSONException, UnsupportedEncodingException {
        mRequestStartTimeDeactivateGroup = System.currentTimeMillis();
        Log.e(LogConstants.BEHAVIOUR_LOG, "Deactivating group: " + groupId);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "Post /group/deactivate with values " + jsonRequest.toString());
        deactivateGroup.setEnabled(false);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, serverBaseUrl + "/group/deactivate", jsonRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeDeactivateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "/group/deactivate post onResponse");
                        Intent intent = new Intent(context, TrackingService.class);
                        Intent intentDriver = new Intent(context, TrackingDriverService.class);
                        deactivateGroup.setEnabled(true);
                        stopService(intent);
                        stopService(intentDriver);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeDeactivateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                        deactivateGroup.setEnabled(true);

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
    }

    private void activateGroup(String groupId, String driverId,final Button activateGroup) throws JSONException, UnsupportedEncodingException {
        mRequestStartTimeActivateGroup = System.currentTimeMillis();
        Log.e(LogConstants.BEHAVIOUR_LOG, "Activating group: " + groupId + " with driver id: " + driverId);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        jsonRequest.put("driver", driverId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "Post /group/activate with values " + jsonRequest.toString());
        activateGroup.setEnabled(false);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, serverBaseUrl + "/group/activate", jsonRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeActivateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "/group/activate post " + response.toString());
                        Iterator<String> keys =  response.keys();
                        while (keys.hasNext()){
                           if(keys.next().equals("error")){
                               Toast.makeText(getApplicationContext(), "Error activando el grupo, " +
                                       "los usuarios actuales se encuentran activos" +
                                       "en otro grupo ",Toast.LENGTH_LONG).show();
                           }
                        }
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeActivateGroup;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                        activateGroup.setEnabled(true);

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
    }
    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }

    public void getGroupInformation(String groupId){
        mRequestStartTimeGroupInformation = System.currentTimeMillis();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e(LogConstants.PREPARING_REQUEST, "/group with groupId: " + groupId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, serverBaseUrl + "/group/" + groupId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroupInformation;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        try {
                            JSONObject obj = response;
                            Log.e(LogConstants.SERVER_RESPONSE, "/group get onResponse");
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
                                driverStatusBtn.setVisibility(View.VISIBLE);
                            } else {
                                activateGroup.setVisibility(View.VISIBLE);
                                deactivateGroup.setVisibility(View.INVISIBLE);
                                driverStatusBtn.setVisibility(View.INVISIBLE);
                            }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroupInformation;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
}}
