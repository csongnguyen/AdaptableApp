package com.adaptableandroid.com.adaptableandroid.models;

/**
 * Created by Connie on 4/8/2015.
 */
public class Task {
    private String shortName;
    private String longName;
    private int status;
    private int id;

    public Task(){
        this.shortName = null;
        this.longName = null;
        this.status = 0;
    }

    public Task(String shortName, String longName, int status, int id){
        super();
        this.shortName = shortName;
        this.longName = longName;
        this.status = status;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getShortName(){
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }
}
