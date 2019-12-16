package com.elmpool.tallr.services;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Item {

    public static final int PIN = 0;
    public static final int MSG = 1;
    public static final int MSG_EMOJI = 2;
    public static final int TYPING = 3;

    private Long time;

    public Long getTimestamp() {
        return time;
    }

    public String getTime(){
        Date date = new Date(getTimestamp());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy' AT 'HH:mm");
        return sdf.format(date);
    }

    public abstract int getType();

}
