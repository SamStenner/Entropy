package com.elmpool.tallr.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;

public class BounceScrollView extends NestedScrollView {

    public static float ratioFwd = 4f;
    public static float ratioBck = 1f;
    public static float decelFac = -2f;

    private BounceScrollAdapter decorAdapter;

    public BounceScrollView(@NonNull Context context) {
        super(context);
        decorate();
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        decorate();
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        decorate();
    }

    private void decorate(){
        decorAdapter = new BounceScrollAdapter(this);
        setOverScrollMode(OVER_SCROLL_NEVER);
        new VerticalOverScrollBounceEffectDecorator(decorAdapter,
                BounceRecyclerView.ratioFwd,
                BounceRecyclerView.ratioBck,
                BounceRecyclerView.decelFac);
    }

    public void setScrollTopEnabled(boolean enabled){
        decorAdapter.setScrollTopEnabled(enabled);
    }

    public void setScrollBottomEnabled(boolean enabled){
        decorAdapter.setScrollBottomEnabled(enabled);
    }

}
