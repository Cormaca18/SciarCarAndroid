package com.sciarcar.sciarcar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    TextView meetingPlace;
    TextView phoneNumber;
    Button cancelled;
    Button metMatch;
    Button noShow;

    String matchId;
    String tripId1;
    String tripId2;
    String phoneTripId;
    String sugMeetingPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);


        matchId = getIntent().getStringExtra("matchID");
        phoneTripId = getIntent().getStringExtra("phoneTripID");
        tripId1 = getIntent().getStringExtra("tripID1");
        tripId2 = getIntent().getStringExtra("tripID2");
        sugMeetingPlace = getIntent().getStringExtra("meetingPlace");

        meetingPlace = findViewById(R.id.meeting_place);
        phoneNumber = findViewById(R.id.phone_number);
        cancelled = findViewById(R.id.cancelled);
        metMatch = findViewById(R.id.met_match);
        noShow = findViewById(R.id.no_show);


        meetingPlace.setText(sugMeetingPlace);

        cancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("trip_id", Profile.getCurrentProfile().getId()));

                new DataPoster().sendPost("https://sciarcar.herokuapp.com/cancelled", params);

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

        metMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("trip_id", Profile.getCurrentProfile().getId()));

                new DataPoster().sendPost("https://sciarcar.herokuapp.com/met_match", params);

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

        noShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("trip_id", phoneTripId));
                params.add(new AbstractMap.SimpleEntry("match_id", matchId));

                new DataPoster().sendPost("https://sciarcar.herokuapp.com/no_show", params);

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

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
