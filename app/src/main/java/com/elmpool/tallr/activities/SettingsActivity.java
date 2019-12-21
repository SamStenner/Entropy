package com.elmpool.tallr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.fragments.PreferenceFragment;
import com.elmpool.tallr.utils.Manager;
import com.elmpool.tallr.widgets.bounce.BounceAdapter;
import com.elmpool.tallr.widgets.bounce.BounceListener;
import com.elmpool.tallr.widgets.bounce.BounceRecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import me.everything.android.ui.overscroll.IOverScrollDecor;

public class SettingsActivity extends AppCompatActivity {

    private View rootView;
    private BounceRecyclerView recycler;
    private View shadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setViews();
        setToolbar();
        Manager.setStatusBar(this);
        PreferenceFragment prefFragment = new PreferenceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_preferences, prefFragment).commit();
    }

    public void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                setShadow(-1);
            }
        });

        float ratioForwards = ResourcesCompat.getFloat(getResources(), R.dimen.scroll_ratio_forwards);
        float ratioBackwards = ResourcesCompat.getFloat(getResources(), R.dimen.scroll_ratio_backwards);
        float decelFactor = ResourcesCompat.getFloat(getResources(), R.dimen.scroll_decel_factor);

        BounceListener bounceListener = new BounceListener() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {

            }

            @Override
            public void OnOverStart() {
                setShadow(true);
            }

            @Override
            public void OnOverEnd() {

            }

            @Override
            public void onReturn() {
                setShadow(BounceAdapter.SCROLL_DIRECTION_START);
            }
        };

        this.recycler = new BounceRecyclerView(recyclerView, bounceListener, ratioForwards, ratioBackwards, decelFactor);

        Manager.setNavBar(this, rootView, recyclerView);
    }

    private void setShadow(boolean selected){
        shadow.setSelected(selected);
    }

    private void setShadow(int direction){
        shadow.setSelected(recycler.canScrollVertically(direction));
    }

    private void setViews(){
        rootView = findViewById(R.id.layout_root);
        shadow = findViewById(R.id.shadow);
    }

    private void setToolbar(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
