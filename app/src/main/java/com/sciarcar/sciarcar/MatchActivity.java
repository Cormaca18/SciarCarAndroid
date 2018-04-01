package com.sciarcar.sciarcar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.Profile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry("trip_id", getIntent().getStringExtra("tripID")));


        /*

        new DataPoster(){

            @Override
            protected void onResponse(int status, JSONObject message){

                String match = "";

            }

        }.sendPost("https://sciarcar.herokuapp.com/get_match", params);

        */

    }
}
