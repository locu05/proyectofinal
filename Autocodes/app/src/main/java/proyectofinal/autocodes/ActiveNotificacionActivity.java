package proyectofinal.autocodes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActiveNotificacionActivity extends AppCompatActivity {

    public static boolean running;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_notificacion);
    }
}
