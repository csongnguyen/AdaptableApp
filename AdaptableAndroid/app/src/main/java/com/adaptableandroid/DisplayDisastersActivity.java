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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adaptableandroid.com.adaptableandroid.models.ChildItem;
import com.adaptableandroid.com.adaptableandroid.models.GroupItem;

import org.json.JSONArray;
import org.json.JSONObject;

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

    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    SharedPreferences sharedPreferences;

    ExpandableListAdapter explistAdapter;
    List<GroupItem> groupItems;

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
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_disasters);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF4697b5));

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView tView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbarTitle);
        tView.setText("Disasters");
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isNetworkAvailable()) {
            return;
        }
        sharedPreferences = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE);

        new DisplayInfo().execute();
    }


//    private void addImpactAndFactWithGroupItem(SharedPreferences sp){
//        String impact = sp.getString(TAG_IMPACT, "nothing to display for Impacts");
//        String fact = sp.getString(TAG_FACT, "nothing to display for Facts");
//        groupItems = new ArrayList<GroupItem>();
//        if(!StringUtils.stringIsEmpty(impact)){
//            System.out.println("Impact: " + impact);
//
//            GroupItem groupItem = new GroupItem(IMPACT_TITLE);
//            List<ChildItem> items = new ArrayList<ChildItem>();
//            items.add(new ChildItem(impact));
//            groupItem.setChildren(items);
//            groupItems.add(groupItem);
//        }
//
//        if(!StringUtils.stringIsEmpty(fact)){
//            System.out.println("Fact: " + fact);
//
//            GroupItem groupItem = new GroupItem(FACT_TITLE);
//            List<ChildItem> items = new ArrayList<ChildItem>();
//            items.add(new ChildItem(fact));
//            groupItem.setChildren(items);
//            groupItems.add(groupItem);
//        }
//        setAdapter();
////        explistAdapter = new ExpandableListAdapter(this, groupItems);
////        expListView.setAdapter(explistAdapter);
//    }



    private ExpandableListAdapter setAdapter(List<GroupItem> gItems, int percentRisk){
//        //                    explistAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
//        expListView = (AnimatedExpandableListView) mViewPager.findViewById(R.id.impactList);
        System.out.println("Setting expandable adapter");
        return new ExpandableListAdapter(DisplayDisastersActivity.this, gItems, "", percentRisk);
//        mAppSectionsPagerAdapter.setExpandableAdapter(explistAdapter);
//        mAppSectionsPagerAdapter.getItem(0).setAdapter(explistAdapter);
//        if(expListView == null){
//   Log.d("Explistview", "is not NULL");
//            expListView.setAdapter(explistAdapter);

//        ListView list = mViewPager
//        ListView list = (ListView) findViewById(R.id.listOfFactualElements);
//        System.out.println("list.toString()" + list.toString());

//        }

    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    private class MyAppSectionsPagerAdapter extends FragmentPagerAdapter {
        Map<Integer, MyLaunchpadSectionFragment> fragmentMap;
//        List <ExpandableListAdapter> impactAdapters, factAdapters;
        List<ExpandableListAdapter> expAdapters;

        public MyAppSectionsPagerAdapter(FragmentManager fm, List<ExpandableListAdapter> a1) {

            super(fm);
            fragmentMap = new HashMap<Integer, MyLaunchpadSectionFragment>();
            expAdapters = a1;

        }


        @Override
        public MyLaunchpadSectionFragment getItem(int i) {

            if(!fragmentMap.containsKey(i)){
                MyLaunchpadSectionFragment fragment = new MyLaunchpadSectionFragment();
                Bundle args = new Bundle();
                args.putString(MyLaunchpadSectionFragment.ARG_DISASTER, StringUtils.disasterTypes[i]);
                args.putInt(MyLaunchpadSectionFragment.ARG_DISASTER_NUMBER, i + 1);
                args.putInt(MyLaunchpadSectionFragment.ARG_TOTAL_DISASTERS, getCount());
                args.putInt(MyLaunchpadSectionFragment.ARG_DROUGHT_PERCENT, expAdapters.get(0).getPercentRisk());
                fragment.setArguments(args);
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
        public static final String ARG_DISASTER = "disaster_type";
        public static final String ARG_DISASTER_NUMBER = "disaster_number";
        public static final String ARG_TOTAL_DISASTERS = "total";
        public static final String ARG_DROUGHT_PERCENT = "drought_percent";
        public ImageView lArrow, rArrow;

        ExpandableListAdapter adapt1, adapt2;

        public void setAdapter1(ExpandableListAdapter a){
            adapt1 = a;
        }

//        public void setAdapter2(ExpandableListAdapter a){
//            adapt2 = a;
//        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_display_disasters, container, false);
            rootView.setVisibility(View.VISIBLE);
            System.out.println("Creating view of fragment");
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.disasterTitle)).setText(
                    args.getString(ARG_DISASTER));

            ((TextView) rootView.findViewById(R.id.disasterPageNumber)).setText(
                    args.getInt(ARG_DISASTER_NUMBER) + " of " + args.getInt(ARG_TOTAL_DISASTERS));

            rootView.findViewById(R.id.disasterBackground).setBackgroundResource(R.drawable.drought_background);

            final AnimatedExpandableListView impactView = (AnimatedExpandableListView) rootView.findViewById(R.id.impactList);
//            final AnimatedExpandableListView factView = (AnimatedExpandableListView) rootView.findViewById(R.id.factList);
            if(impactView == null){
                Log.d("impactView", "NULL from mylaunchpadSectionFragment");
            }
//            if(factView == null){
//                Log.d("factView", "NULL from mylaunchpadSectionFragment");
//            }
            setOnClickListener(impactView, adapt1);// = (AnimatedExpandableListView) rootView.findViewById(R.id.explist2);
//            setOnClickListener(factView, adapt2);

            ImageView leftArrow = (ImageView) rootView.findViewById(R.id.riskscreen_left);
            ImageView rightArrow = (ImageView) rootView.findViewById(R.id.riskscreen_right);
            if(args.getInt(ARG_DISASTER_NUMBER) == 1 ){
                leftArrow.setVisibility(View.GONE);
            }
            if(args.getInt(ARG_DISASTER_NUMBER) == args.getInt(ARG_TOTAL_DISASTERS)){
                rightArrow.setVisibility(View.GONE);
            }

            lArrow = leftArrow;
            rArrow = rightArrow;

            CustomGauge gauge = (CustomGauge) rootView.findViewById(R.id.gaugeForRisk);
            gauge.setValue(args.getInt(ARG_DROUGHT_PERCENT));

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

                            setFadeOut(downArrow);

                            expListView.expandGroupWithAnimation(groupPosition);
                            lastView = groupPosition;
                            lastDownArrow = downArrow;
                        }
                        return true;
                    }

                    private void setFadeOut(final ImageView img){
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(img, "alpha", img.getAlpha(), 0.1f);
                        fadeOut.setDuration(200);

                        final AnimatorSet mAnimationSet = new AnimatorSet();
                        mAnimationSet.play(fadeOut);
                        mAnimationSet.start();

                        mAnimationSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                img.setVisibility(View.INVISIBLE);
                            }
                        });

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        super.onCreateOptionsMenu(menu);
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
        Intent intent = new Intent(this, DisplayChecklistActivityWithFragment.class);
        startActivity(intent);

    }

    private class DisplayInfo extends AsyncTask<String, String, String>{
        private static final String IMPACTS_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/grabImpacts.php";
        JSONObject jsonObject;

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

                    SharedPreferences.Editor editor = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE).edit();
                    editor.putString(alreadyUpdated, "already Updated");

                    List<ExpandableListAdapter> impactAdapters = new ArrayList<ExpandableListAdapter>();
//                    List<ExpandableListAdapter> factAdapters = new ArrayList<ExpandableListAdapter>();
                    for(int disasterIndex = 0; disasterIndex < 2; disasterIndex++){
                        JSONArray products = jsonObject.getJSONArray(StringUtils.disasterTypes[disasterIndex]);
                        // groups will be impact, facts, restrictions (these are the headers)
                        // in each group, what we grab from json is what we will display
                        System.out.println("Length of products: " + products.length());
                        for(int i = 0; i < products.length(); i++){
                            JSONObject object = products.getJSONObject(i);

                            String id = object.getString(TAG_ID);
                            String impactTitle = object.getString(TAG_TITLE);
                            String source = object.getString(TAG_SOURCE);
                            String impact = object.getString(TAG_IMPACT);
                            String fact = object.getString(TAG_FACT);
                            String link = object.getString(TAG_LINK);



                            if(!StringUtils.stringIsEmpty(impact)){
                                System.out.println("Impact: " + impact);
                                groupItems = new ArrayList<GroupItem>();
//                            listDataHeader.add(im);
//                            impacts.add(impact);
//                            listDataChild.put(im, impacts);
                                GroupItem groupItem = new GroupItem(impactTitle);
                                groupItem.setHeadLabel("Impact");
                                groupItem.setSubheader("Source: " + source);
                                List<ChildItem> items = new ArrayList<ChildItem>();
                                items.add(new ChildItem(impact));

                                groupItem.setChildren(items);
                                groupItems.add(groupItem);

                                editor.putString(TAG_IMPACT, impact);

                            }

                            if(!StringUtils.stringIsEmpty(fact)){
                                System.out.println("Fact: " + fact);
//                                groupItems = new ArrayList<GroupItem>();
//                            listDataHeader.add(TAG_FACT);
//                            facts.add(fact);
//                            listDataChild.put(TAG_FACT, facts);
                                GroupItem groupItem = new GroupItem(fact);
                                groupItem.setHeadLabel("Fact");
                                groupItem.setSubheader("Source: " + source);
                                List<ChildItem> items = new ArrayList<ChildItem>();
                                items.add(new ChildItem(link, true));
                                groupItem.setChildren(items);
                                groupItems.add(groupItem);

                                editor.putString(TAG_FACT, fact);
//                                factAdapters.add(setAdapter(groupItems));
                            }
                        }
                        System.out.println("Checking Location to put in Disasters Page " + disasterIndex);
//                            if(sharedPreferences.contains(AlarmReceiver.TAG_ZIP)
//                                    && sharedPreferences.contains(AlarmReceiver.TAG_NEW_DROUGHT_CONDITION)
//                                    && sharedPreferences.contains(AlarmReceiver.TAG_NEW_DROUGHT_PERCENT)){
                        Log.d("SYSTEM_PREFERENCES", " does contain zip, condition, and percent");
                        String zip = sharedPreferences.getString(AlarmReceiver.TAG_ZIP, "00000");
                        String city = sharedPreferences.getString(AlarmReceiver.TAG_CITY, "Cupertino");
                        String state = sharedPreferences.getString(AlarmReceiver.TAG_STATE, "California");
                        int drought_condition = sharedPreferences.getInt(AlarmReceiver.TAG_NEW_DROUGHT_CONDITION, 0);
                        float drought_percent = sharedPreferences.getFloat(AlarmReceiver.TAG_NEW_DROUGHT_PERCENT, 0);

                        GroupItem groupItem = new GroupItem(city + ", " + state + " " + zip );
                        groupItem.setHeadLabel("My Risk Status");
                        groupItem.setSubheader("");
                        List<ChildItem> items = new ArrayList<ChildItem>();
                        items.add(new ChildItem(AlarmReceiver.droughtLevelTitles[drought_condition]));
                        items.add(new ChildItem(drought_percent + "% of my area in drought"));
                        groupItem.setChildren(items);
                        groupItems.add(groupItem);



//                            }
                        impactAdapters.add(setAdapter(groupItems, (int)drought_percent));
                    }
                    editor.commit();

                    mAppSectionsPagerAdapter = new MyAppSectionsPagerAdapter(getSupportFragmentManager(), impactAdapters);
                    mViewPager = (ViewPager) findViewById(R.id.pager_disasters);
                    mViewPager.setAdapter(mAppSectionsPagerAdapter);

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
