package proyectofinal.autocodes;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import proyectofinal.autocodes.adapter.GroupArrayAdapter;
import proyectofinal.autocodes.model.Grupo;

public class ListGroupActivity extends AppCompatActivity {

    TextView errorMsg;
    Context context;
    List<Grupo> groupList;

    @Override
    protected void onResume() {
        super.onResume();
        groupList = new ArrayList<Grupo>();
        RequestParams params = new RequestParams();
        params.put("userid","ACA-VA-EL-ID-DEL-USUARIO-ACTUAL");
        Log.e("Preparing rest call", params.toString());
        invokeWS(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_listgroup);

    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://107.170.81.44:3000/getgroups",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    Log.e("Response:", obj.toString());

                        for(int i = 0 ; i< obj.getJSONArray("grupos").length() ; i++) {
                            Grupo grupo = new Grupo();
                            JSONObject jsonGroup = (JSONObject) obj.getJSONArray("grupos").get(i);
                            grupo.setId((Integer) jsonGroup.get("id"));
                            grupo.setName((String) jsonGroup.get("nombre"));
                            groupList.add(grupo);
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
