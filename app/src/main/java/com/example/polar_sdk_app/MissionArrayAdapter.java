package com.example.polar_sdk_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MissionArrayAdapter extends ArrayAdapter<Mission> {
    MissionArrayAdapter(Context context, ArrayList<Mission> missions)
    {
        super(context,0,missions);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        Mission mission = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.mission_list_item,parent,false);
        TextView textViewName = convertView.findViewById(R.id.mission_name);
        TextView textViewScore = convertView.findViewById(R.id.mission_score);
        TextView textViewDescription = convertView.findViewById(R.id.mission_description);
        TextView textViewValue = convertView.findViewById(R.id.mission_value);
        TextView textViewUnit = convertView.findViewById(R.id.mission_unit);
       // return super.getView(position, convertView, parent);

        textViewName.setText(mission.getMissionName());
        textViewScore.setText(mission.getMissionScore());
        textViewDescription.setText(mission.getMissionDescription());
        textViewValue.setText(mission.getMissionValue());
        textViewUnit.setText(mission.getMissionUnit());
        return convertView;
    }
}
