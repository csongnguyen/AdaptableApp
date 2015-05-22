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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptableandroid.com.adaptableandroid.models.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Connie on 5/18/2015.
 */
public class DisplayChecklistActivityWithFragment  extends ActionBarActivity {
    List<Task> mylist;
    PopupWindow pw;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    MyAppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    CheckBox checkBoxToBeUpdated;

    private static final String TAG_STATUS = "is_completed";
    private static final String TAG_SHORT = "shortWarning";
    private static final String TAG_LONG = "longWarning";
    private static final String UPDATED_CHECKLIST = "updatedChecklist";
    private static final String DISASTER_TYPE = "disaster_type";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_checklists);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF4697b5));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView tView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbarTitle);
        tView.setText("Checklist");
        new DisplayChecklist().execute();

    }



    private void addToMyList(String shortName, String longName, String status){//}, String id){
        mylist.add(new Task(shortName, longName, Integer.parseInt(status)));//, Integer.parseInt(id)));
    }

    private MyAdapter setAdapter(List<Task> list){
//        adapt = new MyAdapter(this, R.layout.list_inner_view, mylist);
        return new MyAdapter(this, R.layout.list_inner_view, list);
//        ListView listTask = (ListView) findViewById(R.id.listView1);
//        listTask.setAdapter(adapt);
//        listTask.setDivider(null);
//        pbar = (ProgressBar) findViewById(R.id.progressBarChecklist);
//        pbar.setProgress(getNumberOfCompletedTasks());
//        pbar.setMax(mylist.size());
//        pbar.setBackgroundResource(R.drawable.drought_background);
//
//        setProgressViews();
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


    private void initiatePopupWindow(final String taskName){
        try{
            // We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            final View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_element));

            // create a 500px width and 470px height PopupWindow
            pw = new PopupWindow(layout, 500, 450, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0,0);

            TextView mResultText = (TextView) layout.findViewById(R.id.popup_text);
            mResultText.setText(taskName);
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
    }

    private void showCompletionAnimation(){
        //Custom animation on image
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.completion_screen_image,(ViewGroup) findViewById(R.id.completion_animation));

        // create a 300px width and 470px height PopupWindow
        pw = new PopupWindow(layout, 100, 100, true);
        pw.showAtLocation(layout, Gravity.CENTER, 0,0);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, .3f);
        fadeOut.setDuration(500);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", .3f, 1f);
        fadeIn.setDuration(200);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeOut).after(fadeIn);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pw.dismiss();
            }
        });
        mAnimationSet.start();
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    private class MyAppSectionsPagerAdapter extends FragmentPagerAdapter {
        Map<Integer, MyLaunchpadSectionFragment> fragmentMap;
        List<MyAdapter> listAdapters;

        public MyAppSectionsPagerAdapter(FragmentManager fm, List<MyAdapter> lists) {
            super(fm);
            fragmentMap = new HashMap<Integer, MyLaunchpadSectionFragment>();
            listAdapters = lists;
        }

        @Override
        public MyLaunchpadSectionFragment getItem(int i) {

            if(!fragmentMap.containsKey(i)){
                MyLaunchpadSectionFragment fragment = new MyLaunchpadSectionFragment();
                Bundle args = new Bundle();
                args.putString(MyLaunchpadSectionFragment.ARG_DISASTER, StringUtils.disasterTypes[i]);
                args.putInt(MyLaunchpadSectionFragment.ARG_DISASTER_NUMBER, i + 1);
                args.putInt(MyLaunchpadSectionFragment.ARG_TOTAL_DISASTERS, getCount());
                fragment.setArguments(args);
                fragment.setAdapter1(listAdapters.get(i));

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
        public ImageView lArrow, rArrow;

        MyAdapter adapt1;
        List<Task> mylist;
        ProgressBar pbar;
        View myRootView;

        public void setAdapter1(MyAdapter a){

            adapt1 = a;
            mylist = a.taskList;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_display_checklist, container, false);
            myRootView = rootView;
            System.out.println("Creating view of fragment");
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.checklistProgressViewTitle)).setText(
                    args.getString(ARG_DISASTER) + " Preparation");

            rootView.findViewById(R.id.progressBarChecklist).setBackgroundResource(R.drawable.drought_background);

            ListView listView = (ListView) rootView.findViewById(R.id.listView1);

//            final AnimatedExpandableListView impactView = (AnimatedExpandableListView) rootView.findViewById(R.id.impactList);
//            final AnimatedExpandableListView factView = (AnimatedExpandableListView) rootView.findViewById(R.id.factList);
            if(listView == null){
                Log.d("listView", "NULL from mylaunchpadSectionFragment");
            }else{
                listView.setAdapter(adapt1);
                listView.setDivider(null);
            }

//            setOnClickListener(impactView, adapt1);// = (AnimatedExpandableListView) rootView.findViewById(R.id.explist2);
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

            pbar = (ProgressBar) rootView.findViewById(R.id.progressBarChecklist);
            pbar.setProgress(getNumberOfCompletedTasks());
            pbar.setMax(mylist.size());
            pbar.setBackgroundResource(R.drawable.drought_background);

            setProgressViews(rootView);

            return rootView;
        }

        public int getPBarMax(){
            return pbar.getMax();
        }

        private int getNumberOfCompletedTasks(){
            int total = 0;
            for(int i = 0;i < mylist.size();i++){
                if(mylist.get(i).getStatus() == 1){
                    total++;
                }
            }
            return total;
        }

        public void setPBarProgress(){
            pbar.setProgress(getNumberOfCompletedTasks());
            setProgressViews();
        }

        private void setProgressViews(View rootView){
            int percentFinished = (int)(100*((double)pbar.getProgress()/pbar.getMax()));
            TextView pView1 = (TextView) rootView.findViewById(R.id.checklistProgressView1);
            pView1.setText(Math.floor(percentFinished) + "%");

            TextView pView2 = (TextView) rootView.findViewById(R.id.checklistProgressView2);
            pView2.setText(pbar.getProgress() + "/" + pbar.getMax());
        }

        private void setProgressViews(){
            int percentFinished = (int)(100*((double)pbar.getProgress()/pbar.getMax()));
            TextView pView1 = (TextView) myRootView.findViewById(R.id.checklistProgressView1);
            pView1.setText(Math.floor(percentFinished) + "%");

            TextView pView2 = (TextView) myRootView.findViewById(R.id.checklistProgressView2);
            pView2.setText(pbar.getProgress() + "/" + pbar.getMax());
        }


    }


    private class MyAdapter extends ArrayAdapter<Task> {
        Context context;
        List<Task> taskList = new ArrayList<Task>();
        int layoutResourceId;

        public MyAdapter(Context context, int layoutResourceId, List<Task> objects){
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.taskList = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            CheckBox chk;
            Button info;
            LayoutInflater inflater;

            if (convertView == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_inner_view, parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.checkBox1);
                info = (Button) convertView.findViewById(R.id.infoButton);
                convertView.setTag(R.id.checkBox1,chk);
                convertView.setTag(R.id.infoButton, info);

                chk.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        CheckBox cb = (CheckBox) v;
                        Task changeTask = (Task) cb.getTag();

//                        System.out.println("Status of checkbox before change: " + ((Task) cb.getTag()).getStatus());
                        //change status on View, but also in database model
                        changeTask.setStatus(cb.isChecked() == true ? 1:0);
                        checkBoxToBeUpdated = cb;
                        new UpdateChecklist().execute();

                        if(checkBoxToBeUpdated.isChecked()){
                            showCompletionAnimation();
//                            checkBoxToBeUpdated.setTextColor(0xFF00FF00);
                        }
//                        else{checkBoxToBeUpdated.setTextColor(0xFFFF0000);}


                        int maxList = mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).getPBarMax();
                        Log.d("MVIEWPAGER ON CHK CLICK", "" + mViewPager.getCurrentItem());
                        int numberCompleted = mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).getNumberOfCompletedTasks();
                        mAppSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).setPBarProgress();

                        if(numberCompleted == maxList){
                            Toast.makeText(getApplicationContext(), "Congratulations, "
                                    + "you're done!", Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(getApplicationContext(), "Clicked on that one checkbox: " + cb.getText() + " is "
//                                + cb.isChecked(), Toast.LENGTH_LONG).show();
                    }
                });

                info.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Button bu = (Button) v;
                        Task task = (Task) bu.getTag();
                        initiatePopupWindow(task.getLongName());
                    }
                });
            } else{
                chk = (CheckBox) convertView.getTag(R.id.checkBox1);
                info = (Button) convertView.getTag(R.id.infoButton);
            }

            Task current = taskList.get(position);

            if(chk != null && current != null && info != null){
//                System.out.println("current task = " + current.getShortName());
                chk.setText(current.getShortName());
                chk.setChecked(current.getStatus()==1?true:false);
//                if(chk.isChecked()){chk.setTextColor(0xFF00FF00);}
//                else{chk.setTextColor(0xFFFF0000);}
                chk.setTag(current);
                info.setTag(current);
//                Log.d("listener", String.valueOf(current.getId()));
            }

            return convertView;
        }
    }

    private class UpdateChecklist extends AsyncTask<String, String, String> {
        private static final String UPDATE_CHECKLIST_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/updateChecklist.php";
        JSONObject jsonResult;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls){
            // Check for success tag
            int success;
            try{
                Log.d("Checklist Update", "Starting");
                System.out.println("Updating checklist right now....");
                Task taskToBeUpdated = (Task) checkBoxToBeUpdated.getTag();
//                String id = Integer.toString(taskToBeUpdated.getId());
//                String shortName = URLEncoder.encode(taskToBeUpdated.getShortName(), "UTF-8");
                String shortName = taskToBeUpdated.getShortName();
                String status = Integer.toString(taskToBeUpdated.getStatus());

                jsonResult = jsonParser.makeHttpPostRequest(UPDATE_CHECKLIST_URL, TAG_SHORT, shortName, TAG_STATUS,status);
//                jsonResult = jsonParser.makeHttpPostRequest(UPDATE_CHECKLIST_URL, TAG_ID, id, TAG_STATUS,status);
                Log.d("Checking result:", jsonResult.toString());

            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String someString){
            try {
                if(!jsonResult.toString().isEmpty()){

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class DisplayChecklist extends AsyncTask<String, String, String> {
        private static final String POPULATE_CHECKLIST_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/getChecklist.php";
        JSONObject[] jsonObjects = new JSONObject[StringUtils.disasterTypes.length];

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayChecklistActivityWithFragment.this);
            pDialog.setMessage("Setting up checklist...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls){
            // Check for success tag
            int success;
            try{
                Log.d("Checklist Request", "Starting");
                System.out.println("Checking checklist request right now....");

                for(int i = 0; i < jsonObjects.length; i++){
                    jsonObjects[i] = jsonParser.makeHttpGetRequest(POPULATE_CHECKLIST_URL, DISASTER_TYPE, StringUtils.disasterTypes[i]);
                    Log.d("Checking result:", jsonObjects[i].toString());
                }
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
                    }}, 300);  // 1000 milliseconds
                List<MyAdapter> myAdapters = new ArrayList<MyAdapter>();
                for(int a = 0; a < jsonObjects.length; a++){
                    JSONObject jsonObject = jsonObjects[a];
                    if(!jsonObject.toString().isEmpty()){
                        JSONArray products = jsonObject.getJSONArray(StringUtils.TAG_PRODUCTS);
                        mylist = new ArrayList<Task>();

                        for(int i = 0; i < products.length(); i++){
                            JSONObject object = products.getJSONObject(i);
                            String shortWarning = object.getString(TAG_SHORT);
                            String longWarning = object.getString(TAG_LONG);
                            String status = object.getString(TAG_STATUS);

                            if(!StringUtils.stringIsEmpty(shortWarning) && !StringUtils.stringIsEmpty(longWarning)
                                    && !StringUtils.stringIsEmpty(status)){// && !StringUtils.stringIsEmpty(id)){
                                addToMyList(shortWarning, longWarning, status);
                            }
                        }
                        addToMyList("This is a test warning to see the wrap around effect of a long to-do list item, just to see what will really happen.", "Blank Testing To-do", "0");
                        myAdapters.add(setAdapter(mylist));

                    }

                }
                mAppSectionsPagerAdapter = new MyAppSectionsPagerAdapter(getSupportFragmentManager(), myAdapters);

                mViewPager = (ViewPager) findViewById(R.id.pager_checklists);
                mViewPager.setAdapter(mAppSectionsPagerAdapter);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
