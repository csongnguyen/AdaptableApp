package com.adaptableandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    public static int TEST_TIME_OUT = 3000;
    public final static String EXTRA_MESSAGE = "com.example.connie.myfirstapp.MESSAGE";
    private LocationManager locationManager;
    private String bestProvider;
    private String location;
    PopupWindow pw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setBackgroundDrawable(new ColorDrawable(0xff353538));
        //actionbar.setIcon(R.drawable.ic_action_search);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.custom_menu, null);
        actionbar.setCustomView(view);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            createNotification();
            return true;
        }
        else if(id == R.id.action_warnings){
            checkLocation();
            return true;
        }
        else if(id == R.id.action_checklists){
            // TO-DO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToChecklist(View view){
        Intent intent = new Intent(this, DisplayChecklistActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Hello, there");
        startActivity(intent);

    }

    public void createNotification(View view){
        createNotification();
    }

    /*http://www.vogella.com/tutorials/AndroidNotifications/article.html*/
    public void createNotification(){
        //Prepare intent which is triggered
        // if the notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

//        // Build notification
//        Notification n = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.adaptablelogo_transparent)
//                .setContentTitle("WARNING")
//                .setContentText("Your location's drought conditions have changed.")
//                .setContentIntent(pIntent).build();
//
//        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        //hide the notification after it's selected
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notifyManager.notify(0, n);

        // I can also do:
            NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.adaptablelogo_transparent)
            .setContentTitle("WARNING")
            .setContentText("Your location's drought conditions have changed.")
            .setAutoCancel(true);

            mBuilder.setContentIntent(pIntent);

            //Sets ID for notification
            int mNotificationId = 001;
            //Gets instance of NotificationManager service
            NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            // Builds the notification and issues it.
            mNotifyManager.notify(mNotificationId, mBuilder.build());


    }

    public void checkLocation(View view){
        checkLocation();
    }

    public void checkLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager != null){


//            Location location_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Location location_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            System.out.println("Got a location manager");
            if(isNetworkEnabled | gpsIsEnabled){
                Criteria criteria = new Criteria();
                bestProvider = locationManager.getBestProvider(criteria, false);
                Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
                if(isNetworkEnabled){
                    System.out.println("network is enabled.");
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                else if(gpsIsEnabled){
                    System.out.println("gps is enabled");
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }


                try{
                    location = currentLocation.getLatitude() + " " + currentLocation.getLongitude();
                }catch(Exception e){
                    location = "Cannot get location";
                }




                // Set the text view as the activity layout
                try{
                    // We need to get the instance of the LayoutInflater, use the context of this activity
                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    //Inflate the view from a predefined XML layout
                    View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_element));

                    // create a 300px width and 470px height PopupWindow

                    pw = new PopupWindow(layout, 300, 470, true);
                    pw.showAtLocation(layout, Gravity.CENTER, 0,0);

                    // Create the text view
                    TextView mResultText = (TextView) layout.findViewById(R.id.server_status_text);
                    //mResultText.setTextSize(40);
                    mResultText.setText(location);
//            TextView mResultText = (TextView) layout.findViewById(R.id.server_status_text);
                    Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pw.dismiss();
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }

    }
}
