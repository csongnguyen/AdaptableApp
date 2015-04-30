package com.adaptableandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Connie on 4/21/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // The Android's default system path of your app database.
    private static String DB_PATH = "";
    private static String DB_NAME = "citris_database";
    private static String DROUGHT_TABLE = "drought_data";
    private SQLiteDatabase mDatabase;
    private final Context mContext;

    /**
        Constructor
        Takes and keeps a reference of the passed context
        in order to access the application assets and resources.
        @param context
     **/
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.mContext = context;

        DB_PATH = "/data/data/" + context.getApplicationContext().getPackageName() + "/databases/";
        System.out.println("db_path is = " + DB_PATH);
    }

    /**
     * Creates an empty database on the system and
     * rewrites it with your own database.
     */
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        SQLiteDatabase db_Read = null;
//        if(dbExist){
//            // do nothing, the database already exists
//            System.out.println("Database already exists, not creating new one.");
//        }
//        else{
            /* by calling this method, an empty database
               will be created in the default system path
               of your app, so we are going to overwrite
               that database with our database.
            */
//            db_Read = this.getReadableDatabase();
//            db_Read.close();
            try{
                copyDatabase();
            } catch(IOException e){
                throw new Error("Error copying database");
            }
        }
//    }

    /**
        Check if the database already exists to avoid recopying
         the file each time you open the application.
       @return true if it exists, returns false if it doesn't
     **/
    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;

            File f = new File(DB_PATH);
            if(!f.exists()){
                f.mkdir();
            }
            if(f.exists()){
                System.out.println("Just created directory for DB_PATH");
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            }


        } catch(SQLiteException e){
            // database doesn't exist yet
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true: false;
    }

    /**
     * Copies your database from your local assets-folder
     * to the system folder, from where it can be accessed
     * and handled. This is done by transferring bytestream.
     */
    private void copyDatabase() throws IOException {
        System.out.println("copying database...");
        // open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        System.out.println("Finished copying database!");
    }

    public void openDatabase() throws SQLException {
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close(){
        if(mDatabase != null){
            mDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    /* Add your public helper methods to access and get
        content from the database.
        You can return cursors by doing " return myDatabase.query(...)"
        so it'd be easy for you to create adapters for your views.
    */
    //----------------helper functions------------------------------

    public void getDroughtInfo(){
        System.out.println("Database path "  + mDatabase.getPath());
        String selectQuery = "SELECT * FROM " + DROUGHT_TABLE;
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do {
                System.out.println("County: " + cursor.getString(2));
                // -you can make the select query more specific by identifying the county
                //   that is closest to your location.
                // -make sure you have the census data to help you with this.
                // -create an object that holds all the information of YOUR particular county.
                //   UNLESS.... we include having a social network, which we will then also need
                //   to include their peers' locations as well.
                // -set up a controller for determining the county nearest your location.
                // -once you find the county, figure out if you are in D1, D2, D3, or D4.
                // -Based on that, determine if you are in need of an alert.
                //   if yes, then send out a notification on the phone indicating an urgency to
                //   look at the checklist.
            } while(cursor.moveToNext());
        }
    }
}
