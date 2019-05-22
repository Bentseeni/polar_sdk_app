package com.example.polar_sdk_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.protobuf.StringValue;

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
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrBroadcastData;
import polar.com.sdk.api.model.PolarHrData;
import polar.com.sdk.api.model.PolarOhrPPGData;
import polar.com.sdk.api.model.PolarOhrPPIData;
import polar.com.sdk.api.model.PolarSensorSetting;

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
        //TextView textViewPpg = this.findViewById(R.id.ppg_number);
        TextView textViewPp = this.findViewById(R.id.ppi_number);
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
            public void ppgFeatureReady(String identifier) { Log.d(TAG,"PPG READY: " + identifier);
           // ppgStream();
            }

            @Override
            public void ppiFeatureReady(String identifier) { Log.d(TAG,"PPI READY: " + identifier);
           // ppiStream();
            }

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
               // Log.d(TAG,"HR value: " + data.hr + " rrsMs: " + data.rrsMs + " rr: " + data.rrs + " contact: " + data.contactStatus + "," + data.contactStatusSupported);
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



    }

    public void ppgStream(){
        final TextView textView = this.findViewById(R.id.ppg_number);
        if(ppgDisposable == null)
        {
            ppgDisposable = api.requestPpgSettings(DEVICE_ID).toFlowable().flatMap(new Function<PolarSensorSetting, Publisher<PolarOhrPPGData>>() {
                @Override
                public Publisher<PolarOhrPPGData> apply(PolarSensorSetting polarSensorSetting) throws Exception {
                    return api.startOhrPPGStreaming(DEVICE_ID,polarSensorSetting.maxSettings());
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PolarOhrPPGData>() {
                @Override
                public void accept(PolarOhrPPGData polarOhrPPGData) throws Exception {
                    for (PolarOhrPPGData.PolarOhrPPGSample data : polarOhrPPGData.samples) {
                      // textView.setText(Integer.toString(data.ppg0));
                         Log.d(TAG, "    ppg0: " + data.ppg0 + " ppg1: " + data.ppg1 + " ppg2: " + data.ppg2 + "ambient: " + data.ambient);
                    }
                    textView.setText(Integer.toString(polarOhrPPGData.samples.get(0).ppg0));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e(TAG, "" + throwable.getLocalizedMessage());

                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    Log.d(TAG,"complete");
                }
            });
        }
        else {
            ppgDisposable.dispose();
            ppgDisposable = null;
        }
    }

    public void ppiStream()
    {
        if(ppiDisposable == null)
        {
            final TextView textView = this.findViewById(R.id.ppi_number);
            final TextView textViewHr = this.findViewById(R.id.hr_number);


            ppiDisposable = api.startOhrPPIStreaming(DEVICE_ID).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<PolarOhrPPIData>() {
                @Override
                public void accept(PolarOhrPPIData polarOhrPPIData) throws Exception {
                    for(PolarOhrPPIData.PolarOhrPPISample sample : polarOhrPPIData.samples) {
                        Log.d(TAG, "ppi: " + sample.ppi
                                + " blocker: " + sample.blockerBit + " errorEstimate: " + sample.errorEstimate+sample.skinContactStatus+sample.skinContactSupported+sample.hr);
                        //textView.setText(sample.ppi+"±"+sample.errorEstimate +" "+ sample.hr);
                    }
                    textView.setText(polarOhrPPIData.samples.get(0).ppi+ "±" +polarOhrPPIData.samples.get(0).errorEstimate);

                    if(polarOhrPPIData.samples.get(0).hr != 0) {
                        textViewHr.setText(Integer.toString(polarOhrPPIData.samples.get(0).hr) );
                    }
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
        else
        {
            ppiDisposable.dispose();
            ppiDisposable = null;
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
