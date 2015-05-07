package com.adaptableandroid;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adaptableandroid.com.adaptableandroid.models.ChildItem;
import com.adaptableandroid.com.adaptableandroid.models.GroupItem;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;


public class MainActivity extends ActionBarActivity {
    public static int TEST_TIME_OUT = 3000;
    public final static String EXTRA_MESSAGE = "com.example.connie.myfirstapp.MESSAGE";
    private LocationManager locationManager;
    private String bestProvider;
    private String location;
    PopupWindow pw;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;

    ExpandableListAdapter explistAdapter;
    AnimatedExpandableListView expListView;
//    List<String> listDataHeader;
//    HashMap<String, List<String>> listDataChild;
//    List<String> facts;
//    List<String> impacts;
    List<GroupItem> groupItems;

    private final static String TAG_PRODUCTS = "products";
    private final static String TAG_ID = "id";
    private final static String TAG_IMPACT = "impacts";
    private final static String TAG_LINK = "Link";
    private final static String TAG_FACT = "Fact";
    public final static String MYPREFERENCES = "MyPrefs";
    public final static String NOTHING_TO_DISPLAY = "Nothing to display";
    public final static String FACT_TITLE = "Fact of the day";
    public final static String IMPACT_TITLE = "Impact of the day";
    public final static String APP_TAG = "APP_TAG";
    JSONArray products = null;
    String alreadyUpdated = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflator.inflate(R.layout.custom_menu, null);
        actionbar.setCustomView(view);

    }

    @Override
    public void onStart(){
        super.onStart();
//
        AlarmService alarmService = new AlarmService(getBaseContext());
        alarmService.startAlarm();
//        setAlarm();

        if(!isNetworkAvailable()){return;}
        expListView = (AnimatedExpandableListView) findViewById(R.id.lvExp);
        expListView.setGroupIndicator(null);
        sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        groupItems = new ArrayList<GroupItem>();
//        listDataHeader = new ArrayList<String>();
//        listDataChild = new HashMap<String, List<String>>();
//        facts = new ArrayList<String>();
//        impacts = new ArrayList<String>();

        if(!sharedPreferences.contains(alreadyUpdated)){
            new DisplayInfo().execute();
        }
        else{
            addImpactAndFactWithGroupItem(sharedPreferences);
        }

        expListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {
            int lastView = -1;

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
                // call collapseGroupWithAnimation(int) and expandGroupWithAnimation(int)
                // to animate group expansion/collapse.
                if(expListView.isGroupExpanded(groupPosition)){
                    expListView.collapseGroupWithAnimation(groupPosition);
                }else{
                    if(lastView != -1){
                        expListView.collapseGroupWithAnimation(lastView);
                    }

                    expListView.expandGroupWithAnimation(groupPosition);
                    lastView = groupPosition;
                }
                return true;
            }

        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                expListView.collapseGroupWithAnimation(groupPosition);
                return true;
            }
        });
    }

    private void setAdapter(){
        //                    explistAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
        expListView = (AnimatedExpandableListView) findViewById(R.id.lvExp);
        expListView.setGroupIndicator(null);
        explistAdapter = new ExpandableListAdapter(MainActivity.this, groupItems);
        expListView.setAdapter(explistAdapter);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addImpactAndFactsTestWithGroupItem(){
        String impact = "Test Impact!";
        String fact = "Test Fact!";
        List<GroupItem> groupItems = new ArrayList<GroupItem>();
        if(!StringUtils.stringIsEmpty(impact)){
            final String im = "Impact";
            System.out.println("Impact: " + impact);

            GroupItem groupItem = new GroupItem(im);
            List<ChildItem> items = new ArrayList<ChildItem>();
            items.add(new ChildItem(impact));
//            items.add(new ChildItem("hello world"));
//            items.add(new ChildItem("blah"));

            groupItem.setChildren(items);
            groupItems.add(groupItem);
        }

        if(!StringUtils.stringIsEmpty(fact)){
            System.out.println("Fact: " + fact);

            GroupItem groupItem = new GroupItem(TAG_FACT);
            List<ChildItem> items = new ArrayList<ChildItem>();
            items.add(new ChildItem(fact));
//            items.add(new ChildItem("fact2"));
//            items.add(new ChildItem("fact3EXTREMELY LONG TO TEST THE FUNCTIONALITY OF
//                                      THE APP AND THE RESPONSIVE PORITION OF THE ANDROID APP"));

            groupItem.setChildren(items);
            groupItems.add(groupItem);
        }
        GroupItem groupItem = new GroupItem(NOTHING_TO_DISPLAY);
        List<ChildItem> items = new ArrayList<ChildItem>();
        items.add(new ChildItem(fact));
        groupItem.setChildren(items);
        groupItems.add(groupItem);
        explistAdapter = new ExpandableListAdapter(this, groupItems);
        expListView.setAdapter(explistAdapter);
    }

    private void addImpactAndFactWithGroupItem(SharedPreferences sp){
        String impact = sp.getString(TAG_IMPACT, NOTHING_TO_DISPLAY);
        String fact = sp.getString(TAG_FACT, NOTHING_TO_DISPLAY);
        groupItems = new ArrayList<GroupItem>();
        if(!StringUtils.stringIsEmpty(impact)){
            System.out.println("Impact: " + impact);

            GroupItem groupItem = new GroupItem(IMPACT_TITLE);
            List<ChildItem> items = new ArrayList<ChildItem>();
            items.add(new ChildItem(impact));
            groupItem.setChildren(items);
            groupItems.add(groupItem);
        }

        if(!StringUtils.stringIsEmpty(fact)){
            System.out.println("Fact: " + fact);

            GroupItem groupItem = new GroupItem(FACT_TITLE);
            List<ChildItem> items = new ArrayList<ChildItem>();
            items.add(new ChildItem(fact));
            groupItem.setChildren(items);
            groupItems.add(groupItem);
        }
        setAdapter();
//        explistAdapter = new ExpandableListAdapter(this, groupItems);
//        expListView.setAdapter(explistAdapter);
    }

//    public void addImpactAndFact(){
//        System.out.println("RECREATING VIEW");
//        String impact = sharedPreferences.getString(TAG_IMPACT, NOTHING_TO_DISPLAY);
//        String fact = sharedPreferences.getString(TAG_FACT, NOTHING_TO_DISPLAY);
//        if(!stringIsEmpty(impact)){
//            final String im = "Impact";
//            System.out.println("Impact: " + impact);
//            listDataHeader.add(im);
//            impacts.add(impact);
//            listDataChild.put(im, impacts);
//        }
//
//        if(!stringIsEmpty(fact)){
//            System.out.println("Fact: " + fact);
//            listDataHeader.add(TAG_FACT);
//            facts.add(fact);
//            listDataChild.put(TAG_FACT, facts);
//
//        }
//        explistAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//        expListView.setAdapter(explistAdapter);
//    }

/*
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
*/

    public void goToDisasters(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_anim));
        Intent intent = new Intent(this, DisplayDisastersActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Hello, there");
        startActivity(intent);

    }

    public void createNotification(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_anim));
        createNotification();
    }

    /*http://www.vogella.com/tutorials/AndroidNotifications/article.html*/
    public void createNotification(){
        //Prepare intent which is triggered
        // if the notification is selected
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);



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

    public String getAddress(double latitude, double longitude){
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getSubAdminArea()).append("\n");
                result.append(address.getPostalCode()).append("\n");
                result.append(address.getAdminArea()).append("\n");
                result.append(address.getCountryName());
            }
        } catch(Exception e){
            Log.e("tag", e.getMessage());
        }
        return result.toString();
    }

    public void checkLocation(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_anim));
        checkLocation();
    }

    public void checkLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager != null){
//          Location location_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//          Location location_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
                    location = getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());
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
                    TextView mResultText = (TextView) layout.findViewById(R.id.popup_text);
                    mResultText.setText(location);
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

    private class DisplayInfo extends AsyncTask<String, String, String>{
        private static final String IMPACTS_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/grabImpacts.php";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting to update...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls){
            // Check for success tag
            int success;
            try{
//                Log.d("Impact and Fact Request", "Starting");
                jsonObject = jsonParser.getJSONFromURL(IMPACTS_URL);
//                Log.d("Checking result:", jsonObject.toString());

            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(String someString){
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        pDialog.dismiss();
                    }}, 1000);  // 1000 milliseconds
                if(jsonObject == null){
                    System.out.println("jsonObject is null");
                }
                if(jsonObject != null && !jsonObject.toString().isEmpty()){
                    products = jsonObject.getJSONArray(TAG_PRODUCTS);
                    // groups will be impact, facts, restrictions (these are the headers)
                    // in each group, what we grab from json is what we will display

                    for(int i = 0; i < products.length(); i++){
                        JSONObject object = products.getJSONObject(i);

                        String id = object.getString(TAG_ID);
                        String impact = object.getString(TAG_IMPACT);
                        String link = object.getString(TAG_LINK);
                        String fact = object.getString(TAG_FACT);

                        SharedPreferences.Editor editor = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.putString(alreadyUpdated, "already Updated");

                        if(!StringUtils.stringIsEmpty(impact)){
                            System.out.println("Impact: " + impact);
//                            listDataHeader.add(im);
//                            impacts.add(impact);
//                            listDataChild.put(im, impacts);
                            GroupItem groupItem = new GroupItem(IMPACT_TITLE);
                            List<ChildItem> items = new ArrayList<ChildItem>();
                            items.add(new ChildItem(impact));

                            groupItem.setChildren(items);
                            groupItems.add(groupItem);

                            editor.putString(TAG_IMPACT, impact);

                        }

                        if(!StringUtils.stringIsEmpty(fact)){
                            System.out.println("Fact: " + fact);
//                            listDataHeader.add(TAG_FACT);
//                            facts.add(fact);
//                            listDataChild.put(TAG_FACT, facts);
                            GroupItem groupItem = new GroupItem(FACT_TITLE);
                            List<ChildItem> items = new ArrayList<ChildItem>();
                            items.add(new ChildItem(fact));
                            groupItem.setChildren(items);
                            groupItems.add(groupItem);

                            editor.putString(TAG_FACT, fact);
                        }
                        editor.commit();
                    }

                    setAdapter();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
