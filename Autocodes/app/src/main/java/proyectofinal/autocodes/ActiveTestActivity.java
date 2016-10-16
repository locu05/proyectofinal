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
import android.widget.Toast;

import proyectofinal.autocodes.constant.LogConstants;

public class ActiveTestActivity extends AppCompatActivity {

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
                        Looper.prepare();
                        Toast.makeText(getBaseContext(), "STARTING", Toast.LENGTH_LONG);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                final View activeTestResult = findViewById(R.id.activeTestResult);
                                final View resultLabel = findViewById(R.id.resultLabel);
                                final View activeTestresultLabel = findViewById(R.id.activeTestResultLabel);

                                activeTestResult.setVisibility(View.VISIBLE);
                                activeTestresultLabel.setVisibility(View.VISIBLE);
                                resultLabel.setVisibility(View.VISIBLE);

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
}
