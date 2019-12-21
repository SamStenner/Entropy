package com.elmpool.tallr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.adapters.ItemAdapter;
import com.elmpool.tallr.models.Item;
import com.elmpool.tallr.utils.DataParser;
import com.elmpool.tallr.utils.Manager;
import com.elmpool.tallr.utils.TypeIndicator;
import com.elmpool.tallr.widgets.bounce.BounceAdapter;
import com.elmpool.tallr.widgets.bounce.BounceListener;
import com.elmpool.tallr.widgets.bounce.BounceRecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import me.everything.android.ui.overscroll.IOverScrollDecor;

public class MainActivity extends AppCompatActivity {

    private View rootView;
    private MaterialToolbar toolbar;
    private LinearLayout layout_send;
    private View shadowTop;
    private View shadowBottom;
    private TextView chipTyping;
    private TextSwitcher switcher;
    private TextInputLayout txtInpMessage;
    private TextInputEditText txtMessage;
    private ImageView btnImage;
    private BounceRecyclerView recyclerMessages;

    private Menu menu;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference refMessages;
    private DatabaseReference refTyping;
    private DatabaseReference refSubject;

    private ItemAdapter itemAdapter;
    private boolean pinsOnly;
    private TypeIndicator typeIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.activity_main);
        setSession();
        setupViews();
        Manager.setStatusBar(this);
        Manager.setNavBar(rootView, layout_send, recyclerMessages);
    }

    private void setupTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int value = Integer.valueOf(prefs.getString(getString(R.string.key_theme), getString(R.string.theme_default)));
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
        refTyping= database.getReference("meta/typing");
        refSubject = database.getReference("meta/subject");
    }

    private void showLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupViews(){

        rootView = findViewById(R.id.layout_root);
        toolbar = findViewById(R.id.toolbar_main);
        layout_send = findViewById(R.id.layout_send);
        shadowTop = findViewById(R.id.shadow_top);
        shadowBottom = findViewById(R.id.shadow_bottom);
        switcher = findViewById(R.id.switcher_title);
        chipTyping = findViewById(R.id.indicator_typing);
        txtInpMessage = findViewById(R.id.txt_inp_message);
        txtMessage = findViewById(R.id.txt_message);
        btnImage = findViewById(R.id.btn_image);
        recyclerMessages = findViewById(R.id.recycler_messages);
        setupTextInput();
        setupRecycler();
        setupToolbar();
        setupMeta();

    }

    private void setupMeta() {
        refTyping.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                typeIndicator = dataSnapshot.getValue(TypeIndicator.class);
                if(typeIndicator.isTyping() && !typeIndicator.getUserID().equals(user.getUid())) {
                    chipTyping.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!typeIndicator.isTyping()) {
                               chipTyping.setVisibility(View.GONE);
                            }
                        }
                    }, TypeIndicator.VISUAL_THRESHOLD);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        switcher.setInAnimation(this, R.anim.slide_down_in);
        switcher.setOutAnimation(this, R.anim.slide_down_out);

        refSubject.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String subject = dataSnapshot.getValue(String.class);
                switcher.setText(subject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        switcher.startAnimation(animAlpha);

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
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void togglePins(){
        pinsOnly = !pinsOnly;
        menu.getItem(1).setShowAsAction(pinsOnly ? MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER);
        itemAdapter.stopListening();
        setItemAdapter(pinsOnly);
        itemAdapter.notifyDataSetChanged();
        itemAdapter.startListening();

    }

    private void setupRecycler() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMessages.setLayoutManager(llm);
        setItemAdapter(false);
        recyclerMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                shadowTop.setSelected(recyclerView.canScrollVertically(BounceAdapter.SCROLL_DIRECTION_START));
                shadowBottom.setSelected(recyclerView.canScrollVertically(BounceAdapter.SCROLL_DIRECTION_END));
            }
        });
        recyclerMessages.setBounceListener(new BounceListener() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
            }

            @Override
            public void OnOverStart() {
                shadowTop.setSelected(true);
            }

            @Override
            public void OnOverEnd() {

            }

            @Override
            public void onReturn() {
                shadowTop.setSelected(recyclerMessages.canScrollVertically(BounceAdapter.SCROLL_DIRECTION_START));
            }
        });
    }



    private void setItemAdapter(boolean pinsOnly){
        Query query = pinsOnly ? refMessages.orderByChild("type").equalTo(Item.PIN) : refMessages;
        query = query.limitToLast(getResources().getInteger(R.integer.max_messages));
        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query, DataParser.itemSnapshotParser())
                        .build();
        itemAdapter = new ItemAdapter(options, user, this);
        recyclerMessages.setAdapter(itemAdapter);
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
        txtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean focused) {
                scrollToBottomDelayed();
            }
        });
        txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToBottomDelayed();
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void scrollToBottom(){
        int position = itemAdapter.getItemCount()-1;
        if (position >= 0) recyclerMessages.smoothScrollToPosition(position);
    }

    private void scrollToBottomDelayed(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToBottom();
            }
        }, 100);
    }

    private void sendMessage(@NotNull String message){
        message = curateMessage(message);
        if (!TextUtils.isEmpty(message)) {
            Map<String, Object> data = new HashMap<>();
            data.put("/type", Item.MSG);
            data.put("/message", message);
            data.put("/userID", user.getUid());
            data.put("/username", user.getDisplayName());
            data.put("/time", ServerValue.TIMESTAMP);
            String key = refMessages.push().getKey();
            refMessages.child(key).updateChildren(data);
            txtMessage.getText().clear();
        }
    }

    private String curateMessage(String message){
        message = message.trim();
        message = message.replaceAll("(?m)^\\s*$[\n\r]{1,}", "");
        return message;
    }

    @Override
    public void onBackPressed() {
        if (pinsOnly) {
            togglePins();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }

}
