package com.soft_sketch.hereim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class GpsLocationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "channel_1";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

        }
    }


}
