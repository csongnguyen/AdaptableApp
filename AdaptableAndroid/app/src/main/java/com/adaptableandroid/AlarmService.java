package com.adaptableandroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Connie on 5/2/2015.
 */
public class AlarmService {
    private Context context;
    private PendingIntent mAlarmSender;

    public AlarmService(Context context){
        this.context = context;
        mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
        System.out.println("Starting alarm service");
    }

    public void startAlarm(){
        // Set the alarm to 10 seconds from now
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 1);
        c.setTimeInMillis(System.currentTimeMillis());
        long firstTime = c.getTimeInMillis() + 10000;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 6000, mAlarmSender);
        am.set(AlarmManager.RTC_WAKEUP,firstTime, mAlarmSender);
        System.out.println("alarm is set on repeat");
    }
}
