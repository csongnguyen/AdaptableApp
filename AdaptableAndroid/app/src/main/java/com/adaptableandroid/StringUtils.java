package com.adaptableandroid;

/**
 * Created by Connie on 5/1/2015.
 */
public class StringUtils {
    public final static String MYPREFERENCES = "MyPrefs";
    public static final String TAG_SUCCESS = "success";

    public final static String DROUGHT_TABLE = "drought_data";
    public final static String TAG_PRODUCTS = "products";
    // These are from sqlite database
    public final static String PERSONAL_DROUGHT_TABLE = "last_drought_conditions";
    public static String TAG_ZIP = "_id";
    public static String TAG_DROUGHT_COND = "drought_level";
    public static String TAG_PERCENTAGE = "percentage";

    public static String[] disasterTypes = {"Drought", "Earthquake", "Wildfire"};//, "Hurricane", "Tornado"};

    public static enum DisasterType {
        DROUGHT(disasterTypes[0]),
        EARTHQUAKE(disasterTypes[1]),
        WILDFIRE(disasterTypes[2]);

        public final String value;

        private DisasterType(String v){
            value = v;
        }

    };

    public static boolean stringIsEmpty(String s){
        if(s == null || s.isEmpty() || s.equals("null")){
            return true;
        }
        return false;
    }


}
