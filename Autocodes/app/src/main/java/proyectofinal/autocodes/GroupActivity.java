package proyectofinal.autocodes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.facebook.AccessToken;
import com.facebook.Profile;
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
import proyectofinal.autocodes.service.DummyBacService;
import proyectofinal.autocodes.service.PullAndAnalizeDataService;
import proyectofinal.autocodes.service.TrackingDriverService;
import proyectofinal.autocodes.service.TrackingService;


public class GroupActivity extends AppCompatActivity {

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
    Button chat;
    Group group;
    AccessToken at2 = null;

    @Override
    protected void onResume() {
        super.onResume();
        participantList.clear();
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        Bundle extras = getIntent().getExtras();
        at2 = AccessToken.getCurrentAccessToken();
        if (extras != null) {
            intentValues.put(AutocodesIntentConstants.GROUP_ID, extras.getString(AutocodesIntentConstants.GROUP_ID));
            intentValues.put(AutocodesIntentConstants.GROUP_NAME, extras.getString(AutocodesIntentConstants.GROUP_NAME));
            intentValues.put(AutocodesIntentConstants.ADMIN_ID, extras.getString(AutocodesIntentConstants.ADMIN_ID));
        }
        getGroupInformation(intentValues.get(AutocodesIntentConstants.GROUP_ID));
        Log.d(LogConstants.INTENT_VALUES_DEBUG, "Setting group naextras.getString(AutocodesIntentConstants.ADMIN_ID)me: " + intentValues.get(AutocodesIntentConstants.GROUP_NAME));
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
        at2 = AccessToken.getCurrentAccessToken();
        String userId = at2.getUserId();
        String adminId = getIntent().getExtras().getString(AutocodesIntentConstants.ADMIN_ID);
        ParticipantArrayAdapter participantAdapter = new ParticipantArrayAdapter(context, participantList, userId.equals(adminId) );
        textView = (TextView) findViewById(R.id.groupName);
        listView = (ListView) findViewById(R.id.groupView);
        listView.setAdapter(participantAdapter);
        activateGroup = (Button) findViewById(R.id.activateGroup);
        chat = (Button) findViewById(R.id.chatGroup);
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

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatGroupActivity.class);
                Profile userProfile = Profile.getCurrentProfile();
                String fullName = userProfile.getFirstName() + " "  + userProfile.getMiddleName() + " " + userProfile.getLastName();
                intent.putExtra(AutocodesIntentConstants.USER_ID, AccessToken.getCurrentAccessToken().getUserId());
                intent.putExtra(AutocodesIntentConstants.USER_NAME, fullName);
                intent.putExtra(AutocodesIntentConstants.GROUP_ID, intentValues.get(AutocodesIntentConstants.GROUP_ID));
                intent.putExtra(AutocodesIntentConstants.GROUP_NAME, intentValues.get(AutocodesIntentConstants.GROUP_NAME));
                startActivity(intent);
            }
        });

    }

    private void deactivateGroup(String groupId, final Button deactivateGroup) throws JSONException, UnsupportedEncodingException {
        final long mRequestStartTimeDeactivateGroup = System.currentTimeMillis();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        Log.d(LogConstants.PREPARING_REQUEST, "Post /group/deactivate with values " + jsonRequest.toString());
        deactivateGroup.setEnabled(false);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, serverBaseUrl + "/group/deactivate", jsonRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeDeactivateGroup;
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.i(LogConstants.SERVER_RESPONSE, "/group/deactivate response: " + response.toString());
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
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "group/deactivate error:" + error.getMessage());
                        deactivateGroup.setEnabled(true);
                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);


        //TODO: DELETE WHEN IMPLEMENTED
        Intent intentPullAndAnalizeDataService = new Intent(getApplicationContext(),
                PullAndAnalizeDataService.class);
        stopService(intentPullAndAnalizeDataService);
        Intent intent = new Intent(getApplicationContext(), DummyBacService.class);
        intent.putExtra("group", group);
        stopService(intent);
        Intent intentTrackingDriverService = new Intent(getApplicationContext(),
                TrackingDriverService.class);
        stopService(intentTrackingDriverService);
        //TODO: DELETE WHEN IMPLEMENTED
    }

    private void activateGroup(String groupId, String driverId,final Button activateGroup) throws JSONException, UnsupportedEncodingException {
        final long mRequestStartTimeActivateGroup = System.currentTimeMillis();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("group_id", groupId);
        jsonRequest.put("driver", driverId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(LogConstants.PREPARING_REQUEST, "Post /group/activate with values " + jsonRequest.toString());
        activateGroup.setEnabled(false);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, serverBaseUrl + "/group/activate", jsonRequest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeActivateGroup;
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.i(LogConstants.SERVER_RESPONSE, "/group/activate post " + response.toString());
                        Iterator<String> keys =  response.keys();
                        while (keys.hasNext()){
                           if(keys.next().equals("error")){
                               Toast.makeText(getApplicationContext(), "Error activando el grupo, " +
                                       "Alguno de los usuarios se encuentra activo " +
                                       "en otro grupo ",Toast.LENGTH_LONG).show();
                           }
                        }
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeActivateGroup;
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, "/group/activate time"  + String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "/group/activate Error" + error.getMessage());
                        activateGroup.setEnabled(true);

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);


        //TODO: DELETE WHEN IMPLEMENTED
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean mockBluetooth = sharedPref.getBoolean(SettingsActivity.MOCK_BLUETOOTH, false);
        if(mockBluetooth) {
            Log.i(LogConstants.BEHAVIOUR_LOG, "MOCKED BLUETOOTH DEVICE!");
            Intent intentPullAndAnalizeDataService = new Intent(getApplicationContext(),
                    PullAndAnalizeDataService.class);
            intentPullAndAnalizeDataService.putExtra(AutocodesIntentConstants.GROUP_ID, Integer.valueOf(groupId));
            startService(intentPullAndAnalizeDataService);
            Intent intent = new Intent(getApplicationContext(), DummyBacService.class);
            intent.putExtra("group", group);
            startService(intent);
        }
        //TODO: DELETE WHEN IMPLEMENTED
    }
    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }

    public void getGroupInformation(String groupId){
        final long mRequestStartTimeGroupInformation = System.currentTimeMillis();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(LogConstants.PREPARING_REQUEST, "/group with groupId: " + groupId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, serverBaseUrl + "/group/" + groupId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroupInformation;
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        try {
                            JSONObject obj = response;
                            Log.i(LogConstants.SERVER_RESPONSE, "/group response: "  + obj.toString());
                            JSONArray users = obj.getJSONArray("users");

                            group.setActive((obj.getString("active").equals("1"))?1:0);
                            group.setName(obj.getString("name"));
                            group.setDriverId(obj.getString("driver"));
                            group.setAdminId(obj.getString("admin"));
                            group.setId(Integer.valueOf(obj.getString("group_id")));

                            for(int i = 0 ; i< users.length() ; i++) {
                               JSONObject user = (JSONObject) users.get(i);
                               Participant p = new Participant(user.getString("user_fb_id"), user.getString("name"),"http://graph.facebook.com/"+user.getString("user_fb_id")+"/picture?type=large",  R.string.fontello_heart_empty);
                                if(user.getString("user_fb_id").equals(obj.getString("driver"))){
                                    p.setDriver(true);
                                }
                                p.setGroupActive(group.getActive());
                                participantList.add(p);
                            }
                            groupStatus.setText((group.getActive()==1)?"Activo":"Inactivo");


                            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                            AccessToken at = AccessToken.getCurrentAccessToken();
                            if(group.getActive() ==1 && !group.getDriverId().equals(at.getUserId())){
                                driverStatusBtn.setVisibility(View.VISIBLE);
                            } else {
                                driverStatusBtn.setVisibility(View.GONE);
                            }


                            if(group.getActive()==1){
                                activateGroup.setVisibility(View.GONE);
                                deactivateGroup.setVisibility(View.VISIBLE);
                                chat.setVisibility(View.VISIBLE);
                            } else {
                                activateGroup.setVisibility(View.VISIBLE);
                                deactivateGroup.setVisibility(View.GONE);
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
                        Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
}}
