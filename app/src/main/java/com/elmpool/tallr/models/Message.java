package com.elmpool.tallr.models;

import android.view.ViewGroup;

public class Message extends Item {

    public static final int TYPE_CENTER = 0b1;
    public static final int TYPE_TOP = 0b010;
    public static final int TYPE_BOTTOM = 0b100;
    public static final int TYPE_SINGLE = TYPE_TOP | TYPE_BOTTOM;

    private String message, userID, username;
    private int flags = TYPE_SINGLE;
    private int viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;

    public Message(String id, Long time, String message, String userID, String username) {
        super(id, time);
        this.message = message;
        this.userID = userID;
        this.username = username;
    }

    public String getText() {
        return message;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int getType(){
        return MSG;
    }

    public boolean sameUser(Message message){
        return userID.equals(message.getUserID());
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setViewWidth(int viewWidth) {
        if (viewWidth > this.viewWidth)
            this.viewWidth = viewWidth;
    }

    public int getFlags(){
        return flags;
    }

    public int getViewWidth() {
        return viewWidth;
    }
}
