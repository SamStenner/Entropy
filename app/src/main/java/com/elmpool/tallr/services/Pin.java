package com.elmpool.tallr.services;

import android.content.Context;

import com.elmpool.tallr.R;

public class Pin extends Item {

    public static final int ANNOUNCEMENT = 0;
    public static final int WARNING = 1;

    private String message;
    private int type;

    public Pin() {

    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getType() {
        return PIN;
    }

    public int getPinType(){
        return type;
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
                return context.getColor(R.color.colorAnnouncement);
            case WARNING:
                return context.getColor(R.color.colorWarning);
            default:
                return 0;

        }
    }

}
