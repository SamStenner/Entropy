package com.elmpool.tallr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.adapters.ItemAdapter;
import com.elmpool.tallr.services.Item;
import com.elmpool.tallr.services.Manager;
import com.elmpool.tallr.services.Message;
import com.elmpool.tallr.services.Pin;
import com.elmpool.tallr.services.TypeIndicator;
import com.elmpool.tallr.widgets.BounceRecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private LinearLayout layoutTitle;
    private View shaddowTop;
    private View shaddowBottom;
    private TextView lblTitle;
    private TextInputLayout txtInpMessage;
    private TextInputEditText txtMessage;
    private ImageView btnImage;
    private ProgressBar pbMessages;
    private BounceRecyclerView recyclerMessages;

    private Menu menu;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference refMessages;
    private DatabaseReference refPins;
    private DatabaseReference refTyping;

    private ChildEventListener messagesListener;
    private ChildEventListener pinsListener;
    private ValueEventListener typingListener;

    private List<Item> items = new ArrayList<>();
    private List<Item> pins = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private boolean pinsOnly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.activity_main);
        Manager.setStatusBar(this);
        setSession();
        setupViews();
    }

    private void setupTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int value = Integer.valueOf(prefs.getString("key_theme", "-1"));
        AppCompatDelegate.setDefaultNightMode(value);
    }

    private void setSession(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            showLogin();
        }
        database = FirebaseDatabase.getInstance();
        refMessages = database.getReference("messages");
        refPins = database.getReference("pins");
        refTyping= database.getReference("meta/typing");

    }

    private void showLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupViews(){
        toolbar = findViewById(R.id.toolbar_main);
        layoutTitle = findViewById(R.id.layout_title);
        shaddowTop = findViewById(R.id.shadow_top);
        shaddowBottom = findViewById(R.id.layout_send);
        lblTitle = findViewById(R.id.lbl_title);
        txtInpMessage = findViewById(R.id.txt_inp_message);
        txtMessage = findViewById(R.id.txt_message);
        btnImage = findViewById(R.id.btn_image);
        pbMessages = findViewById(R.id.pb_messages);
        recyclerMessages = findViewById(R.id.recycler_messages);
        setupTextInput();
        setupRecycler();
        setupToolbar();
    }

    private void setupToolbar() {
        int duration = 10000;
        float minAlpha = 0.3f;
        float maxAlpha = 1.0f;
        float interpFactor = 8.0f;

        Animation animAlpha = new AlphaAnimation(maxAlpha, minAlpha);
        animAlpha.setDuration(duration);
        animAlpha.setInterpolator(new AccelerateInterpolator(interpFactor));
        animAlpha.setRepeatMode(Animation.REVERSE);
        animAlpha.setRepeatCount(Animation.INFINITE);
        lblTitle.startAnimation(animAlpha);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.menu_pins:
                togglePins();
                return true;
            case R.id.menu_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void togglePins(){
        pinsOnly = !pinsOnly;
        menu.getItem(1).setShowAsAction(pinsOnly ? MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER);
        itemAdapter = new ItemAdapter(this, pinsOnly ? pins : items);
        recyclerMessages.swapAdapter(itemAdapter, true);
        itemAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void setupRecycler() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMessages.setLayoutManager(llm);
        itemAdapter = new ItemAdapter(this, items);
        setupListeners();
        applyListeners();
        recyclerMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                shaddowTop.setSelected(recyclerView.canScrollVertically(-1));
                shaddowBottom.setSelected(recyclerView.canScrollVertically(1));
            }
        });
    }

    private void setupListeners(){
        final int typeTime = 2000;
        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setID(dataSnapshot.getKey());
                addItem(message);
                if (recyclerMessages.getAdapter() == null) {
                    pbMessages.setVisibility(View.GONE);
                    recyclerMessages.setAdapter(itemAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setID(dataSnapshot.getKey());
                int index = findMessageIndex(message);
                if (index != -1) {
                    items.set(index, message);
                    itemAdapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setID(dataSnapshot.getKey());
                int index = findMessageIndex(message);
                if (index != -1) {
                    items.remove(index);
                    itemAdapter.notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        pinsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Pin pin = dataSnapshot.getValue(Pin.class);
                addItem(pin);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        typingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TypeIndicator indicator = dataSnapshot.getValue(TypeIndicator.class);
                if(indicator.isTyping() && !indicator.getUserID().equals(user.getUid())) {
                    if (items.get(items.size()-1).getType() == Item.TYPING) {
                        items.set(items.size()-1, indicator);
                    } else {
                        items.add(indicator);
                        itemAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int index = items.size() - 1;
                            Item item = items.get(index);
                            if (item.getType() == Item.TYPING
                                    && System.currentTimeMillis() - item.getTimestamp() - typeTime > 0) {
                                items.remove(index);
                                itemAdapter.notifyItemRemoved(index);
                            }
                        }
                    }, typeTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void applyListeners(){
        items.clear();
        refMessages.addChildEventListener(messagesListener);
        refPins.addChildEventListener(pinsListener);
        refTyping.addValueEventListener(typingListener);
    }

    private void addItem(Item item){
        if (item.getType() == Item.PIN){
            pins.add(item);
        }
        items.add(item);
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return Long.compare(item1.getTimestamp(), item2.getTimestamp());
            }
        });
        itemAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void setupTextInput(){
        txtMessage.setMovementMethod(new ScrollingMovementMethod());
        txtInpMessage.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txtMessage.getText().toString();
                sendMessage(message);
            }
        });
        txtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEND) {
                    String message = textView.getText().toString();
                    sendMessage(message);
                    return true;
                }
                return false;
            }
        });
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(txtMessage.getText().toString())) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("/userID", user.getUid());
                    data.put("/time", ServerValue.TIMESTAMP);
                    refTyping.updateChildren(data);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void sendMessage(@NotNull String message){
        message = message.trim();
        if (!TextUtils.isEmpty(message)) {
            Log.e("MESSAGE", message);
            Map<String, Object> data = new HashMap<>();
            data.put("/message", message);
            data.put("/userID", user.getUid());
            data.put("/username", user.getDisplayName());
            data.put("/time", ServerValue.TIMESTAMP);
            String key = refMessages.push().getKey();
            refMessages.child(key).updateChildren(data);
            txtMessage.getText().clear();
        }
    }

    private void scrollToBottom(){
        if (recyclerMessages.getAdapter() != null) {
            recyclerMessages.scrollToPosition(recyclerMessages.getAdapter().getItemCount());
        }
    }

    private int findMessageIndex(Message message){
        for (int index=0; index < items.size(); index++) {
            if (items.get(index).equals(message)){
                return index;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        if (pinsOnly) {
            togglePins();
            return;
        }
        super.onBackPressed();
    }
}
