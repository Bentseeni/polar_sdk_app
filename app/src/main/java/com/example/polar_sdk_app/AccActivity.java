package com.example.polar_sdk_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Publisher;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarAccelerometerData;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;
import polar.com.sdk.api.model.PolarSensorSetting;

public class AccActivity extends AppCompatActivity {

    String DEVICE_ID;
    private final static String TAG = MainActivity.class.getSimpleName();
    Disposable accDisposable;
    PolarBleApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        DEVICE_ID = getIntent().getStringExtra("DEVICE_ID");
        api = PolarApi.getInstance(this);

        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean powered) {
                super.blePowerStateChanged(powered);
            }

            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnected(polarDeviceInfo);
            }

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                super.deviceConnecting(polarDeviceInfo);
            }

            @Override
            public void deviceDisconnected(PolarDeviceInfo polarDeviceInfo) {
                super.deviceDisconnected(polarDeviceInfo);
            }

            @Override
            public void ecgFeatureReady(String identifier) {
                super.ecgFeatureReady(identifier);
            }

            @Override
            public void accelerometerFeatureReady(String identifier) {
                Log.d(TAG,"ACC READY: " + identifier);
                accStream();
            }

            @Override
            public void ppgFeatureReady(String identifier) {
                super.ppgFeatureReady(identifier);
            }

            @Override
            public void ppiFeatureReady(String identifier) {
                super.ppiFeatureReady(identifier);
            }

            @Override
            public void biozFeatureReady(String identifier) {
                super.biozFeatureReady(identifier);
            }

            @Override
            public void hrFeatureReady(String identifier) {
                super.hrFeatureReady(identifier);
            }

            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
                super.disInformationReceived(identifier, uuid, value);
            }

            @Override
            public void batteryLevelReceived(String identifier, int level) {
                super.batteryLevelReceived(identifier, level);
            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {
                // Log.d(TAG,"HR value: " + data.hr + " rrsMs: " + data.rrsMs + " rr: " + data.rrs + " contact: " + data.contactStatus + "," + data.contactStatusSupported);
            }

            @Override
            public void polarFtpFeatureReady(String identifier) {
                super.polarFtpFeatureReady(identifier);
            }
        });

        try{
            api.connectToDevice(DEVICE_ID);
        }catch (PolarInvalidArgument polarInvalidArgument){
            polarInvalidArgument.printStackTrace();
        }
       // accStream();
    }

    public void accStream() {
        final TextView textViewX = this.findViewById(R.id.x_text);
        final TextView textViewY = this.findViewById(R.id.y_text);
        final TextView textViewZ = this.findViewById(R.id.z_text);
        final TextView textViewVector = this.findViewById(R.id.vector_text);
       // final int vectorLength = 0;

        if (accDisposable == null) {
            accDisposable = api.requestAccSettings(DEVICE_ID).toFlowable().flatMap(new Function<PolarSensorSetting, Publisher<PolarAccelerometerData>>() {
                @Override
                public Publisher<PolarAccelerometerData> apply(PolarSensorSetting settings) throws Exception {
                    PolarSensorSetting sensorSetting = settings.maxSettings();
                    return api.startAccStreaming(DEVICE_ID, sensorSetting);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Consumer<PolarAccelerometerData>() {
                        @Override
                        public void accept(PolarAccelerometerData polarAccelerometerData) throws Exception {
                            for (PolarAccelerometerData.PolarAccelerometerSample data : polarAccelerometerData.samples) {
                                textViewX.setText("X: " + data.x);
                                textViewY.setText("Y: " + data.y);
                                textViewZ.setText("Z: " + data.z);
                                int vectorLength = (int) Math.sqrt((data.x * data.x) + (data.y * data.y) +(data.z * data.z));
                                textViewVector.setText("V: "+vectorLength);
                                Log.d(TAG, " x: " + data.x + " y: " + data.y + " z: " + data.z+ " v: "+vectorLength);


                            }
                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, "" + throwable.getLocalizedMessage());
                        }
                    },
                    new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.d(TAG, "complete");
                        }
                    }
            );}
         else{
                // NOTE dispose will stop streaming if it is "running"
                accDisposable.dispose();
                accDisposable = null;
            }

    }

    @Override
    public void onPause() {
        super.onPause();
        api.backgroundEntered();
    }

    @Override
    public void onResume() {
        super.onResume();
        accStream();
        api.foregroundEntered();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accDisposable.dispose();
        accDisposable = null;
       // api.shutDown();
    }
}
