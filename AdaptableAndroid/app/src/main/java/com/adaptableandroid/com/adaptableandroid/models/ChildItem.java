package com.adaptableandroid.com.adaptableandroid.models;

/**
 * Created by Connie on 4/29/2015.
 */
public class ChildItem {
    String topic;
    boolean hasLink;

    public ChildItem(String topic){
        this.topic = topic;
        hasLink = false;
    }

    public ChildItem(String topic, boolean cond){
        this.topic = topic;
        hasLink = cond;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    public String getTopic(){
        return topic;
    }
    public boolean hasLink(){
        return hasLink;
    }
}
