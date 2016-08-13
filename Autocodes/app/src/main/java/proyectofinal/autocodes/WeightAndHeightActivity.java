package proyectofinal.autocodes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class WeightAndHeightActivity extends AppCompatActivity {

    private SeekBar seekBarWeight;
    private TextView textViewWeight;
    private SeekBar seekBarHeight;
    private TextView textViewHeight;
    private Button confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_and_height);

        initializeVariables();

        // Initialize the textview with '0'.
        textViewWeight.setText(String.valueOf(seekBarWeight.getProgress()) + " kgs");

        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewWeight.setText(String.valueOf(progress) + " kgs");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            } });

        // Initialize the textview with '0'.
        textViewHeight.setText(String.valueOf((float)seekBarHeight.getProgress()/100) + " mts");

        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                textViewHeight.setText(String.valueOf((float)progress/100) + " mts");
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
                //TODO: aca va el request con los datos
                finish();
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
