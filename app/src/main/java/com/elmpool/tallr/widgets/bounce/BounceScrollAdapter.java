package com.elmpool.tallr.widgets.bounce;

import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class BounceScrollAdapter extends BounceAdapter {

    BounceScrollAdapter(NestedScrollView nestedScrollView, BounceListener bounceListener) {
        super(nestedScrollView, bounceListener, LinearLayoutManager.VERTICAL);
    }

    BounceScrollAdapter(NestedScrollView nestedScrollView, BounceListener bounceListener, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(nestedScrollView, bounceListener, attrs, defStyleAttr, defStyleRes, LinearLayoutManager.VERTICAL);
    }

    BounceScrollAdapter(NestedScrollView nestedScrollView, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        super(nestedScrollView, bounceListener, ratioForwards, ratioBackwards, decelFactor, LinearLayoutManager.VERTICAL);
    }

    @Override
    public NestedScrollView getView() {
        return (NestedScrollView) super.getView();
    }

}