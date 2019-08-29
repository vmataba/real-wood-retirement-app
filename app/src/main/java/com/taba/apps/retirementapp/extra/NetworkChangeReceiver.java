package com.taba.apps.retirementapp.extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.taba.apps.retirementapp.FinancialRequestActivity;

public class NetworkChangeReceiver  extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        //Financial request receiver


        if (!Device.hasInternet(context)){
            Toast.makeText(context,"Internet connection dropped",Toast.LENGTH_LONG).show();
        }

    }
}
