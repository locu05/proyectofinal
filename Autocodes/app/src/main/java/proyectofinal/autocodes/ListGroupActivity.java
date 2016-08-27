package proyectofinal.autocodes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.loopj.android.http.*;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.service.FetchActiveGroupService;
import proyectofinal.autocodes.service.TrackingService;
import proyectofinal.autocodes.view.ProgressWheel;

public class ListGroupActivity extends AppCompatActivity {

    private long mRequestStartTimeUser;
    private long mRequestStartTimeGroup;
    Context context;
    List<Group> groupList;
    boolean value = true;
    String serverBaseUrl = "http://107.170.81.44:3002";
    DynamicListView listView;
    AccessToken at2;
    BroadcastReceiver receiver;
    ProgressWheel progress;
    TextView emptyList;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("ActiveGroup")
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupList.clear();
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        at2 = AccessToken.getCurrentAccessToken();
        progress.setVisibility(View.VISIBLE);
        progress.spin();
        emptyList.setVisibility(View.GONE);
        if (at2 == null){
            Log.e(LogConstants.LOGIN, "User NOT LOGGED, REDIRECTING");
            //Mandar al login si no esta logueado
            Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToNextActivity);
        } else {
            Log.e(LogConstants.LOGIN, "UserId on " + at2.getUserId());
            getUserInfo(at2.getUserId());
            getGroups(at2.getUserId());
            Intent intent = new Intent(context, FetchActiveGroupService.class);
            intent.putExtra("userId", at2.getUserId());
            startService(intent);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.go_to_settings: {
                Intent goToNextActivity = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
            case R.id.logout_button: {
                LoginManager.getInstance().logOut();
                Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
            case R.id.new_group: {
                Intent goToNextActivity = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_listgroup);
        groupList = new ArrayList<Group>();
        final GroupArrayAdapter groupAdapter = new GroupArrayAdapter(context, groupList);
        listView = (DynamicListView) findViewById(R.id.groupListView);
        progress =(ProgressWheel) findViewById(R.id.progressbar_loading);
        emptyList = (TextView) findViewById(R.id.busy_EmptyIndicator);


        SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(
                groupAdapter, this, new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull final ViewGroup lv,
                                  @NonNull final int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
//                    if(admin){
                        deleteGroup(groupList.get(position));
                    groupList.remove(groupList.get(position));
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
//                    } else {
//                        exitGroup(groupList.get(position), at2.getUserId());
//                    }

                }
            }
        });

        swipeUndoAdapter.setAbsListView(listView);
        listView.setAdapter(swipeUndoAdapter);
        listView.enableSimpleSwipeUndo();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra("ActiveGroupMessage");
                Log.e("MENSAJE RECIBIDO", s);
            }
        };


    }

    private void deleteGroup(final Group group) {

        mRequestStartTimeUser = System.currentTimeMillis();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, serverBaseUrl + "/group/" + group.getId(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, "/group delete onResponse");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                        Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                        Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());

                    }
                });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);

//        AsyncHttpClient client = new AsyncHttpClient();
//        Log.e(LogConstants.PREPARING_REQUEST, "Calling /groups delete with groupId: " + group.getId());
//        client.delete(serverBaseUrl + "/group/" + group.getId() ,null ,new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    Log.e(LogConstants.SERVER_RESPONSE, "/group delete " + statusCode);
//
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                // When Http response code is '404'
//                if(statusCode == 404){
//                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code is '500'
//                else if(statusCode == 500){
//                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code other than 404, 500
//                else{
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;

    }

    public void getUserInfo(String userId){
        mRequestStartTimeUser = System.currentTimeMillis();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, serverBaseUrl + "/user/" + userId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                    Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    Log.e(LogConstants.JSON_RESPONSE, "/user " + response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                    Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    if(error.networkResponse!=null){
                        Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));
                    } else {
                        Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.getMessage()));
                    }


                }
            });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);


//        AsyncHttpClient client = new AsyncHttpClient();
//        Log.e(LogConstants.PREPARING_REQUEST, "Calling /user with id " + userId);
//        client.get( serverBaseUrl + "/user/" + userId,null ,new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(new String(responseBody));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e(LogConstants.SERVER_RESPONSE, "/user " + statusCode);
//                Log.e(LogConstants.JSON_RESPONSE, "/user " + obj.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                // When Http response code is '404'
//                if(statusCode == 404){
//                    Intent intent = new Intent(getApplicationContext(), WeightAndHeightActivity.class);
//                    startActivity(intent);
//                }
//                // When Http response code is '500'
//                else if(statusCode == 500){
//                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code other than 404, 500
//                else{
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });


    }



    public void getGroups(String userId){
        mRequestStartTimeGroup = System.currentTimeMillis();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, serverBaseUrl + "/groups/" + userId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroup;
                    Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    Log.e(LogConstants.JSON_RESPONSE, "/groups " + response.toString());
                    try {
                    for(int i = 0 ; i< response.getJSONArray("groups").length() ; i++) {
                            Group group = new Group();
                            JSONObject jsonGroup = (JSONObject) response.getJSONArray("groups").get(i);
                            group.setId((Integer) jsonGroup.get("group_id"));
                            group.setName((String) jsonGroup.get("name"));
                            group.setActive((Integer) jsonGroup.get("active"));
                            groupList.add(group);
                        }}
                    catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                    }

                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                    if(groupList.size()==0) {
                        emptyList.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        progress.stopSpinning();
                    } else {
                        progress.setVisibility(View.GONE);
                        progress.stopSpinning();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroup;
                    Log.e(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());

                }
            });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);


//        AsyncHttpClient client = new AsyncHttpClient();
//        Log.e(LogConstants.PREPARING_REQUEST, "Calling /groups with userId: " + userId);
//        client.get(serverBaseUrl + "/groups/" + userId ,null ,new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//                try {
//                    JSONObject obj = new JSONObject(new String(responseBody));
//                    Log.e(LogConstants.SERVER_RESPONSE, "/groups " + statusCode);
//                    Log.e(LogConstants.JSON_RESPONSE, "/groups " + obj.toString());
//
//                        for(int i = 0 ; i< obj.getJSONArray("groups").length() ; i++) {
//                            Group group = new Group();
//                            JSONObject jsonGroup = (JSONObject) obj.getJSONArray("groups").get(i);
//                            group.setId((Integer) jsonGroup.get("group_id"));
//                            group.setName((String) jsonGroup.get("name"));
//                            group.setActive((Integer) jsonGroup.get("active"));
//                            groupList.add(group);
//                        }
//
//                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
//
//
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                // When Http response code is '404'
//                if(statusCode == 404){
//                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code is '500'
//                else if(statusCode == 500){
//                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code other than 404, 500
//                else{
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
    }
}
