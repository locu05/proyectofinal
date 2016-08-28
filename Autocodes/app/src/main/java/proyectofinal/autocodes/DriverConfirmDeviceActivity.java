package proyectofinal.autocodes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import proyectofinal.autocodes.service.TrackingDriverService;

/**
 * Created by locu on 28/8/16.
 */
public class DriverConfirmDeviceActivity extends AppCompatActivity {

    public static boolean running;
    Button confirmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverconfirmdevice);
        final Bundle extras = getIntent().getExtras();
        confirmDevice = (Button) findViewById(R.id.confirmDevice);
        confirmDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentTrackingDriverService = new Intent(getApplicationContext(), TrackingDriverService.class);
                intentTrackingDriverService.putExtra("group", extras.getSerializable("group"));
                startService(intentTrackingDriverService);
                finish();
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
