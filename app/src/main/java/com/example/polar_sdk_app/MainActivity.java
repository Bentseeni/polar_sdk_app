package com.example.polar_sdk_app;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarDeviceInfo;

public class MainActivity extends AppCompatActivity {

    PolarBleApi api;
    ArrayList<SensorDevice> arrayList = new ArrayList<>();
    ArrayList<String> deviceNameList = new ArrayList<>();
    Disposable scanDisposable;
    private final static String TAG = MainActivity.class.getSimpleName();
    private long then;
    private int longClickDuration = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // api = PolarBleApiDefaultImpl.defaultImplementation(this,PolarBleApi.FEATURE_DEVICE_INFO);
        api = PolarApi.getInstance(this);
        api.setPolarFilter(true);
        final SensorDeviceArrayAdapter arrayAdapter = new SensorDeviceArrayAdapter(this,arrayList);
        final ListView listView = findViewById(R.id.device_list_view);
        //PolarApi.getInstance().


        findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final Button scanButton = findViewById(R.id.scan_button);
               // scanButton.setText("Clear");
                //arrayList.clear();
                //arrayAdapter.clear();
                //arrayAdapter.notifyDataSetChanged();
                if (scanDisposable == null)
                {
                    scanButton.setText("Clear");

                    scanDisposable = api.searchForDevice().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PolarDeviceInfo>() {
                        @Override
                        public void accept(PolarDeviceInfo polarDeviceInfo) throws Exception {
                            SensorDevice sensorDevice = new SensorDevice();
                            sensorDevice.setDeviceAddress(polarDeviceInfo.address);
                            sensorDevice.setDeviceId(polarDeviceInfo.deviceId);
                            sensorDevice.setDeviceIsConnectable(polarDeviceInfo.isConnectable);
                            sensorDevice.setDeviceName(polarDeviceInfo.name);
                            sensorDevice.setDeviceRssi(Integer.toString(polarDeviceInfo.rssi));
                            sensorDevice.setDevicePictureUrl("https://picsum.photos/200");


                            arrayList.add(sensorDevice);
                            deviceNameList.add(sensorDevice.getDeviceName()+" "+sensorDevice.getDeviceRssi());
                            listView.setAdapter(arrayAdapter);


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {

                        }
                    });
                }
                else {
                    scanDisposable.dispose();
                    scanDisposable = null;
                    arrayList.clear();
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                    scanButton.setText("Scan");

                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Button scanButton = findViewById(R.id.scan_button);

                try {
                    Intent intent = new Intent(MainActivity.this,HrActivity.class);
                    intent.putExtra("device",arrayList.get(position).getDeviceId());
                    scanDisposable.dispose();
                    scanDisposable = null;
                    arrayList.clear();
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                    scanButton.setText("Scan");

                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();

                }

                //bundle.putSerializable("api",api);
            }
        });

        findViewById(R.id.textView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {





                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    then = (long) System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ((System.currentTimeMillis() - then) > longClickDuration) {

                        Intent intent = new Intent(MainActivity.this,HrActivity.class);
                        intent.putExtra("device","5092832D");
                        startActivity(intent);

                        System.out.println("Long Click has happened!");
                        return false;
                    } else {
                        /* Implement short click behavior here or do nothing */
                        System.out.println("Short Click has happened...");
                        return false;
                    }
                }
                return true;
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && savedInstanceState == null) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        api.foregroundEntered();

    }

    @Override
    protected void onPause() {
        super.onPause();
        api.backgroundEntered();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        api.shutDown();
    }
}
