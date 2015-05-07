package com.adaptableandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Connie on 4/23/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        WakeLocker.acquire(context);
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
        Log.d("OK", "AlarmReceiver.onReceive");

        Vibrator vib=(Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);    //for Vibration
        vib.vibrate(2000);

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        Intent notIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, 0);

                NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.adaptablelogo_transparent)
                        .setContentTitle("WARNING")
                        .setContentText("New automatic notification system")
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        //Sets ID for notification
        int mNotificationId = 001;
        //Gets instance of NotificationManager service

        NotificationManagerCompat mNotifyManager = NotificationManagerCompat.from(context);
        // Builds the notification and issues it.
        mNotifyManager.notify(mNotificationId, mBuilder.build());
        WakeLocker.release();

//        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
//
//        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
//        style.bigText(message);
//
//        //Generate a notification with just short text and small icon
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setContentIntent(contentIntent)
//                .setSmallIcon(R.drawable.adaptablelogo_transparent)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setStyle(style)
//                .setWhen(java.lang.System.currentTimeMillis())
//                .setAutoCancel(true);
//
//        Notification notification = builder.build();
//        manager.notify(0, notification);


    }

    /*http://www.vogella.com/tutorials/AndroidNotifications/article.html*/
//    public void createNotification(){
//        //Prepare intent which is triggered
//        // if the notification is selected
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.adaptablelogo_transparent)
//                        .setContentTitle("WARNING")
//                        .setContentText("Your location's drought conditions have changed.")
//                        .setAutoCancel(true);
//
//        mBuilder.setContentIntent(pIntent);
//
//        //Sets ID for notification
//        int mNotificationId = 001;
//        //Gets instance of NotificationManager service
//        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // Builds the notification and issues it.
//        mNotifyManager.notify(mNotificationId, mBuilder.build());
//    }
}
