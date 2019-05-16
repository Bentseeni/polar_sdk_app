package com.example.polar_sdk_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.UUID;

import io.reactivex.disposables.Disposable;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;

public class HrActivity extends AppCompatActivity {

    String DEVICE_ID;
    private final static String TAG = MainActivity.class.getSimpleName();
    Disposable ppgDisposable;
    Disposable ppiDisposable;
    Disposable accDisposable;
    Disposable broadcastDisposable;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);
        TextView textViewId = this.findViewById(R.id.id_text);
        DEVICE_ID = getIntent().getStringExtra("device");
        Log.d(TAG,"DEVICE_ID: "+DEVICE_ID);
        textViewId.setText(DEVICE_ID);

        PolarApi.getInstance(this).setApiCallback(new PolarBleApiCallback() {
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
                super.batteryLevelReceived(identifier, level);
            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {
                super.hrNotificationReceived(identifier, data);
            }

            @Override
            public void polarFtpFeatureReady(String identifier) {
                super.polarFtpFeatureReady(identifier);
            }
        });

    }
}
