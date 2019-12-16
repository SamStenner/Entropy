package com.elmpool.tallr.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;

public class BounceRecyclerView extends RecyclerView {

    public static float ratioFwd = 6f;
    public static float ratioBck = 1f;
    public static float decelFac = -2f;

    private BounceRecyclerAdapter decorAdapter;

    public BounceRecyclerView(@NonNull Context context) {
        super(context);
    }

    public BounceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BounceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void decorate(){
        decorAdapter = new BounceRecyclerAdapter(this);
        setOverScrollMode(OVER_SCROLL_NEVER);
        new VerticalOverScrollBounceEffectDecorator(decorAdapter, ratioFwd, ratioBck, decelFac);
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        super.setLayoutManager(layout);
        decorate();
    }

    public void setScrollTopEnabled(boolean enabled){
        decorAdapter.setScrollTopEnabled(enabled);
    }

    public void setScrollBottomEnabled(boolean enabled){
        decorAdapter.setScrollBottomEnabled(enabled);
    }


}
