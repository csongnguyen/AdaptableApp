package com.adaptableandroid.com.adaptableandroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connie on 4/29/2015.
 */
public class GroupItem {
    String title;
    List<ChildItem> children = new ArrayList<ChildItem>();

    public GroupItem(String t){
        this.title = t;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String t){
        this.title = t;
    }

    public List<ChildItem> getChildren(){
        return children;
    }

    public void setChildren(List<ChildItem> children){
        this.children = children;
    }


}
