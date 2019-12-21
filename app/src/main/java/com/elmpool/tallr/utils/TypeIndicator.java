package com.elmpool.tallr.utils;

public class TypeIndicator {

    public static final int TYPING_THRESHOLD = 1500;
    public static final int VISUAL_THRESHOLD = (int) (TYPING_THRESHOLD * 1.5f);

    private String userID;
    private Long time;

    public TypeIndicator() {

    }

    public String getUserID() {
        return userID;
    }

    public Long getTime(){
        return time;
    }

    public boolean isTyping(){
        return System.currentTimeMillis() - getTime() <= TYPING_THRESHOLD;
    }

}
