package proyectofinal.autocodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by locu on 4/9/16.
 */
public class ActiveNotificationActivity extends AppCompatActivity {

    public static boolean running;

    private EditText activeNotificationEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_notificacion);
        activeNotificationEdit = (EditText) findViewById(R.id.activeNotificationEdit);
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
