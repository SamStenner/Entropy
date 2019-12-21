package com.elmpool.tallr.utils;

import androidx.annotation.NonNull;

import com.elmpool.tallr.models.EmojiMessage;
import com.elmpool.tallr.models.Item;
import com.elmpool.tallr.models.Message;
import com.elmpool.tallr.models.Pin;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

public class DataParser {

    public static SnapshotParser<Item> itemSnapshotParser() {
        return new SnapshotParser<Item>() {
            @NonNull
            @Override
            public Item parseSnapshot(@NonNull DataSnapshot snapshot) {
                int type = snapshot.child("type").getValue(Integer.class);
                String id = snapshot.getKey();
                Long time = snapshot.child("time").getValue(Long.class);
                switch (type) {
                    case Item.PIN: return parsePin(id, time, snapshot);
                    case Item.MSG: return parseMessage(id, time, snapshot);
                    default: throw new IllegalArgumentException();
                }
            }
        };
    }

    private static Message parseMessage(String id, Long time, DataSnapshot snapshot){
        String text = snapshot.child("message").getValue(String.class);
        String userID = snapshot.child("userID").getValue(String.class);
        String username = snapshot.child("username").getValue(String.class);
        return isEmoji(text) ?
                new EmojiMessage(id, time, text, userID, username) :
                new Message(id, time, text, userID, username);
    }

    private static Item parsePin(String id, Long time, DataSnapshot snapshot) {
        String text = snapshot.child("message").getValue(String.class);
        int pinType = snapshot.child("pinType").getValue(Integer.class);
        return new Pin(id, time, text, pinType);
    }

    private static boolean isEmoji(String message){
        int num = EmojiParser.extractEmojis(message).size();
        return EmojiManager.isOnlyEmojis(message) && num <= 12;
    }

}
