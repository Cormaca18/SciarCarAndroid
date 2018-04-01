package com.sciarcar.sciarcar;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    PlaceAutocompleteFragment origin_search;
    PlaceAutocompleteFragment dest_search;

    EditText startDepartTime;
    EditText endDepartTime;
    EditText peopleCount;

    Marker originMarker;
    Marker destMarker;

    boolean mapReady = false;

    public LatLng originCoords;
    public LatLng destCoords;

    Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Display box displays for 4 times toast length seconds
        for (int i=0; i < 2; i++)
        {
            Toast.makeText(getApplicationContext(), "Enter the time you want to leave, How much this time can vary, And the distance you would be willing to walk from your set location ", Toast.LENGTH_LONG).show();
        }

        goButton = findViewById(R.id.go_button);
        startDepartTime = findViewById(R.id.start_depart_time);
        endDepartTime = findViewById(R.id.end_depart_time);
        peopleCount = findViewById(R.id.people_count);

        final Activity that = this;

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = Profile.getCurrentProfile().getId();

                if(!startDepartTime.getText().toString().contains(":")){
                    Toast.makeText(getApplicationContext(), "Please Enter a correct time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!endDepartTime.getText().toString().contains(":")){
                    Toast.makeText(getApplicationContext(), "Please Enter a correct time", Toast.LENGTH_SHORT).show();
                    return;
                }

                int pplCount = Integer.parseInt(peopleCount.getText().toString());

                if(pplCount < 0 || pplCount > 2){
                    Toast.makeText(getApplicationContext(), "Number of people must be either 1 or 2", Toast.LENGTH_SHORT).show();
                    return;
                }

                double originLat = originCoords.latitude;
                double originLng = originCoords.longitude;
                double destLat = destCoords.latitude;
                double destLng = destCoords.longitude;

                // user_id, start_time, end_time, origin_lat, origin_long, dest_lat, dest_long, num_seats

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("user_id", userId));
                params.add(new AbstractMap.SimpleEntry("num_seats", String.valueOf(pplCount)));
                params.add(new AbstractMap.SimpleEntry("start_time", String.valueOf(getTimestampFromTime(startDepartTime.getText().toString()))));
                params.add(new AbstractMap.SimpleEntry("end_time", String.valueOf(getTimestampFromTime(endDepartTime.getText().toString()))));
                params.add(new AbstractMap.SimpleEntry("origin_lat", String.valueOf(originLat)));
                params.add(new AbstractMap.SimpleEntry("origin_long", String.valueOf(originLng)));
                params.add(new AbstractMap.SimpleEntry("dest_lat", String.valueOf(destLat)));
                params.add(new AbstractMap.SimpleEntry("dest_long", String.valueOf(destLng)));

                new DataPoster(){

                    @Override
                    protected void onResponse(int status, JSONObject message){

                        String result = "jsonError";

                        try {
                            result = message.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("submit_trip", result);

                        if(!result.equals("failure")){

                            Intent i = new Intent(getApplicationContext(), PotentialMatchesActivity.class);
                            i.putExtra("tripID", result);
                            startActivity(i);

                        }else{

                            that.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Please correct your data", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }

                    }

                }.sendPost("https://sciarcar.herokuapp.com/submitTrip", params);


            }
        });

        //associating objects to fragments so they can be manipulated
        origin_search = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origin_autocomplete_fragment);

        dest_search = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);

        //Solution for adding mins to end of give or take time

        /*
        timeRadius.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String newStr = timeRadius.getText().toString();
                    if(newStr.length() > 4) {
                        if (!newStr.substring(newStr.length() - 5, newStr.length()).equals(" mins")) {
                            timeRadius.setText(newStr + " mins");
                        }
                    }else{
                        timeRadius.setText(newStr + " mins");
                    }
                }
            }
        });

        */


        //adding placeholder tect in searchbars
        origin_search.setHint("Enter Origin");
        dest_search.setHint("Enter Destination");

        //Set co-ordinates for origin marker - listens for click on origin in search bar after map is ready and moves map focus
        origin_search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(mapReady){
                    originCoords = place.getLatLng();
                    originMarker.setPosition(originCoords);
                    moveMap();
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("PLACE", "An error occurred: " + status);
            }
        });

        //same as above for destoination
        dest_search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(mapReady){
                    destCoords = place.getLatLng();
                    destMarker.setPosition(destCoords);
                    moveMap();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("PLACE", "An error occurred: " + status);
            }
        });
    }

    //handling callback after ASYNC loads maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });


        // Add a marker in Maynooth and move the camera. To be changed to user's current location
        originCoords = new LatLng(53.383596, -6.600554
        );
        destCoords = new LatLng(53.386596, -6.600554
        );
        //sets marker titles and colour
        originMarker = mMap.addMarker(new MarkerOptions().position(originCoords).title("Origin"));
        originMarker.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        // initialize camera position
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(originCoords.latitude,originCoords.longitude) , 14.0f) );

        destMarker = mMap.addMarker(new MarkerOptions().position(destCoords).title("Destination"));
        destMarker.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));


    }

    public void moveMap(){
        //attempt at finding zoom value
        /*
        double lat1 = originCoords.latitude;
        double lon1 = originCoords.longitude;

        double lat2 = destCoords.latitude;
        double lon2 = destCoords.longitude;

        float zoom = 14;
        double newLat = 0;
        double newLon = 0;

        newLat = (lat1+lat2)/2D;
        newLon = (lon1+lon2)/2D;

        double distance = Math.sqrt(Math.pow(lat1-lat2, 2) + Math.pow(lon1-lon2, 2));

        zoom = (float)(3.5D/distance);
        */
        //working code for finding zoom value taking in co-ordinates. Will need to be adjusted so both markers are always visible
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(destCoords);
        builder.include(originCoords);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        mMap.moveCamera( cu );

    }

    public long getTimestampFromTime(String time){

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        String s = date + " " + time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try
        {
            Date d = simpleDateFormat.parse(s);

            return d.getTime();
        }
        catch (ParseException ex)
        {
            return 0;
        }

    }


}
