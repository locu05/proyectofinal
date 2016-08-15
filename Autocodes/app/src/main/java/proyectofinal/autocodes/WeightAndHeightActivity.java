package proyectofinal.autocodes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.constant.LogConstants;

public class WeightAndHeightActivity extends AppCompatActivity {

    private SeekBar seekBarWeight;
    private TextView textViewWeight;
    private SeekBar seekBarHeight;
    private TextView textViewHeight;
    private Button confirm;
    String serverBaseUrl = "http://107.170.81.44:3002";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_and_height);

        initializeVariables();

        // Initialize the textview with '0'.
        textViewWeight.setText(String.valueOf(seekBarWeight.getProgress()));

        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewWeight.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            } });

        // Initialize the textview with '0'.
        textViewHeight.setText(String.valueOf((float)seekBarHeight.getProgress()/100));

        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewHeight.setText(String.valueOf((float)progress/100));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            } });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createUser(Integer.valueOf(textViewWeight.getText().toString()), Float.valueOf((Float.valueOf( textViewHeight.getText().toString() ) * 100)).intValue());
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void createUser(int weight, int height) throws JSONException, UnsupportedEncodingException {
        Profile userProfile = Profile.getCurrentProfile();
        String fullName = userProfile.getFirstName() + " "  + userProfile.getMiddleName() + " " + userProfile.getLastName();
        JSONObject obj = new JSONObject();
        obj.put("name", fullName);
        obj.put("avatar",userProfile.getProfilePictureUri(48,48));
        obj.put("weight", weight);
        obj.put("height", height);
        obj.put("user_fb_id",userProfile.getId());

        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = new StringEntity(obj.toString());

        client.post(getApplicationContext(), serverBaseUrl + "/user", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(LogConstants.SERVER_RESPONSE, "" + statusCode);

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
    // A private method to help us initialize our variables.
    private void initializeVariables() {
        seekBarWeight = (SeekBar) findViewById(R.id.seekBar1);
        textViewWeight = (TextView) findViewById(R.id.textView1);
        seekBarHeight = (SeekBar) findViewById(R.id.seekBar2);
        textViewHeight = (TextView) findViewById(R.id.textView2);
        confirm = (Button) findViewById(R.id.submitWeightAndHeight);
    }

}