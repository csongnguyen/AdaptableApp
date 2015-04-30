package com.adaptableandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by Connie on 4/17/2015.
 */
public class DisplayDisastersActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        setContentView(R.layout.activity_display_disasters);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void goToChecklist(View view){
        Intent intent = new Intent(this, DisplayChecklistActivity.class);
        startActivity(intent);

    }
}
