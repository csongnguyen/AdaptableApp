package com.adaptableandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Connie on 4/23/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
        Log.d("OK", "AlarmReceiver.onReceive");
    }
}
