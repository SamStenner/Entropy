package com.elmpool.tallr.models;

import android.content.Context;

import com.elmpool.tallr.R;
import com.elmpool.tallr.utils.Manager;

public class Pin extends Item {

    public static final int ANNOUNCEMENT = 0;
    public static final int WARNING = 1;

    private String message;
    private int pinType;

    public Pin(String id, Long time, String message, int type) {
        super(id, time);
        this.message = message;
        this.pinType = type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return PIN;
    }

    public int getPinType(){
        return pinType;
    }

    public String getTitle(Context context){
        switch (getPinType()) {
            case ANNOUNCEMENT:
                return context.getString(R.string.lbl_pin_announcement);
            case WARNING:
                return context.getString(R.string.lbl_pin_warning);
            default:
                return null;

        }
    }

    public int getColor(Context context){
        switch (getPinType()) {
            case ANNOUNCEMENT:
                return Manager.getColor(context, R.color.colorAnnouncement);
            case WARNING:
                return Manager.getColor(context, R.color.colorWarning);
            default:
                return 0;

        }
    }

}
