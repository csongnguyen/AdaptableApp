package com.adaptableandroid.com.adaptableandroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connie on 4/29/2015.
 */
public class GroupItem {
    String headLabel;
    String title;
    String subheader;
    List<ChildItem> children = new ArrayList<ChildItem>();

    public GroupItem(String t){
        this.title = t;
    }

    public String getHeadLabel(){ return headLabel;}

    public String getTitle(){
        return title;
    }

    public String getSubheader(){return subheader;}

    public void setHeadLabel(String h){ this.headLabel = h;}

    public void setTitle(String t){
        this.title = t;
    }

    public void setSubheader(String s){ this.subheader = s;}

    public List<ChildItem> getChildren(){
        return children;
    }

    public void setChildren(List<ChildItem> children){
        this.children = children;
    }


}
