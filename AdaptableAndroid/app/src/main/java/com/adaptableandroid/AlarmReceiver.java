package com.adaptableandroid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Connie on 4/23/2015.
 */
public class AlarmReceiver extends BroadcastReceiver{
    Context context;
    Address mAddress;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;

//    Location mLastLocation;
    SharedPreferences sp;

    public static String TAG_CURRENT_LONGITUDE = "current_longitude";
    public static String TAG_CURRENT_LATITUDE = "current_latitude";
    public static String TAG_ZIP = "zip_id";
    public static String TAG_CITY = "city";
    public static String TAG_NEW_DROUGHT_CONDITION = "New_Drought_Condition";
    public static String TAG_NEW_DROUGHT_PERCENT = "New_Drought_Percent";
    public static String TAG_WEEK = "Week";
    public static String TAG_FIPS = "FIPS";
    public static String TAG_COUNTY = "County";
    public static String TAG_STATE = "State";
    public static String TAG_NOTHING = "Nothing";
    public static String TAG_D0 = "D0";
    public static String TAG_D1 = "D1";
    public static String TAG_D2 = "D2";
    public static String TAG_D3 = "D3";
    public static String TAG_D4 = "D4";
    public static String TAG_VALID_START = "ValidStart";
    public static String TAG_VALID_END = "ValidEnd";

    public static double NOTIFY_THRESHOLD = 50.0;
    public static String THRESHOLD_D0 = "Abnormally Dry";
    public static String THRESHOLD_D1 = "Moderate Drought";
    public static String THRESHOLD_D2 = "Severe Drought";
    public static String THRESHOLD_D3 = "Extreme Drought";
    public static String THRESHOLD_D4 = "Exceptional Drought (the worst possible measurement)";

    private static double[] droughtLevelPercentages = {0, 1, 2, 3, 4, 5};
    public static String[] droughtLevelTitles = {TAG_NOTHING, THRESHOLD_D0, THRESHOLD_D1, THRESHOLD_D2, THRESHOLD_D3, THRESHOLD_D4};


    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        WakeLocker.acquire(context);
        Log.d("check", "if there is disaster update through alarmReceiver");
        sp = context.getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE);

        checkLocation();
//        buildGoogleApiClient();
//        mGoogleApiClient.connect();


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

//    @Override
//    public void onConnected(Bundle connectionHint){
//        while(mLastLocation == null){
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                    mGoogleApiClient);
//
//        }
//            Log.d("my Location is not null", "TRUE");
//            checkLocation();
////            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
////            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // The connection has been interrupted.
//        // Disable any UI components that depend on Google APIs
//        // until onConnected() is called.
//
//        Log.d("GOOGLE_API_CLIENT", "suspended");
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // This callback is important for handling errors that
//        // may occur while attempting to connect with Google.
//
//        // More about this in the next section.
//        Log.e("GOOGLE_API_CLIENT", "Failed to connect");
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    /*http://www.vogella.com/tutorials/AndroidNotifications/article.html*/
    public void sendNotification(StringUtils.DisasterType disasterTag, int droughtCondition, double percent){
//        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
        Log.d("OK", "AlarmReceiver.onReceive");

        Vibrator vib=(Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);    //for Vibration
        vib.vibrate(2000);

//        String message = intent.getStringExtra("message");
//        String title = intent.getStringExtra("title");

        Intent notIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_ic)
                        .setContentTitle("NEW " + disasterTag.value + " WARNING!")
                        .setContentText(droughtLevelTitles[droughtCondition] + ", now at " + percent + "%")
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);

        //Sets ID for notification
        int mNotificationId = 001;
        //Gets instance of NotificationManager service

        NotificationManagerCompat mNotifyManager = NotificationManagerCompat.from(context);
        // Builds the notification and issues it.
        mNotifyManager.notify(mNotificationId, mBuilder.build());
        WakeLocker.release();

    }


//    public Address getAddress(double latitude, double longitude){
////        StringBuilder result = new StringBuilder();
//        try {
//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if(addresses.size() > 0){
////                System.out.println("Address: " + addresses.get(0).getLocality() + ", zip: " + addresses.get(0).getPostalCode());
//                return addresses.get(0);
////                result.append(address.getLocality()).append("\n");     // city
////                result.append(address.getSubAdminArea()).append("\n"); // null
////                result.append(address.getPostalCode()).append("\n");   // zip code
////                result.append(address.getAdminArea()).append("\n");    // state
////                result.append(address.getCountryName());               // country
//            }
//        } catch(Exception e){
//            Log.e("address tag", e.getMessage());
//        }
//        return null;
////        return result.toString();
//    }

    public String getZipCode(){
        return mAddress.getPostalCode();
    }

    public String getCity(){
        return mAddress.getLocality();
    }

    public String getState(){
        return mAddress.getAdminArea();
    }

    public String getCountry(){
        return mAddress.getCountryName();
    }

    public Address checkLocation(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null){
//          Location location_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//          Location location_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            System.out.println("Got a location manager");
            if(isNetworkEnabled | gpsIsEnabled){
                String location;

//                Criteria criteria = new Criteria();
//                String bestProvider = locationManager.getBestProvider(criteria, false);
//                Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
                Location currentLocation = LocationUtils.getLastKnownLocation(locationManager);
                if(isNetworkEnabled){
                    System.out.println("network is enabled.");
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                else if(gpsIsEnabled){
                    System.out.println("gps is enabled");
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
//                System.out.println("Current location latitude, longitude:" + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                try{

//                    if(mLastLocation != null){
//                        currentLocation = mLastLocation;
//                    }
                    Double latitude = Double.parseDouble(sp.getString(TAG_CURRENT_LATITUDE, "0")); // = currentLocation.getLatitude()
                    Double longitude = Double.parseDouble(sp.getString(TAG_CURRENT_LONGITUDE, "0")); // = currentLocation.getLongitude()

                    location = latitude + " " + longitude;
                    mAddress = LocationUtils.getAddress(context, latitude, longitude);

                }catch(Exception e){
                    location = "";
                    e.printStackTrace();
                }

                if(!StringUtils.stringIsEmpty(location)){
                    new CheckLocationRisk().execute();
                }
                return mAddress;
            }
        }
        return null;
    }

    private class CheckLocationRisk extends AsyncTask<String, String, String> {
        private static final String URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/checkLocationRisk.php";
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String ... urls){

            try{
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpGetRequest(URL, TAG_ZIP, getZipCode());
                System.out.println("Checking result of checkLocationRisk.php:" + jsonObject.toString());

                if(jsonObject.getString(StringUtils.TAG_SUCCESS).equals("1")) {
                    Log.d("checkLocationRisk:", "Success!");

                    JSONArray products = jsonObject.getJSONArray(StringUtils.TAG_PRODUCTS);
                    JSONObject object = products.getJSONObject(0);

                    checkDroughtConditionForZip(getZipCode(), object);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void checkDroughtConditionForZip(String zip, JSONObject externalDBDroughtCondition){

        // Now, check this jsonObject against the values supplied in the sqlitedatabase
        DatabaseHelper myDBHelper =  new DatabaseHelper(context);
        try {
            String week =  externalDBDroughtCondition.getString(TAG_WEEK);
            String county =  externalDBDroughtCondition.getString(TAG_COUNTY);
            droughtLevelPercentages[0] =  externalDBDroughtCondition.getDouble(TAG_NOTHING);
            droughtLevelPercentages[1] =  externalDBDroughtCondition.getDouble(TAG_D0);
            droughtLevelPercentages[2] =  externalDBDroughtCondition.getDouble(TAG_D1);
            droughtLevelPercentages[3] =  externalDBDroughtCondition.getDouble(TAG_D2);
            droughtLevelPercentages[4] =  externalDBDroughtCondition.getDouble(TAG_D3);
            droughtLevelPercentages[5] =  externalDBDroughtCondition.getDouble(TAG_D4);
            String validStart =  externalDBDroughtCondition.getString(TAG_VALID_START);
            String validEnd =  externalDBDroughtCondition.getString(TAG_VALID_END);

            myDBHelper.openDatabase();
//                    myDBHelper.getDroughtInfo();
            Cursor sqliteObject = myDBHelper.getDroughtConditionForZip(zip);
            int newDroughtCond = -1;
            double newDroughtPercent = -1;
            if(sqliteObject == null){
                System.out.println("Adding new location " + zip + " to my risk locations");
                if(droughtLevelPercentages[0] == 100){
                    newDroughtCond = 0;
                    newDroughtPercent = droughtLevelPercentages[0];
                }else{
                    System.out.println("There is a drought. Must be d1 or higher. " + (droughtLevelPercentages.length) + " total drought levels");
                    for(int i = 1; i < droughtLevelPercentages.length; i++){
                        if(droughtLevelPercentages[i] < 100 || i == (droughtLevelPercentages.length - 1)){// && droughtLevelPercentages[i] < 50){
                            newDroughtCond = i;
                            newDroughtPercent = droughtLevelPercentages[i];
                            break;
                        }

//                            else if(droughtLevelPercentages[i] < 100 && droughtLevelPercentages[i] > 50){
//                                newDroughtCond = i;
//                                newDroughtPercent = droughtLevelPercentages[i];
//                            }
                    }
                }
                System.out.println("new drought condition: " + newDroughtCond);
                System.out.println("new drought percentage: " + newDroughtPercent);
                myDBHelper.addDroughtLocation(zip, newDroughtCond, newDroughtPercent);
                if(newDroughtCond >= 3){
                    sendNotification(StringUtils.DisasterType.DROUGHT, newDroughtCond, newDroughtPercent);
                }
            }
            else{
                if(sqliteObject.moveToFirst()){

                    // I want to change the parameters no matter what, but first I want to check
                    // if it is necessary to notify the user that their drought conditions
                    // have changed.
                    newDroughtCond = sqliteObject.getInt(sqliteObject.getColumnIndex(StringUtils.TAG_DROUGHT_COND));
                    newDroughtPercent = sqliteObject.getDouble(sqliteObject.getColumnIndex(StringUtils.TAG_PERCENTAGE));
                    System.out.println("Found from personal drought table: " + sqliteObject.getString(sqliteObject.getColumnIndex(StringUtils.TAG_ZIP)));
                    System.out.println("Drought condition and drought percent: " + sqliteObject.getString(sqliteObject.getColumnIndex(StringUtils.TAG_DROUGHT_COND)) + ", " + sqliteObject.getString(sqliteObject.getColumnIndex(StringUtils.TAG_PERCENTAGE)));

                    if(droughtLevelPercentages[newDroughtCond] != newDroughtPercent){
                        if(droughtLevelPercentages[newDroughtCond] >= 100
                                && newDroughtCond < (droughtLevelPercentages.length -1)){
                            newDroughtCond = newDroughtCond + 1;
                            sendNotification(StringUtils.DisasterType.DROUGHT, newDroughtCond, droughtLevelPercentages[newDroughtCond]);
                        }
                        else if(droughtLevelPercentages[newDroughtCond] == 0
                                && newDroughtCond > 0){
                            newDroughtCond = newDroughtCond - 1;
                            sendNotification(StringUtils.DisasterType.DROUGHT, newDroughtCond, droughtLevelPercentages[newDroughtCond]);
                        }
                        newDroughtPercent = droughtLevelPercentages[newDroughtCond];
                        myDBHelper.updateDroughtLocation(zip, newDroughtCond, newDroughtPercent);
                    }
                    // in the case where the current drought condition is 100, but the new drought condition remains
                    // 100 but the other drought conditions keep increasing
                    else if(droughtLevelPercentages[newDroughtCond] == 100 && newDroughtCond < (droughtLevelPercentages.length -1)){
                        if(droughtLevelPercentages[newDroughtCond+1] > 0){
                            newDroughtCond = newDroughtCond+1;
                            newDroughtPercent = droughtLevelPercentages[newDroughtCond];
                            sendNotification(StringUtils.DisasterType.DROUGHT, newDroughtCond, newDroughtPercent);
                            myDBHelper.updateDroughtLocation(zip, newDroughtCond, newDroughtPercent);
                        }
                    }

                }
            }
            System.out.println("AlarmReceiver: About to add locations to shared preferences");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(TAG_CITY, mAddress.getLocality());
            editor.putString(TAG_ZIP, mAddress.getPostalCode());
            editor.putString(TAG_STATE, mAddress.getAdminArea());
            editor.putInt(TAG_NEW_DROUGHT_CONDITION, newDroughtCond);
            editor.putFloat(TAG_NEW_DROUGHT_PERCENT, (float)newDroughtPercent);
//            editor.putString(TAG_CURRENT_LATITUDE, mAddress.getLatitude() + "");
//            editor.putString(TAG_CURRENT_LONGITUDE, mAddress.getLongitude() + "");
            editor.commit();
            System.out.println("AlarmReceiver: Added locations to shared preferences");
        }catch(Exception e){
            System.out.println("Cannot open database to check drought conditions in your area b/c: " + e.toString());
        }

    }
}
