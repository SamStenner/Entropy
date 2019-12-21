package com.elmpool.tallr.models;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Item {

    public static final int ITEM = 0;
    public static final int PIN = 1;
    public static final int MSG = 2;
    public static final int MSG_EMOJI = 3;

    private final int syncThreshold = 1 * 60;

    protected String id;
    protected Long time;


    public Item(String id, Long time) {
        this.id = id;
        this.time = time;
    }

    public String getID() {
        return id;
    }

    public Long getTimestamp() {
        return time;
    }

    public String getTime(){
        Date date = new Date(getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy' AT 'HH:mm");
        return sdf.format(date);
    }

    public int getType() {
        return ITEM;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return ((Message)obj).getID().equals(getID());
    }

    public boolean syncTime(Item other){
        return syncTime(other, syncThreshold);
    }

    public boolean syncTime(Item other, long threshold) {
        return Math.abs(this.getTimestamp() - other.getTimestamp()) <= threshold * 1000;
    }

}
