package proyectofinal.autocodes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.service.FetchActiveGroupService;
import proyectofinal.autocodes.view.ProgressWheel;

public class ListGroupActivity extends AppCompatActivity {

    Context listGroupActivityContext;
    List<Group> groupList;
    boolean value = true;
    String serverBaseUrl = "http://107.170.81.44:3002";
    public static final String PREFS_FILE = "AutocodesPrefs";
    DynamicListView listView;
    AccessToken at2;
    BroadcastReceiver receiver;
    ProgressWheel progress;
    TextView emptyList;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LogConstants.LOGIN, "Starting main activity");
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
        Log.d(LogConstants.LOGIN, "Resuming main activity");
        groupList.clear();
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        at2 = AccessToken.getCurrentAccessToken();
        progress.setVisibility(View.VISIBLE);
        progress.spin();
        emptyList.setVisibility(View.GONE);
        if (at2 == null){
            Log.i(LogConstants.LOGIN, "User not logged, redirecting to login");
            //Mandar al login si no esta logueado
            Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToNextActivity);
        } else {
            Log.d(LogConstants.LOGIN, "User logged: " + at2.getUserId());
            SharedPreferences settings = getSharedPreferences("PREFS_FILE", 0);
            if(!settings.getBoolean("userOK", false)){
                Log.i(LogConstants.LOGIN, "Checking if user exists " + at2.getUserId());
                getUserInfo(at2.getUserId());
            } else{
                Log.i(LogConstants.LOGIN, "Getting user groups: " + at2.getUserId());
                getGroups(at2.getUserId());
            }
            Intent intent = new Intent(listGroupActivityContext, FetchActiveGroupService.class);
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
                SharedPreferences settings = getSharedPreferences("PREFS_FILE", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("userOK", false);
                editor.commit();
                Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
            case R.id.new_group: {
                Intent goToNextActivity = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
//           x case R.id.active_test_btn: {
//                Intent goToNextActivity = new Intent(getApplicationContext(), ActiveTestActivity.class);
//                startActivity(goToNextActivity);
//                return true;
//            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listGroupActivityContext = this;
        at2 = AccessToken.getCurrentAccessToken();
        setContentView(R.layout.activity_listgroup);
        groupList = new ArrayList<Group>();
        final GroupArrayAdapter groupAdapter = new GroupArrayAdapter(listGroupActivityContext, groupList);
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
                    if(groupList.get(position).getActive()==0) {
                        deleteGroup(groupList.get(position));
                        groupList.remove(groupList.get(position));
                        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
//                    } else {
//                        exitGroup(groupList.get(position), at2.getUserId());
//                    }
                    } else {
                        Toast.makeText(getApplicationContext(), "No se puede eliminar/salir" +
                                " de un grupo activo", Toast.LENGTH_LONG).show();
                    }


                }
            }
        });

        swipeUndoAdapter.setAbsListView(listView);
        listView.setAdapter(swipeUndoAdapter);
        listView.enableSimpleSwipeUndo();


    }

    private void deleteGroup(final Group group) {

        final long mRequestStartTimeUser = System.currentTimeMillis();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.DELETE, serverBaseUrl + "/group/" + group.getId(), null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                            Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            Log.i(LogConstants.SERVER_RESPONSE, "/group delete onResponse");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                            Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                        }
                    });

            AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;

    }

    public void getUserInfo(final String userId){
        final long mRequestStartTimeUser = System.currentTimeMillis();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, serverBaseUrl + "/user/" + userId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                    Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    Log.i(LogConstants.JSON_RESPONSE, "/user/userid " + response.toString());

                    SharedPreferences settings = getSharedPreferences("PREFS_FILE", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("userOK", true);
                    editor.commit();
                    getGroups(userId);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    SharedPreferences settings = getSharedPreferences("PREFS_FILE", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("userOK", false);
                    editor.commit();

                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUser;
                    Log.d(LogConstants.TIME_SERVER_RESPONSE, "/user/" + userId + " " + String.valueOf(totalRequestTime));
                    if(error.networkResponse!=null){
                        //Si el usuario no existe, ir a la pantalla de peso y altura para crearlo
                        if (error.networkResponse.statusCode == 404){
                            Log.d(LogConstants.SERVER_RESPONSE, "/user/" + userId + " Does not exist, sending to WeightAndHeightActivity");
                            Intent intent = new Intent(getApplicationContext(), WeightAndHeightActivity.class);
                            startActivity(intent);
                        } else{
                            Log.e(LogConstants.SERVER_RESPONSE, "/user/" + userId + " Status Code:" + String.valueOf(error.networkResponse.statusCode));
                        }
                    } else {
                        Log.e(LogConstants.SERVER_RESPONSE, "/user/" + userId + " Error:" + String.valueOf(error.getMessage()));
                    }
                }
            });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);

    }



    public void getGroups(final String userId){
        final long mRequestStartTimeGroup = System.currentTimeMillis();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, serverBaseUrl + "/groups/" + userId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeGroup;
                    Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    Log.i(LogConstants.JSON_RESPONSE, "/groups " + response.toString());
                    try {
                    for(int i = 0 ; i< response.getJSONArray("groups").length() ; i++) {
                            Group group = new Group();
                            JSONObject jsonGroup = (JSONObject) response.getJSONArray("groups").get(i);
                            group.setId((Integer) jsonGroup.get("group_id"));
                            group.setName((String) jsonGroup.get("name"));
                            group.setAdminId(((Integer) jsonGroup.get("is_admin") == 1 )? userId : null);
                            // group.setDriverId((String) jsonGroup.get("driver"));
                            group.setActive((Integer) jsonGroup.get("active"));
                            groupList.add(group);
                        }}
                    catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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
                    Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                    if(error.getMessage() != null){
                        Log.e(LogConstants.SERVER_RESPONSE, error.getMessage());
                    } else {
                        Log.e(LogConstants.SERVER_RESPONSE, "/groups/" + userId + " Status code: " + error.networkResponse);
                    }

                }
            });

        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);

    }
}
