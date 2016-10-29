package proyectofinal.autocodes;

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

import proyectofinal.autocodes.constant.LogConstants;
import proyectofinal.autocodes.font.RobotoTextView;
import proyectofinal.autocodes.service.DeviceDataHolder;
import proyectofinal.autocodes.service.PullAndAnalizeDataService;

public class ActiveTestActivity extends AppCompatActivity {

    public static boolean running;

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
                            Thread.sleep(8000);
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
                                activeTestResult.setText(String.valueOf(bac));
                                if(bac < 0.5) {
                                    activeTestresultLabel.setText("Apto para manejar");
                                } else {
                                    activeTestresultLabel.setText("No apto para manejar");
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
}
