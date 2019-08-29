package com.taba.apps.retirementapp.extra;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Device {

    private Context context;

    public Device(Context context){
        this.context = context;
    }

    public static boolean hasInternet (Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
