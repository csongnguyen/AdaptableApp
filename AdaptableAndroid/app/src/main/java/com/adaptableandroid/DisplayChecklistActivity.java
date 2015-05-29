package com.adaptableandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptableandroid.com.adaptableandroid.models.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Connie on 4/6/2015.
 */
public class DisplayChecklistActivity extends ActionBarActivity {
    MyAdapter adapt;
    List<Task> mylist;
    PopupWindow pw;
    ProgressBar pbar;
    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    CheckBox checkBoxToBeUpdated;

    JSONArray products;


    private static final String TAG_ID = "id";
    private static final String TAG_STATUS = "is_completed";
    private static final String TAG_SHORT = "shortWarning";
    private static final String TAG_LONG = "longWarning";
    private static final String UPDATED_CHECKLIST = "updatedChecklist";
    private static final String DISASTER_TYPE = "disaster_type";
    private static final String DROUGHT = "drought";
    private static final String UPDATED_CHECKLIST_DROUGHT_SHORT = UPDATED_CHECKLIST + DROUGHT + TAG_SHORT;
    private static final String UPDATED_CHECKLIST_DROUGHT_LONG = UPDATED_CHECKLIST + DROUGHT + TAG_LONG;
    private static final String UPDATED_CHECKLIST_DROUGHT_STATUS = UPDATED_CHECKLIST + DROUGHT + TAG_STATUS;
    private static final String UPDATED_CHECKLIST_DROUGHT_ID = UPDATED_CHECKLIST + DROUGHT + TAG_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_display_checklist);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE);

        String update_checklist_type = UPDATED_CHECKLIST + DROUGHT;
        new DisplayChecklist().execute();
//        if(!sharedPreferences.contains(update_checklist_type)){
//            System.out.println("Checklist has not been set up yet. Setting up");
//            new DisplayChecklist().execute();
//        }
//        else{
//            System.out.println("Checklist has already been updated from database. Taking values from sharedPreferences.");
//            addSetToMyList(sharedPreferences.getStringSet(UPDATED_CHECKLIST_DROUGHT_SHORT,  new TreeSet<String>()),
//                    sharedPreferences.getStringSet(UPDATED_CHECKLIST_DROUGHT_LONG,  new TreeSet<String>()),
//                    sharedPreferences.getStringSet(UPDATED_CHECKLIST_DROUGHT_STATUS, new TreeSet<String>()),
//                    sharedPreferences.getStringSet(UPDATED_CHECKLIST_DROUGHT_ID, new TreeSet<String>()));
//        }
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

    private void addToMyList(String shortName, String longName, String status, String id){
        mylist.add(new Task(shortName, longName, Integer.parseInt(status), Integer.parseInt(id)));
    }

    private void addSetToMyList(Set<String> shortSet, Set<String> longSet, Set<String> statusSet, Set<String> idSet){
        // get the list of tasks
        mylist = new ArrayList<Task>();
        Iterator<String> shortIterator = shortSet.iterator();
        Iterator<String> longIterator = longSet.iterator();
        Iterator<String> statusIterator = statusSet.iterator();
        Iterator<String> idIterator = idSet.iterator();

        while(shortIterator.hasNext() && longIterator.hasNext()){
            String shortTask = shortIterator.next();
            String longTask = longIterator.next();
            String status = statusIterator.next();
            String id = idIterator.next();
//            System.out.println("Adding string " + shortTask);
            addToMyList(shortTask, longTask, status, id);
        }

        setAdapter();
    }

    private void setAdapter(){
        adapt = new MyAdapter(this, R.layout.list_inner_view, mylist);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
        listTask.setDivider(null);
        pbar = (ProgressBar) findViewById(R.id.progressBarChecklist);
        pbar.setProgress(getNumberOfCompletedTasks());
        pbar.setMax(mylist.size());
        pbar.setBackgroundResource(R.drawable.drought_background);

        setProgressViews();
    }

    private void setProgressViews(){
        double percentFinished = 100*((double)pbar.getProgress()/pbar.getMax());
        TextView pView1 = (TextView) findViewById(R.id.checklistProgressView1);
        pView1.setText(Math.floor(percentFinished) + "%");

        TextView pView2 = (TextView) findViewById(R.id.checklistProgressView2);
        pView2.setText(pbar.getProgress() + "/" + pbar.getMax());
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.);
        return true;
    }*/

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

    private class MyAdapter extends ArrayAdapter<Task>{
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
            ImageView info;
            LayoutInflater inflater;

            if (convertView == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_inner_view, parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.checkBox1);
                info = (ImageView) convertView.findViewById(R.id.infoButton);
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

                        int maxList = pbar.getMax();
                        int numberCompleted = getNumberOfCompletedTasks();
                        pbar.setProgress(numberCompleted);
                        setProgressViews();

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
                        ImageView bu = (ImageView) v;
                        Task task = (Task) bu.getTag();
                        initiatePopupWindow(task.getLongName());
                    }
                });
            } else{
                chk = (CheckBox) convertView.getTag(R.id.checkBox1);
                info = (ImageView) convertView.getTag(R.id.infoButton);
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
//                String status = Integer.toString(taskToBeUpdated.getStatus());
//
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
//                    if(checkBoxToBeUpdated.isChecked()){
//                        showCompletionAnimation();
//                        checkBoxToBeUpdated.setTextColor(0xFF00FF00);
//                    }
//                    else{checkBoxToBeUpdated.setTextColor(0xFFFF0000);}
//
//                    int maxList = pbar.getMax();
//                    int numberCompleted = getNumberOfCompletedTasks();
//                    pbar.setProgress(numberCompleted);
//
//                    if(numberCompleted == maxList){
//                        Toast.makeText(getApplicationContext(), "Congratulations, "
//                                + "you're done!", Toast.LENGTH_LONG).show();
//                    }
//                    System.out.println("jsonResponse to Update Checklist: " +  jsonResult.getInt(TAG_SUCCESS));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class DisplayChecklist extends AsyncTask<String, String, String> {
        private static final String POPULATE_CHECKLIST_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/getChecklist.php";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(DisplayChecklistActivity.this);
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
                jsonObject = jsonParser.makeHttpGetRequest(POPULATE_CHECKLIST_URL, DISASTER_TYPE, DROUGHT);
                Log.d("Checking result:", jsonObject.toString());

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
                if(!jsonObject.toString().isEmpty()){
                    products = jsonObject.getJSONArray(StringUtils.TAG_PRODUCTS);
//                    Set<String> shortWarnings = new TreeSet<String>();
//                    Set<String> longWarnings = new TreeSet<String>();
//                    Set<String> statuses = new TreeSet<String>();
//                    Set<String> ids = new TreeSet<String>();

                    // get the list of tasks
                    mylist = new ArrayList<Task>();
//                    SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MYPREFERENCES, Context.MODE_PRIVATE).edit();

                    for(int i = 0; i < products.length(); i++){
                        JSONObject object = products.getJSONObject(i);
                        String shortWarning = object.getString(TAG_SHORT);
                        String longWarning = object.getString(TAG_LONG);
                        String status = object.getString(TAG_STATUS);
                        String id = object.getString(TAG_ID);

                        String update_checklist_type = UPDATED_CHECKLIST + object.getString(DISASTER_TYPE);
//                        editor.putString(update_checklist_type, update_checklist_type);

                        if(!StringUtils.stringIsEmpty(shortWarning) && !StringUtils.stringIsEmpty(longWarning)
                                && !StringUtils.stringIsEmpty(status) && !StringUtils.stringIsEmpty(id)){
                            addToMyList(shortWarning, longWarning, status, id);
//                            shortWarnings.add(shortWarning);
//                            longWarnings.add(longWarning);
//                            statuses.add(status);
//                            ids.add(id);
                        }


                    }
//                    editor.putStringSet(UPDATED_CHECKLIST_DROUGHT_SHORT , shortWarnings);
//                    editor.putStringSet(UPDATED_CHECKLIST_DROUGHT_LONG, longWarnings);
//                    editor.putStringSet(UPDATED_CHECKLIST_DROUGHT_STATUS, statuses);
//                    editor.putStringSet(UPDATED_CHECKLIST_DROUGHT_ID, ids);
//                    editor.commit();
                    setAdapter();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
