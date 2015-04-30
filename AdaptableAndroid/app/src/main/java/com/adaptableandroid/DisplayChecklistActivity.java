package com.adaptableandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Connie on 4/6/2015.
 */
public class DisplayChecklistActivity extends ActionBarActivity {
    MyAdapter adapt;
    List<Task> mylist;
    PopupWindow pw;
    int numberCompleted = 0;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_checklist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the list of tasks
        mylist = new ArrayList<Task>();
        Task first = new Task("Eat pizza", 0);
        Task second = new Task("Call maintenance", 0);
        Task third = new Task("Send mail", 0);
        mylist.add(first);
        mylist.add(second);
        mylist.add(third);

        for(Integer i = 0; i < 15; i++ ){
            String newTask = "Task " + i;
            Task n = new Task(newTask, 0);
            mylist.add(n);
            System.out.println("printed " + newTask);
        }


        adapt = new MyAdapter(this, R.layout.list_inner_view, mylist);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
        pbar = (ProgressBar) findViewById(R.id.progressBarChecklist);
        pbar.setMax(mylist.size());
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.);
        return true;
    }*/

    private void initiatePopupWindow(){
        try{
            // We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_element));

            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, 300, 470, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0,0);

            TextView mResultText = (TextView) layout.findViewById(R.id.server_status_text);
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

    private void showCompletionAnimation(){
        //Custom animation on image
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.completion_screen_image,(ViewGroup) findViewById(R.id.completion_animation));

        // create a 300px width and 470px height PopupWindow
        pw = new PopupWindow(layout, 100, 100, true);
        pw.showAtLocation(layout, Gravity.CENTER, 0,0);

        //ImageView myView =  (ImageView) layout.findViewById(R.id.completion_icon);

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
            Button info;
            LayoutInflater inflater;


            if (convertView == null){
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_inner_view, parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.checkBox1);

                info = (Button) convertView.findViewById(R.id.infoButton);
                convertView.setTag(chk);


                chk.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        CheckBox cb = (CheckBox) v;
                        Task changeTask = (Task) cb.getTag();
                        changeTask.setStatus(cb.isChecked() == true ? 1:0);
                        if(cb.isChecked()){cb.setTextColor(0xFF00FF00);}
                        else{cb.setTextColor(0xFFFF0000);}
//                        Toast.makeText(getApplicationContext(), "Clicked on that one checkbox: " + cb.getText() + " is "
//                                + cb.isChecked(), Toast.LENGTH_LONG).show();


                        int maxList = pbar.getMax();
                        if(numberCompleted < maxList && cb.isChecked()){
                            numberCompleted++;
                            pbar.setProgress(numberCompleted);
                            showCompletionAnimation();
                            if(numberCompleted == maxList){
                                Toast.makeText(getApplicationContext(), "Congratulations, "
                                        + "you're done!", Toast.LENGTH_LONG).show();
                            }
                        }else if(!cb.isChecked()){
                            numberCompleted--;
                            pbar.setProgress(numberCompleted);
                        }



                    }
                });

                info.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        initiatePopupWindow();
                    }
                });
            }
            else{
                chk = (CheckBox) convertView.getTag();
                //info = (Button) convertView.getTag();
                //inflater = LayoutInflater.from(context);
            }

            Task current = taskList.get(position);

            if(chk != null && current != null){


                System.out.println("current = " + current.getName());
                chk.setText(current.getName());
                chk.setChecked(current.getStatus()==1?true:false);
                if(chk.isChecked()){chk.setTextColor(0xFF00FF00);}
                else{chk.setTextColor(0xFFFF0000);}
                chk.setTag(current);
                Log.d("listener", String.valueOf(current.getId()));
            }

            return convertView;
        }
    }
}
