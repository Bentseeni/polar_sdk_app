package com.example.polar_sdk_app;

import android.content.Context;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;

public class PolarApi {
    private static PolarBleApi polarBleApi = null;

    //private static Context context;

    public static PolarBleApi getInstance(Context context) {
        if(polarBleApi == null){
            polarBleApi = PolarBleApiDefaultImpl.defaultImplementation(context.getApplicationContext(),PolarBleApi.ALL_FEATURES);
        }
        return  polarBleApi;
    }

  /*  private PolarApi() {
        PolarBleApi polarBleApi;
        polarBleApi = PolarBleApiDefaultImpl.defaultImplementation(context.getApplicationContext(),PolarBleApi.ALL_FEATURES);
      */
    }

