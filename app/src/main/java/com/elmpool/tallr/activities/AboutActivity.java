package com.elmpool.tallr.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elmpool.tallr.BuildConfig;
import com.elmpool.tallr.R;
import com.elmpool.tallr.utils.Manager;
import com.elmpool.tallr.widgets.bounce.BounceAdapter;
import com.elmpool.tallr.widgets.bounce.BounceListener;
import com.elmpool.tallr.widgets.bounce.BounceScrollView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import me.everything.android.ui.overscroll.IOverScrollDecor;

public class AboutActivity extends AppCompatActivity {

    private View rootView;
    private BounceScrollView scroll;
    private View shadow;
    private LinearLayout layoutScroll;
    private TextView lblVersion;
    private ImageView imgDev;
    private Chip chipWebsite;
    private Chip chipEmail;
    private Chip chipLinkedIn;
    private Chip chipTwitter;
    private Chip chipGitHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupViews();
        setToolbar();
        setupChips();
        setupApp();
        setupScroll();
        Manager.setStatusBar(this);
        Manager.setNavBar(this, rootView, layoutScroll);
    }

    private void setToolbar(){
        MaterialToolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews(){
        rootView = findViewById(R.id.layout_root);
        scroll = findViewById(R.id.scroll_about);
        layoutScroll = findViewById(R.id.layout_interior);
        shadow = findViewById(R.id.shadow);
        imgDev = findViewById(R.id.img_dev);
        lblVersion = findViewById(R.id.lbl_app_version);
        chipWebsite = findViewById(R.id.chip_website);
        chipEmail = findViewById(R.id.chip_email);
        chipLinkedIn = findViewById(R.id.chip_linkedin);
        chipTwitter = findViewById(R.id.chip_twitter);
        chipGitHub = findViewById(R.id.chip_github);
    }

    private void setupApp(){
        lblVersion.setText(String.format("v%s", BuildConfig.VERSION_NAME));
        imgDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getString(R.string.lbl_ouch);
                Toast.makeText(AboutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupScroll(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    setShadow(BounceAdapter.SCROLL_DIRECTION_START);
                }
            });
        } else {
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    setShadow(BounceAdapter.SCROLL_DIRECTION_START);
                }
            });
        }
        scroll.setBounceListener(new BounceListener() {

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

        });
    }

    private void setShadow(boolean selected){
        shadow.setSelected(selected);
    }

    private void setShadow(int direction){
        shadow.setSelected(scroll.canScrollVertically(direction));
    }

    private void setupChips(){
        chipWebsite.setOnClickListener(Manager.buildLinkListener(this, R.string.link_website));
        chipEmail.setOnClickListener(Manager.buildEmailListener(this, R.string.link_email));
        chipLinkedIn.setOnClickListener(Manager.buildLinkListener(this, R.string.link_linkedin));
        chipTwitter.setOnClickListener(Manager.buildLinkListener(this, R.string.link_twitter));
        chipGitHub.setOnClickListener(Manager.buildLinkListener(this, R.string.link_github));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
