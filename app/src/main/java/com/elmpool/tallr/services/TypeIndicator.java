package com.elmpool.tallr.services;

import android.util.Log;

public class TypeIndicator extends Item {

    private String userID;

    public TypeIndicator() {

    }

    @Override
    public int getType() {
        return TYPING;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isTyping(){
        return System.currentTimeMillis() - getTimestamp() <= 10000;
    }

}
