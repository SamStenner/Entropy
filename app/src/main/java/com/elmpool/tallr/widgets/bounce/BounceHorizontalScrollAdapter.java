package com.elmpool.tallr.widgets.bounce;

import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import androidx.recyclerview.widget.LinearLayoutManager;

public class BounceHorizontalScrollAdapter extends BounceAdapter {

    BounceHorizontalScrollAdapter(HorizontalScrollView horizontalScrollView, BounceListener bounceListener) {
        super(horizontalScrollView, bounceListener, LinearLayoutManager.HORIZONTAL);
    }

    BounceHorizontalScrollAdapter(HorizontalScrollView horizontalScrollView, BounceListener bounceListener, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(horizontalScrollView, bounceListener, attrs, defStyleAttr, defStyleRes, LinearLayoutManager.HORIZONTAL);
    }

    BounceHorizontalScrollAdapter(HorizontalScrollView horizontalScrollView, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        super(horizontalScrollView, bounceListener, ratioForwards, ratioBackwards, decelFactor, LinearLayoutManager.HORIZONTAL);
    }

    @Override
    public HorizontalScrollView getView() {
        return (HorizontalScrollView) super.getView();
    }

    @Override
    public boolean isInAbsoluteStart() {
        return canScrollStart && !getView().canScrollHorizontally(SCROLL_DIRECTION_START);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return canScrollEnd && !getView().canScrollHorizontally(SCROLL_DIRECTION_END);
    }

}