package com.example.oficialbombero.models;

import java.util.ArrayList;

public class Chat {
    private String id;
    private String writing;
    private long timestamp;
    private ArrayList<String> ids;
    private int numberMessages;
   // private int idNotification;

    public Chat() {
    }

    public Chat(String id, String writing, long timestamp, ArrayList<String> ids, int numberMessages) {
        this.id = id;
        this.writing = writing;
        this.timestamp = timestamp;
        this.ids = ids;
        this.numberMessages = numberMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMessages(int numberMessages) {
        this.numberMessages = numberMessages;
    }
}
