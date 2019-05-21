package com.example.polar_sdk_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.protobuf.StringValue;

import java.util.UUID;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrBroadcastData;
import polar.com.sdk.api.model.PolarHrData;

public class HrActivity extends AppCompatActivity {

    String DEVICE_ID;
    int BATTERY_LVL;
    private final static String TAG = MainActivity.class.getSimpleName();
    Disposable ppgDisposable;
    Disposable ppiDisposable;
    Disposable accDisposable;
    Disposable broadcastDisposable;

    PolarBleApi api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);

        TextView textViewId = this.findViewById(R.id.id_text);
        final TextView textViewHr = this.findViewById(R.id.hr_number);
        TextView textViewPpg = this.findViewById(R.id.ppg_number);
        TextView textViewPp = this.findViewById(R.id.pp_number);
        final TextView textViewBatt = this.findViewById(R.id.battery_lvl_text);


        DEVICE_ID = getIntent().getStringExtra("device");
        Log.d(TAG,"DEVICE_ID: "+DEVICE_ID);
        textViewId.setText(DEVICE_ID);
        api = PolarApi.getInstance(this);
        //PolarApi.getInstance(this).setApiCallback(new PolarBleApiCallback()
        api.setApiCallback(new PolarBleApiCallback() {

            @Override
            public void blePowerStateChanged(boolean powered) {
                Log.d(TAG,"BLE power: " + powered);
            }

            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"CONNECTED: " + polarDeviceInfo.deviceId);
            }

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"CONNECTING: " + polarDeviceInfo.deviceId);

            }

            @Override
            public void deviceDisconnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"DISCONNECTED: " + polarDeviceInfo.deviceId);
                accDisposable = null;
                ppgDisposable = null;
                ppiDisposable = null;
            }

            @Override
            public void ecgFeatureReady(String identifier) { Log.d(TAG,"ECG READY: " + identifier);}

            @Override
            public void accelerometerFeatureReady(String identifier) {Log.d(TAG,"ACC READY: " + identifier); }

            @Override
            public void ppgFeatureReady(String identifier) { Log.d(TAG,"PPG READY: " + identifier);}

            @Override
            public void ppiFeatureReady(String identifier) { Log.d(TAG,"PPI READY: " + identifier);}

            @Override
            public void biozFeatureReady(String identifier) {Log.d(TAG,"BIOZ READY: " + identifier);}

            @Override
            public void hrFeatureReady(String identifier) { Log.d(TAG,"HR READY: " + identifier); }

            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
                Log.d(TAG,"uuid: " + uuid + " value: " + value);
            }

            @Override
            public void batteryLevelReceived(String identifier, int level) {
                Log.d(TAG,"BATTERY LEVEL: " + level);
                textViewBatt.setText(String.valueOf(level)+"%");
                BATTERY_LVL = level;
            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {
                Log.d(TAG,"HR value: " + data.hr + " rrsMs: " + data.rrsMs + " rr: " + data.rrs + " contact: " + data.contactStatus + "," + data.contactStatusSupported);
            }

            @Override
            public void polarFtpFeatureReady(String identifier) { Log.d(TAG,"FTP ready");}
        });

        try{
            api.connectToDevice(DEVICE_ID);
        }catch (PolarInvalidArgument polarInvalidArgument){
            polarInvalidArgument.printStackTrace();
        }

        if (broadcastDisposable ==null)
        {
            broadcastDisposable = api.startListenForPolarHrBroadcasts(null).subscribe(new Consumer<PolarHrBroadcastData>() {
                @Override
                public void accept(PolarHrBroadcastData polarHrBroadcastData) throws Exception {
                    textViewHr.setText(String.valueOf(polarHrBroadcastData.hr));

                    //Log.d(TAG,String.valueOf(polarHrBroadcastData.hr));


                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e(TAG,""+throwable.getLocalizedMessage());
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    Log.d(TAG,"complete");
                }
            });
        }

        if(ppgDisposable == null)
        {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  TextView textViewBatt = this.findViewById(R.id.battery_lvl_text);
      //  textViewBatt.setText(String.valueOf(BATTERY_LVL));
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
        try {
            api.disconnectFromDevice(DEVICE_ID);
        } catch (PolarInvalidArgument polarInvalidArgument) {
            polarInvalidArgument.printStackTrace();
        }
        //api.shutDown();
    }
}
