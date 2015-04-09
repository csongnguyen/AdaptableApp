package com.adaptableandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connie on 4/6/2015.
 */
public class DisplayChecklistActivity extends Activity {
    MyAdapter adapt;
    List<Task> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_checklist);

        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        // Set the text view as the activity layout
        //setContentView(textView);

        // get the list of tasks
        mylist = new ArrayList<Task>();
        Task first = new Task("Eat pizza", 0);
        Task second = new Task("Call maintenance", 0);
        Task third = new Task("Send mail", 0);
        mylist.add(first);
        mylist.add(second);
        mylist.add(third);


        adapt = new MyAdapter(this, R.layout.list_inner_view, mylist);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.);
        return true;
    }*/

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
            CheckBox chk = null;

            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_inner_view, parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.checkBox1);

                chk.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        CheckBox cb = (CheckBox) v;
                        Task changeTask = (Task) cb.getTag();
                        changeTask.setStatus(cb.isChecked() == true ? 1:0);
                        if(cb.isChecked()){cb.setTextColor(0xFF00FF00);}
                        else{cb.setTextColor(0xFFFF0000);}
                        Toast.makeText(getApplicationContext(), "Clicked on that one checkbox: " + cb.getText() + " is "
                                        + cb.isChecked(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                chk = (CheckBox) convertView.getTag();
            }

            Task current = taskList.get(position);
            if(chk != null){
                chk.setText(current.getName());
                chk.setChecked(current.getStatus()==1?true:false);
                chk.setTag(current);
                Log.d("listener", String.valueOf(current.getId()));
            }

            return convertView;
        }
    }
}
