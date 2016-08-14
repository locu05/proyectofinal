package proyectofinal.autocodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.model.Group;

public class ListGroupActivity extends AppCompatActivity {

    Context context;
    List<Group> groupList;
    boolean value = true;
    String serverBaseUrl = "http://107.170.81.44:3002";

    @Override
    protected void onResume() {
        super.onResume();
        //Chequea credenciales
        AccessToken at2 = AccessToken.getCurrentAccessToken();
        if (at2 == null){
            Log.e("LOGIN", "User NOT LOGGED, REDIRECTING");
            //Mandar al login si no esta logueado
            Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(goToNextActivity);
        } else {
            Log.e("LOGIN", "UserId on " + at2.getUserId());
            getUserInfo(at2.getUserId());
        }

        groupList = new ArrayList<Group>();
        getGroups(at2.getUserId());

    }
/*
    private void getFriends(AccessToken at ){
        GraphRequest req = GraphRequest.newMyFriendsRequest(at, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                Log.e("KEY", "Respondio");
                FacebookRequestError error = response.getError();
                if(error != null){
                    String err = error.getErrorMessage();
                    Log.e("KEY", err);
                }
                Log.e("RESP", response.getRawResponse());
                try {
                    JSONObject obj = (JSONObject) objects.get(0);
                    Iterator<?> it = obj.keys();
                    while (it.hasNext()){
                        Log.e("KEY", (String) it.next());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        req.executeAsync();
    }
*/
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;

    }

    public void getUserInfo(String userId){
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e("Preparing Request", "Calling /user with id " + userId);
        client.get( serverBaseUrl + "/user/" + userId,null ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("ServerResponse", "/user " + statusCode);
                Log.e("JsonResponse", "/user " + obj.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Intent intent = new Intent(getApplicationContext(), WeightAndHeightActivity.class);
                    startActivity(intent);
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



    public void getGroups(String userId){
        AsyncHttpClient client = new AsyncHttpClient();
        Log.e("Preparing Request", "Calling /groups with userId: " + userId);
        client.get(serverBaseUrl + "/groups/" + userId ,null ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    Log.e("ServerResponse", "/groups " + statusCode);
                    Log.e("JsonResponse", "/groups " + obj.toString());

                        for(int i = 0 ; i< obj.getJSONArray("groups").length() ; i++) {
                            Group group = new Group();
                            JSONObject jsonGroup = (JSONObject) obj.getJSONArray("groups").get(i);
                            group.setId((Integer) jsonGroup.get("group_id"));
                            group.setName((String) jsonGroup.get("name"));
                            group.setActive((Integer) jsonGroup.get("active"));
                            groupList.add(group);
                        }

                        GroupArrayAdapter groupAdapter = new GroupArrayAdapter(context, groupList);

                        ListView listView = (ListView) findViewById(R.id.groupListView);
                        listView.setAdapter(groupAdapter);


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
    }
}
