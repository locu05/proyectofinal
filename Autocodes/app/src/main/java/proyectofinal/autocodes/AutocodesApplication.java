package proyectofinal.autocodes;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by lucas on 06/08/16.
 */
public class AutocodesApplication extends Application {

    AccessToken at;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public AccessToken getAccessToken() {
        return at;
    }

    public void setAccessToken(AccessToken authToken) {
        this.at = authToken;
    }


}
