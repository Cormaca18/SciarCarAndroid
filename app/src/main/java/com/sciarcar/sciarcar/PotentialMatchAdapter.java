package com.sciarcar.sciarcar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 14/03/2018.
 */

public class PotentialMatchAdapter extends ArrayAdapter<PotentialMatch> {

    private ArrayList<PotentialMatch> dataSet;
    Context mContext;
    String phoneTripId = "";

    // View lookup cache
    private static class ViewHolder {
        TextView origin;
        TextView dest;
        TextView pplCount;
        CheckBox match;
        TextView circle;
    }

    public PotentialMatchAdapter(ArrayList<PotentialMatch> data, Context context, String phoneTripId) {
        super(context, R.layout.potential_match_row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.phoneTripId = phoneTripId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final PotentialMatch dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.potential_match_row_item, parent, false);
            viewHolder.origin = (TextView) convertView.findViewById(R.id.origin);
            viewHolder.dest = (TextView) convertView.findViewById(R.id.dest);
            viewHolder.pplCount = (TextView) convertView.findViewById(R.id.ppl_count);
            viewHolder.match = convertView.findViewById(R.id.checkBox);
            viewHolder.circle = convertView.findViewById(R.id.circle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.origin.setText(dataModel.origin);
        viewHolder.dest.setText(dataModel.destination);
        viewHolder.pplCount.setText(dataModel.peopleCount);
        viewHolder.match.setChecked(dataModel.checked);
        if(!dataModel.circle){
            viewHolder.circle.setText("X");
            viewHolder.circle.setTextColor(Color.RED);
        }else{
            viewHolder.circle.setText("M");
            viewHolder.circle.setTextColor(Color.GREEN);
        }
        viewHolder.match.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String tripId = dataModel.tripId;

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("trip_id", tripId));
                params.add(new AbstractMap.SimpleEntry("user_id", Profile.getCurrentProfile().getId()));
                params.add(new AbstractMap.SimpleEntry("phone_trip_id", phoneTripId));


                if(b) {

                    new DataPoster() {

                        @Override
                        protected void onResponse(int status, JSONObject message) {

                            String result = "jsonError";

                            String matchId = "";

                            try {
                                result = message.getString("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                result = message.getString("match");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(!matchId.equals("")){

                                Intent i = new Intent(mContext, MatchActivity.class);
                                i.putExtra("tripID", phoneTripId);
                                mContext.startActivity(i);

                            }

                            Log.d("tripticked", result);

                        }


                    }.sendPost("https://sciarcar.herokuapp.com/trip_ticked", params);

                }else{

                    new DataPoster() {

                        @Override
                        protected void onResponse(int status, JSONObject message) {

                            String result = "jsonError";

                            try {
                                result = message.getString("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("tripunticked", result);

                        }


                    }.sendPost("https://sciarcar.herokuapp.com/trip_unticked", params);

                }

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
