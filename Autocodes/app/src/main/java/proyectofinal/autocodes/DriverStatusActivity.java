package proyectofinal.autocodes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.model.Group;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.util.ImageUtil;

public class DriverStatusActivity extends AppCompatActivity {
    String serverBaseUrl = "http://107.170.81.44:3002";
    BroadcastReceiver receiver;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("proyectofinal.autocodes.service.ACTIVE_GROUP_STATUS")
        );
    }
    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_status);
        final TextView driverNameTv = (TextView) findViewById(R.id.driverName);
        final ImageView driverAvatar = (ImageView) findViewById(R.id.driver_avatar);
        final TextView driverBac = (TextView) findViewById(R.id.bacTv);
        final TextView driverAptoTv = (TextView) findViewById(R.id.aptoLabel);
        final TextView secretCodeTv = (TextView) findViewById(R.id.secretCodeTv);

        setUpImageLoader();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Group groupStatus = (Group) intent.getSerializableExtra("GROUP_STATUS");
                // do something here.

                if(groupStatus.getDriverBac() == null){
                    driverBac.setText("No hay datos");
                } else{
                    driverBac.setText(String.valueOf(groupStatus.getDriverBac()));
                    if(groupStatus.getDriverBac() < 0.7d){
                        driverAptoTv.setText( "Apto para manejar");
                        driverAptoTv.setTextColor(Color.GREEN);
                    } else {
                        driverAptoTv.setText( "No apto para manejar");
                        driverAptoTv.setTextColor(Color.RED);
                        if(!AccessToken.getCurrentAccessToken().getUserId().equals(groupStatus.getDriverId())){
                            secretCodeTv.setText(String.valueOf(("" + groupStatus.getId()).hashCode()));
                        }
                        secretCodeTv.setVisibility(View.VISIBLE);
                    }
                }
                driverNameTv.setText(groupStatus.getDriverName());
                ImageUtil.displayImage(driverAvatar, groupStatus.getDriverAvatar() , null);

            }
        };

        Intent intent = getIntent();
        final int groupId = intent.getIntExtra("GroupId",-1);
        if(groupId == -1){
            //TODO Hacer algo con el error
        }
        /*
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, serverBaseUrl + "/group/" + groupId, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e(LogConstants.JSON_RESPONSE, "/group/ " + groupId  + response.toString());
                            //driverNameTv.setText((String) response.get("active"));
                            if(response.isNull("driver_bac")){
                                driverBac.setText("No hay datos");
                            } else{
                                driverBac.setText(String.valueOf(response.getDouble("driver_bac")));
                                if( response.getDouble("driver_bac") < 0.5d){
                                    driverAptoTv.setText( "Apto para manejar");
                                    driverAptoTv.setTextColor(Color.GREEN);
                                } else {
                                    driverAptoTv.setText( "No apto para manejar");
                                    driverAptoTv.setTextColor(Color.RED);
                                }
                            }
                            JSONArray users = response.getJSONArray("users");
                            for(int i = 0 ; i< users.length() ; i++) {
                                JSONObject user = (JSONObject) users.get(i);
                                if(user.getString("user_fb_id").equals(response.getString("driver"))){
                                    driverNameTv.setText(user.getString("name"));
                                    ImageUtil.displayImage(driverAvatar, "http://graph.facebook.com/"+user.getString("user_fb_id")+"/picture?type=large" , null);
                                }
                            }

                        } catch (Exception e) {
                            Log.e(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Error fetching the active groups: " + e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse!=null){
                            Log.i(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.networkResponse.statusCode));
                            Log.i(LogConstants.FETCH_ACTIVE_GROUP_SERVICE, "Error getting driver status");
                        } else {
                            Log.e(LogConstants.SERVER_RESPONSE, "Status Code:" + String.valueOf(error.getMessage()));
                        }
                    }
                });
        AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gomain, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.go_main: {
                Intent goToNextActivity = new Intent(getApplicationContext(), ListGroupActivity.class);
                startActivity(goToNextActivity);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpImageLoader() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
    }
}
