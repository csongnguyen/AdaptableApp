package com.adaptableandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adaptableandroid.com.adaptableandroid.models.ChildItem;
import com.adaptableandroid.com.adaptableandroid.models.GroupItem;
import com.adaptableandroid.com.adaptableandroid.models.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by Connie on 4/17/2015.
 */
public class DisplayDisastersActivity extends ActionBarActivity {
    MyAppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    public static int goToDisasterNumber;

    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    public SharedPreferences sharedPreferences;

    ExpandableListAdapter explistAdapter;
    List<GroupItem> groupItems;
    List<List<Integer>> myLists;

    String alreadyUpdated = "";
    private final static String TAG_ID = "id";
    private final static String TAG_TITLE = "Title";
    private final static String TAG_SOURCE = "Source";
    private final static String TAG_IMPACT = "Impact";

    private final static String TAG_FACT = "Fact";
    private final static String TAG_LINK = "Link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_disasters);


        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.home_icon2);
        actionbar.setDisplayShowTitleEnabled(true);

//        actionbar.setDisplayShowHomeEnabled(true);
//        actionbar.setHomeButtonEnabled(true);
//        actionbar.setHomeButtonEnabled(true);
//        actionbar.setIcon(R.drawable.settings_menu);
//        actionbar.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF5BA4F3));//0xFF4697b5


//        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);
//        TextView tView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbarTitle);
//        tView.setText("Disasters");

//        Log.d("ON_CREATE", "true  in DisplayDisastersActivity");

    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
//        finish();
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            goToDisasterNumber = data.getIntExtra(StringUtils.ARG_DISASTER_NUMBER, 0);
//            new DisplayInfo().execute();
//            System.out.println("Passing int goToDisasterNumber to DisplayDisastersActivity from onActivityResult: " + goToDisasterNumber);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isNetworkAvailable()) {
            return;
        }
        Log.d("ON_START", "true  in DisplayDisastersActivity");
        sharedPreferences = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE);
        new DisplayInfo().execute();
    }

    private ExpandableListAdapter setAdapter(List<GroupItem> gItems, int percentRisk){
        return new ExpandableListAdapter(DisplayDisastersActivity.this, gItems, "", percentRisk);
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    private class MyAppSectionsPagerAdapter extends FragmentPagerAdapter {
        Map<Integer, MyLaunchpadSectionFragment> fragmentMap;
//        List <ExpandableListAdapter> impactAdapters, factAdapters;
        List<ExpandableListAdapter> expAdapters;
        List<List<Integer>> checklistStatuses;
        SharedPreferences sp;

        public MyAppSectionsPagerAdapter(FragmentManager fm, List<ExpandableListAdapter> a1, List<List<Integer>> checklistStatuses, SharedPreferences sp) {

            super(fm);
            fragmentMap = new HashMap<Integer, MyLaunchpadSectionFragment>();
            expAdapters = a1;
            this.checklistStatuses = checklistStatuses;
            this.sp = sp;
        }

        @Override
        public MyLaunchpadSectionFragment getItem(int i) {

            if(!fragmentMap.containsKey(i)){
                MyLaunchpadSectionFragment fragment = new MyLaunchpadSectionFragment();
                Bundle args = new Bundle();

                List<Integer> checklistStatus = checklistStatuses.get(i);
                int totalCompleted = sp.getInt(StringUtils.CHECKLIST_COMPLETED, -1);
                int total = sp.getInt(StringUtils.CHECKLIST_TOTAL, -1);
//                if(totalCompleted == -1){

                    totalCompleted = 0;
                    for(int index = 0; index < checklistStatus.size();index++){
                        totalCompleted += checklistStatus.get(index);
                    }
//                }
//                if(total == -1){
                    total = checklistStatus.size();
//                }

                int drought_condition = sp.getInt(AlarmReceiver.TAG_NEW_DROUGHT_CONDITION, 0);
                String city = sp.getString(AlarmReceiver.TAG_CITY, "Cupertino");


                args.putString(StringUtils.ARG_DISASTER, StringUtils.disasterTypes[i]);
                args.putInt(StringUtils.ARG_DISASTER_NUMBER, i);
                args.putInt(StringUtils.ARG_TOTAL_DISASTERS, getCount());
                args.putInt(StringUtils.CHECKLIST_COMPLETED, totalCompleted);
                args.putInt(StringUtils.CHECKLIST_TOTAL,total );
                args.putInt(MyLaunchpadSectionFragment.ARG_DROUGHT_PERCENT, expAdapters.get(0).getPercentRisk());
                args.putInt(MyLaunchpadSectionFragment.ARG_DROUGHT_COND, drought_condition);
                args.putString(MyLaunchpadSectionFragment.ARG_CITY, city);



                fragment.setArguments(args);
                fragment.setSharedPreferences(sp);
                if(i < 2){
                    fragment.setAdapter1(expAdapters.get(i));
                }else{
                    fragment.setAdapter1(expAdapters.get(0));
                }

                System.out.println("Grabbing a fragment");
                fragmentMap.put(i, fragment);

                return fragment;
            }
            else{
                return fragmentMap.get(i);
            }
        }

        @Override
        public int getCount() {
            return StringUtils.disasterTypes.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class MyLaunchpadSectionFragment extends Fragment {
//        public static final String CHECKLIST_COMPLETED = "checklist_completed";
//        public static final String CHECKLIST_TOTAL = "checklist_total";
//        public static final String ARG_DISASTER = "disaster_type";
//        public static final String ARG_DISASTER_NUMBER = "disaster_number";
//        public static final String ARG_TOTAL_DISASTERS = "total";
        public static final String ARG_DROUGHT_PERCENT = "drought_percent";
        public static final String ARG_DROUGHT_COND = "drought_condition";
        public static final String ARG_CITY = "city";
        public ImageView lArrow, rArrow;
        private View rootView;
        SharedPreferences sp;

        ExpandableListAdapter adapt1, adapt2;

        public void setAdapter1(ExpandableListAdapter a){
            adapt1 = a;
        }

        public void setSharedPreferences(SharedPreferences sp){
            this.sp = sp;

        }

//        public void setAdapter2(ExpandableListAdapter a){
//            adapt2 = a;
//        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_display_disasters, container, false);
            rootView.setVisibility(View.VISIBLE);
            System.out.println("Creating view of fragment");
            final Bundle args = getArguments();

            ((TextView) rootView.findViewById(R.id.disasterTitle)).setText(
                    args.getString(StringUtils.ARG_DISASTER));

            ((TextView) rootView.findViewById(R.id.disasterPageNumber)).setText(
                    (args.getInt(StringUtils.ARG_DISASTER_NUMBER) + 1) + " of " + args.getInt(StringUtils.ARG_TOTAL_DISASTERS));


            TextView riskStatus = (TextView) rootView.findViewById(R.id.my_risk_status_text);
            String city = args.getString(ARG_CITY);
            switch(args.getString(StringUtils.ARG_DISASTER)){
                case "Drought":
                    int drought_percent = args.getInt(ARG_DROUGHT_PERCENT);
                    int drought_condition = args.getInt(ARG_DROUGHT_COND);
                    rootView.findViewById(R.id.disasterBackground).setBackgroundResource(R.drawable.drought_background);
                    riskStatus.setText(Html.fromHtml("<b>" + drought_percent + "%</b> of " + city + " is in " + AlarmReceiver.droughtLevelTitles[drought_condition]));
                    riskStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.showPopup(getActivity(), "We are currently working on improving your risk analysis!");
                        }
                    });
                    break;
                case "Earthquake":
                    rootView.findViewById(R.id.disasterBackground).setBackgroundResource(R.drawable.bg_earthquake);
                    riskStatus.setClickable(true);

                    riskStatus.setText("How far are you from a fault zone?");
                    riskStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Uri uri = Uri.parse("http://www.redtreesoft.com/google/");
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(intent);
                            DisplayDisastersActivity.goToDisasterNumber = args.getInt(StringUtils.ARG_DISASTER_NUMBER);
                            Intent intent = new Intent(getActivity(), WebViewActivity.class);
                            intent.putExtra(StringUtils.WEB_VIEW, "http://www.redtreesoft.com/google/");
                            startActivity(intent);

                        }
                    });
//                    riskStatus.setText(Html.fromHtml("<a style='text-decoration:none; color: rgb(0,0,0);' href=''>How far are you from a fault zone?</a>"));
                    break;
                case "Wildfire":
                    rootView.findViewById(R.id.disasterBackground).setBackgroundResource(R.drawable.bg_wildfire);
                    riskStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.showPopup(getActivity(), "We are currently working on improving your risk analysis!");
                        }
                    });
                    break;
                default:
                    break;
            }



            final AnimatedExpandableListView impactView = (AnimatedExpandableListView) rootView.findViewById(R.id.impactList);
//            final AnimatedExpandableListView factView = (AnimatedExpandableListView) rootView.findViewById(R.id.factList);
            if(impactView == null){
                Log.d("impactView", "NULL from mylaunchpadSectionFragment");
            }

            setOnClickListener(impactView, adapt1);// = (AnimatedExpandableListView) rootView.findViewById(R.id.explist2);


            ImageView leftArrow = (ImageView) rootView.findViewById(R.id.riskscreen_left);
            ImageView rightArrow = (ImageView) rootView.findViewById(R.id.riskscreen_right);
            if(args.getInt(StringUtils.ARG_DISASTER_NUMBER) == 0 ){
                leftArrow.setVisibility(View.GONE);
            }else{
                System.out.println("Setting leftArrow Image "+ leftArrow.getAlpha() + " for " + args.getString(StringUtils.ARG_DISASTER));
            }
            if(args.getInt(StringUtils.ARG_DISASTER_NUMBER) == args.getInt(StringUtils.ARG_TOTAL_DISASTERS) - 1){
                rightArrow.setVisibility(View.GONE);
            }

            lArrow = leftArrow;
            rArrow = rightArrow;

            ImageView checklistPreview = (ImageView)rootView.findViewById(R.id.checklist_icon);
            checklistPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.image_anim));
                        Log.d("Passing disaster #", " to checklist " + args.getInt(StringUtils.ARG_DISASTER_NUMBER));
                        Intent intent = new Intent(v.getContext(), DisplayChecklistActivityWithFragment.class);
                        intent.putExtra(StringUtils.ARG_DISASTER_NUMBER, args.getInt(StringUtils.ARG_DISASTER_NUMBER));
                        startActivityForResult(intent, 1);
//                        startActivity(intent);
                }
            });
            TextView checklistPercent = (TextView)rootView.findViewById(R.id.checklist_percent_disaster);

            int totalCompleted = sp.getInt(StringUtils.CHECKLIST_COMPLETED + args.getInt(StringUtils.ARG_DISASTER_NUMBER), -1);
            int total = sp.getInt(StringUtils.CHECKLIST_TOTAL + args.getInt(StringUtils.ARG_DISASTER_NUMBER), -1);
            if(total == -1 || totalCompleted == -1){
                checklistPercent.setText(Html.fromHtml("<b>" + (int)(100*((double)args.getInt(StringUtils.CHECKLIST_COMPLETED)/args.getInt(StringUtils.CHECKLIST_TOTAL))) + "%<b>"));
            }else{
                checklistPercent.setText(Html.fromHtml("<b>" + (int)(100*((double)totalCompleted/total)) + "%<b>"));
            }


//            CustomGauge gauge = (CustomGauge) rootView.findViewById(R.id.gaugeForRisk);
//            gauge.setValue(args.getInt(ARG_DROUGHT_PERCENT));

            return rootView;
        }

        public void setOnClickListener(final AnimatedExpandableListView expListView, ExpandableListAdapter adapter){
            if(expListView == null){
                Log.d("explistview", "NULL from mylaunchpadSectionFragment");
            }
            else if(expListView !=  null){
//                Log.d("explistview", "NOT NULL from mylaunchpadSectionFragment");
                if(adapter != null){
                    expListView.setAdapter(adapter);
                }
                else{
                    Log.d("expandable adapter", "NULL from mylaunchpadsectionfragment");
                }
                expListView.setGroupIndicator(null);
                expListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {
                    int lastView = -1;
                    ImageView lastDownArrow = null;

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
                        // call collapseGroupWithAnimation(int) and expandGroupWithAnimation(int)
                        // to animate group expansion/collapse.
                        ImageView downArrow = (ImageView) v.findViewById(R.id.down_arrow);
                        if(expListView.isGroupExpanded(groupPosition)){
                            expListView.collapseGroupWithAnimation(groupPosition);
                            downArrow.setVisibility(View.VISIBLE);
                        }else{
                            if(lastView != -1){
                                expListView.collapseGroupWithAnimation(lastView);
                                if(lastDownArrow != null){
                                    lastDownArrow.setVisibility(View.VISIBLE);
                                }
                            }

//                            setFadeOut(downArrow);

                            downArrow.setVisibility(View.INVISIBLE);
                            expListView.expandGroupWithAnimation(groupPosition);
                            lastView = groupPosition;
                            lastDownArrow = downArrow;
                        }
                        return true;
                    }

//                    private void setFadeOut(final ImageView img){
//                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(img, "alpha", img.getAlpha(), 0.1f);
//                        fadeOut.setDuration(200);
//
//                        final AnimatorSet mAnimationSet = new AnimatorSet();
//                        mAnimationSet.play(fadeOut);
//                        mAnimationSet.start();
//
//                        mAnimationSet.addListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                img.setVisibility(View.INVISIBLE);
//                            }
//                        });
//
//                    }

                });
                expListView.setOnChildClickListener(new AnimatedExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        expListView.collapseGroupWithAnimation(groupPosition);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_without_profile, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        Log.d("id-type", "onOptionsItemSelected in Display " + id);

//        if(id == android.R.id.icon){
//            System.out.println("THIS IS AN ICON PRESSED ONOPTIONSITEMSELECTED IN DISPLAYDISASTERS");
//        }
//        if(id == android.R.id.home){
//                Intent homeIntent = new Intent(this, MainActivity.class);
//                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(homeIntent);
//        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Set the text view as the activity layout
            try{
                // We need to get the instance of the LayoutInflater, use the context of this activity
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                //Inflate the view from a predefined XML layout
                View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_element));

                // create a 300px width and 470px height PopupWindow
                final PopupWindow pw = new PopupWindow(layout, 400, 400, true);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

                // Create the text view
                TextView mResultText = (TextView) layout.findViewById(R.id.popup_text);
                mResultText.setText("Settings will be coming soon!");
                Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();
                    }
                });

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", .3f, 1f);
                fadeIn.setDuration(200);

                final AnimatorSet mAnimationSet = new AnimatorSet();
                mAnimationSet.play(fadeIn);
                mAnimationSet.start();

            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void goToChecklist(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_anim));
        Intent intent = new Intent(this, DisplayChecklistActivityWithFragment.class);
        startActivity(intent);
    }

    private class DisplayInfo extends AsyncTask<String, String, String>{
        private static final String IMPACTS_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/grabImpacts.php";
        private static final String POPULATE_CHECKLIST_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/getChecklist.php";
        JSONObject jsonObject;
        JSONObject[] jsonObjects = new JSONObject[StringUtils.disasterTypes.length];

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayDisastersActivity.this);
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
                for(int i = 0; i < jsonObjects.length; i++){
                    jsonObjects[i] = jsonParser.makeHttpGetRequest(POPULATE_CHECKLIST_URL, DisplayChecklistActivityWithFragment.DISASTER_TYPE, StringUtils.disasterTypes[i]);
//                    Log.d("Checking result:", jsonObjects[i].toString());
                }

            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String someString){
            try {
                pDialog.dismiss();

                // check checklist
                myLists = new ArrayList<List<Integer>>();
                List<Integer> mylist;

                if(jsonObject == null){
                    System.out.println("jsonObject is null");
                }
                if(jsonObject != null && !jsonObject.toString().isEmpty()){

                    SharedPreferences.Editor editor = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE).edit();
                    editor.putString(alreadyUpdated, "already Updated");

                    for(int b = 0; b < jsonObjects.length; b++){
                        /*****CHECKLIST INFORMATION *******/
                        JSONObject jsonObjectA = jsonObjects[b];
                        if(!jsonObjectA.toString().isEmpty()){
                            JSONArray products = jsonObjectA.getJSONArray(StringUtils.TAG_PRODUCTS);
                            mylist = new ArrayList<Integer>();

                            for(int i = 0; i < products.length(); i++){
                                JSONObject object = products.getJSONObject(i);
                                String status = object.getString(DisplayChecklistActivityWithFragment.TAG_STATUS);

                                if(!StringUtils.stringIsEmpty(status)){
                                    mylist.add(Integer.parseInt(status));
                                }
                            }
                            myLists.add(mylist);
                        }

                        /*****END OF CHECKLIST INFORMATION******/
                    }

                    List<ExpandableListAdapter> impactAdapters = new ArrayList<ExpandableListAdapter>();
//                    List<ExpandableListAdapter> factAdapters = new ArrayList<ExpandableListAdapter>();
                    for(int disasterIndex = 0; disasterIndex < 2; disasterIndex++){
                        JSONArray products = jsonObject.getJSONArray(StringUtils.disasterTypes[disasterIndex]);
                        // groups will be impact, facts, restrictions (these are the headers)
                        // in each group, what we grab from json is what we will display
                        groupItems = new ArrayList<GroupItem>();
/******************MY RISK STATUS************************************************/

//                        System.out.println("Checking Location to put in Disasters Page " + disasterIndex);
//                            if(sharedPreferences.contains(AlarmReceiver.TAG_ZIP)
//                                    && sharedPreferences.contains(AlarmReceiver.TAG_NEW_DROUGHT_CONDITION)
//                                    && sharedPreferences.contains(AlarmReceiver.TAG_NEW_DROUGHT_PERCENT)){
//                        Log.d("SYSTEM_PREFERENCES", " does contain zip, condition, and percent");
//                        String zip = sharedPreferences.getString(AlarmReceiver.TAG_ZIP, "00000");
//                        String city = sharedPreferences.getString(AlarmReceiver.TAG_CITY, "Cupertino");
//                        String state = sharedPreferences.getString(AlarmReceiver.TAG_STATE, "California");
//                        int drought_condition = sharedPreferences.getInt(AlarmReceiver.TAG_NEW_DROUGHT_CONDITION, 0);
                        float drought_percent = sharedPreferences.getFloat(AlarmReceiver.TAG_NEW_DROUGHT_PERCENT, 0);

//                        GroupItem groupItem = new GroupItem(city + ", " + state + " " + zip );
//                        groupItem.setHeadLabel("MY RISK STATUS");
//                        groupItem.setSubheader("");
//                        List<ChildItem> items = new ArrayList<ChildItem>();
//                        items.add(new ChildItem(AlarmReceiver.droughtLevelTitles[drought_condition]));
//                        items.add(new ChildItem(drought_percent + "% of my area in drought"));
//                        groupItem.setChildren(items);
//                        groupItems.add(groupItem);
//                            }
                        GroupItem groupItem;
                        List<ChildItem> items;
/******************END OF MY RISK STATUS************************************************/
//                        System.out.println("Length of products: " + products.length());
                        for(int i = 0; i < products.length(); i++){
                            JSONObject object = products.getJSONObject(i);

                            String id = object.getString(TAG_ID);
                            String impactTitle = object.getString(TAG_TITLE);
                            String source = object.getString(TAG_SOURCE);
                            String impact = object.getString(TAG_IMPACT);
                            String fact = object.getString(TAG_FACT);
                            String link = object.getString(TAG_LINK);

                            if(!StringUtils.stringIsEmpty(impact)){
                                 groupItem = new GroupItem(impactTitle);
                                groupItem.setHeadLabel("IMPACT");
                                groupItem.setSubheader("Source: " + source);
                                 items = new ArrayList<ChildItem>();
                                items.add(new ChildItem(impact));

                                groupItem.setChildren(items);
                                groupItems.add(groupItem);

                                editor.putString(TAG_IMPACT, impact);

                            }

                            if(!StringUtils.stringIsEmpty(fact)){
                                 groupItem = new GroupItem(fact);
                                groupItem.setHeadLabel("FACT");
                                groupItem.setSubheader("Source: " + source);
                                 items = new ArrayList<ChildItem>();
                                items.add(new ChildItem(link, true));
                                groupItem.setChildren(items);
                                groupItems.add(groupItem);

                                editor.putString(TAG_FACT, fact);

                            }
                        }
                        impactAdapters.add(setAdapter(groupItems, (int)drought_percent));
                    }
                    editor.commit();

                    mAppSectionsPagerAdapter = new MyAppSectionsPagerAdapter(getSupportFragmentManager(), impactAdapters, myLists, sharedPreferences);
                    mViewPager = (ViewPager) findViewById(R.id.pager_disasters);
                    mViewPager.setAdapter(mAppSectionsPagerAdapter);
                    mViewPager.setCurrentItem(goToDisasterNumber);

                    System.out.println("Setting on page change listener for DisplayDisasterActivity...");
                    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            goToDisasterNumber = mViewPager.getCurrentItem();
                            if(mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).rArrow == null){
                                System.out.println("Apparently the right arrow is null for "+ mViewPager.getCurrentItem());
                            }
                            if(mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).rArrow != null){
                                Log.d("onPageScrolled", "DisplayDisastersActivity now on page " + mViewPager.getCurrentItem());
                                mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).rArrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(mViewPager.getCurrentItem() < (mAppSectionsPagerAdapter.getCount()-1)){
                                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                                        }
                                    }
                                });
                            }
                            if(mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).lArrow != null){
                                mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).lArrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(mViewPager.getCurrentItem() > 0){
                                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
