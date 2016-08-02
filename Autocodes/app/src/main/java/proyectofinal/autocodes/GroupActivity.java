package proyectofinal.autocodes;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import proyectofinal.autocodes.adapter.ParticipantArrayAdapter;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.model.Grupo;
import proyectofinal.autocodes.model.Participant;


public class GroupActivity extends AppCompatActivity {

    TextView errorMsg;
    Context context;
    List<Participant> participantList;

    @Override
    protected void onResume() {
        super.onResume();
        participantList = new ArrayList<Participant>();
        RequestParams params = new RequestParams();
        params.put("userId","ACA-VA-EL-ID-DEL-USUARIO-ACTUAL");
        params.put("groupId","ACA-VA-EL-ID-DEL-GRUPO-ACTUAL");
        Log.e("Preparing rest call", params.toString());
        invokeWS(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(AutocodesIntentConstants.GROUP_ID);
            Log.e("LA POSITION ERA", value);
        }

    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://107.170.81.44:3000/getgroups",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    Log.e("Response:", obj.toString());

//                    for(int i = 0 ; i< obj.getJSONArray("grupos").length() ; i++) {
//                        Grupo grupo = new Grupo();
//                        JSONObject jsonGroup = (JSONObject) obj.getJSONArray("grupos").get(i);
//                        grupo.setId((Integer) jsonGroup.get("id"));
//                        grupo.setName((String) jsonGroup.get("nombre"));
//                        participantList.add(grupo);
//                    }

                    participantList.add(new Participant("Martin",1));
                    participantList.add(new Participant("Fede",2));
                    participantList.add(new Participant("Lucas",3));

                    ParticipantArrayAdapter participantAdapter = new ParticipantArrayAdapter(context, participantList);

                    ListView listView = (ListView) findViewById(R.id.groupView);
                    listView.setAdapter(participantAdapter);


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
