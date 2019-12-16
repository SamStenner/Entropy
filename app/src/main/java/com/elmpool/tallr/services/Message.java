package com.elmpool.tallr.services;

import android.util.Log;

import androidx.annotation.Nullable;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message extends Item {

    private String id, message, userID, username;

    public Message() {

    }

    public void setID(String id){
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getMessage() {
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
        return isEmoji() ? MSG_EMOJI : MSG;
    }

    private boolean isEmoji(){
        int num = EmojiParser.extractEmojis(message).size();
        return EmojiManager.isOnlyEmojis(message) && num <= 12;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return ((Message)obj).getID().equals(getID());
    }
}
