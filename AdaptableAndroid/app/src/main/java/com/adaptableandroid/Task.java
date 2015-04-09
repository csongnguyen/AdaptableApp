package com.adaptableandroid;

/**
 * Created by Connie on 4/8/2015.
 */
public class Task {
    private String name;
    private int status;
    private int id;

    public Task(){
        this.name = null;
        this.status = 0;
    }

    public Task(String name, int status){
        super();
        this.name = name;
        this.status = status;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }
}
