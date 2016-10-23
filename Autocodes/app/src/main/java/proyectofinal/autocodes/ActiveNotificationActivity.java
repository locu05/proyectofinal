package proyectofinal.autocodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import proyectofinal.autocodes.constant.AutocodesIntentConstants;
import proyectofinal.autocodes.constant.LogConstants;

/**
 * Created by locu on 4/9/16.
 */
public class ActiveNotificationActivity extends AppCompatActivity {

    public static boolean running;

    private EditText activeNotificationEdit;
    private Button confirmNotificacionCode;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_notificacion);
        extras = getIntent().getExtras();
        activeNotificationEdit = (EditText) findViewById(R.id.activeNotificationEdit);
        confirmNotificacionCode = (Button) findViewById(R.id.confirmNotificationCode);
        Log.d(LogConstants.BEHAVIOUR_LOG, "El hash code es: " + extras.getString(AutocodesIntentConstants.GROUP_ID).hashCode());

        confirmNotificacionCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActiveNotificationTimer.getInstance().setStartTime();
                ActiveNotificationTimer.getInstance().setElapsedTime();
                if(activeNotificationEdit.getText().toString().equals(String.valueOf(extras.getString(AutocodesIntentConstants.GROUP_ID).hashCode()))) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"El codigo es incorrecto, por favor reingrese",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
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
