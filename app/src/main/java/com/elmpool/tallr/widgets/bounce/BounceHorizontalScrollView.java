package com.elmpool.tallr.widgets.bounce;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator;

public class BounceHorizontalScrollView extends HorizontalScrollView implements BounceBehaviour {

    private BounceHorizontalScrollAdapter adapter;

    public BounceHorizontalScrollView(Context context) {
        super(context);
        setBounceAdapter(null);
    }

    public BounceHorizontalScrollView(Context context, float rationForwards, float ratioBackwards, float decelFactor) {
        super(context);
        setBounceAdapter(null, rationForwards, ratioBackwards, decelFactor);
    }

    public BounceHorizontalScrollView(Context context, float rationForwards, float ratioBackwards, float decelFactor, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener, rationForwards, ratioBackwards, decelFactor);
    }

    public BounceHorizontalScrollView(Context context, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener);
    }

    public BounceHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBounceAdapter(null, attrs, 0, 0);
    }

    public BounceHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBounceAdapter(null, attrs, defStyleAttr, 0);
    }

    public BounceHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBounceAdapter(null, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener) {
        adapter = new BounceHorizontalScrollAdapter(this, bounceListener);
        decorate();
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, AttributeSet attrs, int defStyleAttrs, int defStyleRes) {
        adapter = new BounceHorizontalScrollAdapter(this, bounceListener, attrs, defStyleAttrs, defStyleRes);
        decorate();
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        adapter = new BounceHorizontalScrollAdapter(this, bounceListener, ratioForwards, ratioBackwards, decelFactor);
        decorate();
    }

    @Override
    public BounceAdapter getBounceAdapter() {
        return adapter;
    }

    @Override
    public void decorate() {
        HorizontalOverScrollBounceEffectDecorator decorator =
                new HorizontalOverScrollBounceEffectDecorator(adapter,
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
    public void removeBounceListener() {
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
