package proyectofinal.autocodes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class LoginFragment extends Fragment {
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
/*
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                ((AutocodesApplication) getActivity().getApplication()).setAccessToken(currentAccessToken);

                if(currentAccessToken!= null){
                    getActivity().finish();
                    Log.i("LOGIN", "ACCESS TOKEN CHANGED");
                } else {
                    Log.i("LOGIN", "ACCESS TOKEN CHANGED TO NULL");
                }

            }
        };
        profileTracker = new ProfileTracker () {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile!= null) {
                    Log.i("NAME", currentProfile.getFirstName());
                }
            }
        };
*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();

        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        callbackManager = CallbackManager.Factory.create();


        // Inflate the layout for this fragment
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("LOGIN", "LOGIN OK");
                Toast.makeText(getContext(), "LogIn successful", Toast.LENGTH_LONG);
                getActivity().finish();
            }

            @Override
            public void onCancel() {
                Log.e("LOGIN", "LOGIN CANCELED");
                Toast.makeText(getContext(), "Error logging in", Toast.LENGTH_LONG);

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LOGIN", "LOGIN ERROR");
                Toast.makeText(getContext(), "Error logging in", Toast.LENGTH_LONG);
            }
        });
        return view;

    }
}
