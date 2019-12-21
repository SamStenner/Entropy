package com.elmpool.tallr.widgets.bounce;

import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BounceRecyclerAdapter extends BounceAdapter {

    BounceRecyclerAdapter(RecyclerView recyclerView, BounceListener bounceListener) {
        super(recyclerView, bounceListener, LinearLayoutManager.VERTICAL);
    }

    BounceRecyclerAdapter(RecyclerView recyclerView, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        super(recyclerView, bounceListener, ratioForwards, ratioBackwards, decelFactor, LinearLayoutManager.VERTICAL);
    }

    BounceRecyclerAdapter(RecyclerView recyclerView, BounceListener bounceListener, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(recyclerView, bounceListener, attrs, defStyleAttr, defStyleRes, LinearLayoutManager.VERTICAL);
    }

    @Override
    public RecyclerView getView() {
        return (RecyclerView) super.getView();
    }

    @Override
    public boolean isInAbsoluteStart() {
        return canScrollStart && !getView().canScrollVertically(SCROLL_DIRECTION_START);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return canScrollEnd && !getView().canScrollVertically(SCROLL_DIRECTION_END);
    }

}
