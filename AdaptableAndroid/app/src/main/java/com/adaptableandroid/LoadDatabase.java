package com.adaptableandroid;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Created by Connie on 4/25/2015.
 */
public class LoadDatabase extends AsyncTask<String, Void, String> {
    private static final String url = "jdbc:mysql://tiny.idav.ucdavis.edu:3306/competition";
    private static final String user = "root";
    private static final String pass = "parallax";
//        ?useUnicode=yes&characterEncoding=UTF-8

    @Override
    protected String doInBackground(String... urls){
        testDB();
        return null;
    }

    public void getDroughtData(){

    }

    public void testDB() {
//        TextView tv = (TextView)this.findViewById(R.id.testdb_text);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            /* System.out.println("Databaseection success"); */

            String result = "Database connection success\n";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select State from drought_data");
            ResultSetMetaData rsmd = rs.getMetaData();

            while(rs.next()) {
                result += rsmd.getColumnName(1) + ": " + rs.getString(1) + "\n";
//                result += rsmd.getColumnName(2) + ": " + rs.getString(2) + "\n";
//                result += rsmd.getColumnName(3) + ": " + rs.getString(3) + "\n";
            }
//            tv.setText(result);
            System.out.println("loading database results: " + result);
        }
        catch(Exception e) {
            e.printStackTrace();
//            tv.setText(e.toString());
            System.err.println("loading database error: " + e.toString());
        }

    }
}