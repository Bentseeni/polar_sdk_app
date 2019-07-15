package com.example.polar_sdk_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SensorDeviceArrayAdapter extends ArrayAdapter<SensorDevice>
{
    static final int VIEW_TYPE_DEVICE = 0;
    static final int VIEW_TYPE_COUNT = 3;

    SensorDeviceArrayAdapter(Context context, ArrayList<SensorDevice> sensorDevices){super(context,0, sensorDevices);}
/*
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        SensorDevice sensorDevice = getItem(position);
        return VIEW_TYPE_DEVICE;
       // return super.getItemViewType(position);
    }*/

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent) {
        SensorDevice sensorDevice = getItem(position);
       // if(convertView == null)
        //{
         /*   int layoutId = 0;
            if (getItemViewType(position)==VIEW_TYPE_DEVICE)
            {
                layoutId=R.layout.sensor_device_list_item;
            }*/
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sensor_device_list_item,parent,false);

        //}
        TextView textViewDevice = convertView.findViewById(R.id.device_name);
        TextView textViewStrength = convertView.findViewById(R.id.signal_strength);
        ImageView imageView = convertView.findViewById(R.id.sensor_image);

        textViewDevice.setText(sensorDevice.getDeviceName());
        int rssi = Integer.parseInt(sensorDevice.getDeviceRssi());
        if (rssi < -90)
        {
            textViewStrength.setText("Signal strength: Bad ");
        }
        else if (-90 <= rssi && rssi <= -71)
        {
            textViewStrength.setText("Signal strength: Poor ");
        }
        else if (-71 < rssi && rssi <=-61)
        {
            textViewStrength.setText("Signal strength: Ok ");
        }
        else if (rssi > -61)
        {
            textViewStrength.setText("Signal strength: Good ");
        }
        //textViewStrength.setText("Signal strength: "+sensorDevice.getDeviceRssi());
        Picasso.get().load("https://picsum.photos/200.jpg?random=1").transform(new RoundedCornersTransformation(15,1)).resize(100,100).centerCrop().into(imageView);



        return convertView;
        //return super.getView(position, convertView, parent);

    }
}
