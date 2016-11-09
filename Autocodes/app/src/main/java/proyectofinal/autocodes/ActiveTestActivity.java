package proyectofinal.autocodes;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.font.RobotoTextView;
import proyectofinal.autocodes.model.Participant;
import proyectofinal.autocodes.service.DeviceDataHolder;
import proyectofinal.autocodes.service.PullAndAnalizeDataService;
import proyectofinal.autocodes.service.TrackingDriverService;

import static proyectofinal.autocodes.R.id.submitGroup;

public class ActiveTestActivity extends AppCompatActivity {

    public static boolean running;

    String serverBaseUrl = "http://107.170.81.44:3002";
    private long mRequestStartTimeUpdateBac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_test);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                startButton.setEnabled(false);

                new AsyncTask<Void, Integer, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        int progressStatus = 0;
                        if(Looper.myLooper() == null) {
                            Looper.prepare();
                        }
                        try {
                            DeviceDataHolder.getInstance().setActiveAlcoholTest(true);
                            Thread.sleep(10000);
                            DeviceDataHolder.getInstance().setActiveAlcoholTest(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                final TextView activeTestResult = (TextView) findViewById(R.id.activeTestResult);
                                final TextView resultLabel = (TextView) findViewById(R.id.resultLabel);
                                final TextView activeTestresultLabel = (TextView) findViewById(R.id.activeTestResultLabel);

                                double bac = PullAndAnalizeDataService.getDataAnalizer().getBac();
                                callService(String.valueOf(bac));
                                activeTestResult.setText(String.valueOf(bac));
                                if(bac < 0.5) {
                                    activeTestresultLabel.setText("Apto para manejar");
                                } else {
                                    activeTestresultLabel.setText("No apto para manejar");
                                    if(!ActiveNotificationActivity.running) {
                                        if(isMyServiceRunning(TrackingDriverService.class)) {
//                                            if(ActiveNotificationTimer.getInstance().getElapsedTime() > ActiveNotificationTimer.TIME_TO_ALLOW_NOTIFICACION) {
                                                ActiveNotificationTimer.getInstance().setStartTime();
                                                ActiveNotificationTimer.getInstance().setElapsedTime();
                                                Intent intentActiveNotification = new Intent(getApplicationContext(), ActiveNotificationActivity.class);
                                                intentActiveNotification.putExtra(AutocodesIntentConstants.GROUP_ID, String.valueOf(DeviceDataHolder.getInstance().getGroupId()));
                                                intentActiveNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intentActiveNotification.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                getApplicationContext().startActivity(intentActiveNotification);
//                                            } else {
//                                                ActiveNotificationTimer.getInstance().setElapsedTime();
//                                            }

                                        }

                                    }
                                }
                                activeTestResult.setVisibility(View.VISIBLE);
                                activeTestresultLabel.setVisibility(View.VISIBLE);
                                resultLabel.setVisibility(View.VISIBLE);
                                startButton.setEnabled(true);

                            }
                        });

                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        progressBar.setProgress(values[0]);

                    }
                }.execute();
            }
        });
    }

    private void callService(String bac) {
        try {
            mRequestStartTimeUpdateBac = System.currentTimeMillis();
            JSONObject obj = new JSONObject();
            obj.put("groupid", DeviceDataHolder.getInstance().getGroupId());
            obj.put("bac", Float.valueOf(bac));
            Log.d(LogConstants.PREPARING_REQUEST, "Rest call /group/driverBac: " + obj.toString());
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, serverBaseUrl + "/group/driverBac", obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateBac;
                            Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            Log.d(LogConstants.SERVER_RESPONSE, "/group/driverBac onResponse");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            long totalRequestTime = System.currentTimeMillis() - mRequestStartTimeUpdateBac;
                            Log.d(LogConstants.TIME_SERVER_RESPONSE, String.valueOf(totalRequestTime));
                            if(error!=null){
                                Log.d(LogConstants.SERVER_RESPONSE, error.getMessage()!=null?error.getMessage():"");
                            }
                        }
                    });

            AutocodesApplication.getInstance().getRequestQueue().add(jsObjRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        running = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
