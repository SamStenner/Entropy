package com.elmpool.tallr.models;

public class EmojiMessage extends Message {

    public EmojiMessage(String id, Long time, String message, String userID, String username) {
        super(id, time, message, userID, username);
    }

    @Override
    public int getType() {
        return MSG_EMOJI;
    }
}
