package com.adaptableandroid;

/**
 * Created by Connie on 5/1/2015.
 */
public class StringUtils {
    public static boolean stringIsEmpty(String s){
        if(s == null || s.isEmpty() || s.equals("null")){
            return true;
        }
        return false;
    }
}
