package com.elmpool.tallr.widgets;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

public class BounceRecyclerAdapter implements IOverScrollDecoratorAdapter {

    private RecyclerView recyclerView;

    private boolean canScrollTop = true;
    private boolean canScrollBottom = true;


    public BounceRecyclerAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public View getView() {
        return recyclerView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return canScrollTop && !this.recyclerView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return canScrollBottom && !this.recyclerView.canScrollVertically(1);
    }

    public void setScrollTopEnabled(boolean enabled) {
        this.canScrollTop = enabled;
    }

    public void setScrollBottomEnabled(boolean enabled) {
        this.canScrollBottom = enabled;
    }

}
