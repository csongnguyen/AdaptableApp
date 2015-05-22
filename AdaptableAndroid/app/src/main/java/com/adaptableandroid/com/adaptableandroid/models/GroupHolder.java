package com.adaptableandroid.com.adaptableandroid.models;

import android.widget.TextView;

import java.util.List;

/**
 * Created by Connie on 4/29/2015.
 */
public class GroupHolder {
    TextView headLabel;
    TextView title;

    public TextView getHeadLabel(){ return headLabel;}

    public TextView getTitle(){
        return title;
    }

    public void setHeadLabel(TextView h){ this.headLabel = h;}

    public void setTitle(TextView t){
        this.title = t;
    }


}
