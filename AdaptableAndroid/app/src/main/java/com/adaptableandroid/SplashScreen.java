package com.adaptableandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*From testJDBC class*/
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import android.os.AsyncTask;
import android.widget.TextView;


/**
 * Created by Connie on 4/11/2015.
 */
@SuppressLint("NewApi")
public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "ifG235Bk5GNYPM6fLX6tBPkrD";
    private static final String TWITTER_SECRET = "6nbcbRTUKeFZEUhPh7TmKvA1A0Pw7RHpIe3VDtsTiPkfj52ayr";

    // Splash screen timer
    public static int SPLASH_TIME_OUT = 1500;
    public DatabaseHelper myDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_splash);
        myDBHelper =  new DatabaseHelper(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        new Handler().postDelayed(new Runnable() {
            /*
             *  Showing splash screen with a timer.
             *  This will be useful when you want to showcase
             *  your app logo
             */

            @Override
            public void run() {
                // This method will be executed once the timer
                // is over
                // Start your app's main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // this is for when we want to use a SQLiteDatabase
                try {
                    myDBHelper.createDatabase();
                } catch(IOException e){
                    throw new Error("Unable to create database");
                }

                try{
                    SharedPreferences sp = getSharedPreferences(StringUtils.MYPREFERENCES, Context.MODE_PRIVATE);
                    sp.edit().clear().commit();
//                    myDBHelper.openDatabase();
//                    myDBHelper.getDroughtInfo();
//                    LoadDatabase loadingData = new LoadDatabase();
//                    loadingData.execute("Executing load of database");
                } catch(Exception sqle){//catch(SQLException sqle){
                    throw new Error("Cannot open database");
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


//    private class LoadDatabase extends AsyncTask<String, Void, String> {
//        private static final String url = "jdbc:mysql://tiny.idav.ucdavis.edu:3306/competition";
//        private static final String user = "root";
//        private static final String pass = "parallax";
////        ?useUnicode=yes&characterEncoding=UTF-8
//
//        @Override
//        protected String doInBackground(String... urls){
//            testDB();
//            return null;
//        }
//
//        public void testDB() {
////        TextView tv = (TextView)this.findViewById(R.id.testdb_text);
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.getConnection(url, user, pass);
//            /* System.out.println("Databaseection success"); */
//
//            String result = "Database connection success\n";
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery("select State from drought_data");
//            ResultSetMetaData rsmd = rs.getMetaData();
//
//            while(rs.next()) {
//                result += rsmd.getColumnName(1) + ": " + rs.getString(1) + "\n";
////                result += rsmd.getColumnName(2) + ": " + rs.getString(2) + "\n";
////                result += rsmd.getColumnName(3) + ": " + rs.getString(3) + "\n";
//            }
////            tv.setText(result);
//            System.out.println("loading database results: " + result);
//        }
//        catch(Exception e) {
//            e.printStackTrace();
////            tv.setText(e.toString());
//            System.err.println("loading database error: " + e.toString());
//        }
//
//        }
//    }
}
