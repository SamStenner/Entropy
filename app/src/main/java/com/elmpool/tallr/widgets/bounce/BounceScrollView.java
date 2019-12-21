package com.elmpool.tallr.widgets.bounce;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;

public class BounceScrollView extends NestedScrollView implements BounceBehaviour {

    private BounceScrollAdapter adapter;

    public BounceScrollView(@NonNull Context context) {
        super(context);
        setBounceAdapter(null);
    }

    public BounceScrollView(@NonNull Context context, float ratioForwards, float ratioBackwards, float decelFactor) {
        super(context);
        setBounceAdapter(null, null, 0, 0);
    }

    public BounceScrollView(@NonNull Context context, float ratioForwards, float ratioBackwards, float decelFactor, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener, null, 0, 0);
    }

    public BounceScrollView(@NonNull Context context, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener, null, 0, 0);
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBounceAdapter(null, attrs, 0, 0);
    }

    public BounceScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBounceAdapter(null, attrs, defStyleAttr, 0);
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener) {
        adapter = new BounceScrollAdapter(this, bounceListener);
        decorate();
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        adapter = new BounceScrollAdapter(this, bounceListener, ratioForwards, ratioBackwards, decelFactor);
        decorate();
    }

    @Override
    public BounceScrollAdapter getBounceAdapter() {
        return adapter;
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, AttributeSet attrs, int defStyleAttrs, int defStyleRes){
        adapter = new BounceScrollAdapter(this, bounceListener, attrs, defStyleAttrs, defStyleRes);
        decorate();
    }

    @Override
    public void decorate(){
        VerticalOverScrollBounceEffectDecorator decorator =
                new VerticalOverScrollBounceEffectDecorator(adapter,
                        adapter.getRatioForwards(), adapter.getRatioBackwards(), adapter.getDecelFactor());
        decorator.setOverScrollStateListener(adapter.getScrollStateListener());
    }

    @Override
    public void setBounceListener(BounceListener bounceListener) {
        adapter.setBounceListener(bounceListener);
        decorate();
    }

    @Override
    public BounceListener getBounceListener() {
        return adapter.getBounceListener();
    }

    @Override
    public void removeBounceListener(){
        adapter.removeBounceListener();
        decorate();
    }

    @Override
    public void setCanScrollStart(boolean enabled) {
        adapter.setScrollStart(enabled);
    }

    @Override
    public void setCanScrollEnd(boolean enabled) {
        adapter.setScrollEnd(enabled);
    }

    @Override
    public boolean getCanScrollStart() {
        return adapter.getCanScrollStart();
    }

    @Override
    public boolean getCanScrollEnd() {
        return adapter.getCanScrollEnd();
    }

}
