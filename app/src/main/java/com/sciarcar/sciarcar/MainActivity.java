package com.sciarcar.sciarcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ProfilePictureView profilePictureView;
    private ProfileTracker mProfileTracker;
    private Button proceedButton;
    private EditText phoneNo;
    boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        //tracking profile changes - ie logging in or out
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile == null){
                    removeUserInfo();
                }else{
                    loadUserInfo(currentProfile);
                }
            }
        };

        mProfileTracker.startTracking();

        //displaying profile pic and continue button with ids from layout file
        profilePictureView = findViewById(R.id.profile_pic_main);
        proceedButton = findViewById(R.id.proceed_but);
        phoneNo = findViewById(R.id.phone_no);

        final Activity that = this;

        //Handling onclick of Go button checking if a FB profile is logged in
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loggedIn){

                    String userId = Profile.getCurrentProfile().getId();
                    String phoneNum = phoneNo.getText().toString();

                    if(phoneNum.length() > 0 && phoneNum.length() != 10){

                        Toast.makeText(getApplicationContext(), "Please Enter a correct phone number", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("user_id", userId));
                    params.add(new AbstractMap.SimpleEntry("phone_number", phoneNum));



                    new DataPoster(){

                        @Override
                        protected void onResponse(int status, JSONObject message){

                            String result = "jsonError";

                            try {
                                result = message.getString("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("postmsg", result);

                            if(result.equals("success") || result.equals("number updated")) {

                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(i);

                            }else if(result.equals("no number")){

                                that.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Please Enter a phone number", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }else{

                                that.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Please Enter a phone number", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                    }.sendPost("https://sciarcar.herokuapp.com/submit_user", params);



                }else{
                    Toast.makeText(getApplicationContext(), "Please Log into Facebook", Toast.LENGTH_LONG).show();
                }
            }
        });

        //On App launch check if FB profile is active from SavedInstanceState
        if(Profile.getCurrentProfile() != null){
            loggedIn = true;
            profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
        }else{
            loggedIn = false;
        }

        callbackManager = CallbackManager.Factory.create();

    }
    //Load user info, change boolean and profile pic
    public void loadUserInfo(Profile p){
        loggedIn = true;
        profilePictureView.setProfileId(p.getId());
    }
    //remove info after logging out
    public void removeUserInfo(){
        loggedIn = false;
        profilePictureView.setProfileId(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
