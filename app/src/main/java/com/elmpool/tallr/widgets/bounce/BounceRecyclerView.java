package com.elmpool.tallr.widgets.bounce;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;

public class BounceRecyclerView extends RecyclerView implements BounceBehaviour {

    private BounceRecyclerAdapter adapter;

    public BounceRecyclerView(@NonNull Context context) {
        super(context);
        setBounceAdapter(null);
    }

    public BounceRecyclerView(@NonNull Context context, float ratioForwards, float ratioBackwards, float decelFactor) {
        super(context);
        setBounceAdapter(null, ratioForwards, ratioBackwards, decelFactor);
    }

    public BounceRecyclerView(@NonNull Context context, float ratioForwards, float ratioBackwards, float decelFactor, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener, ratioForwards, ratioBackwards, decelFactor);
    }

    public BounceRecyclerView(@NonNull Context context, BounceListener bounceListener) {
        super(context);
        setBounceAdapter(bounceListener);
    }

    public BounceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBounceAdapter(null, attrs, 0, 0);
    }

    public BounceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBounceAdapter(null, attrs, defStyleAttr, 0);
    }

    public BounceRecyclerView(RecyclerView recyclerView, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        this(recyclerView.getContext());
        setBounceAdapter(recyclerView, bounceListener, ratioForwards, ratioBackwards, decelFactor);
    }

    private void setBounceAdapter(RecyclerView recyclerView, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        adapter = new BounceRecyclerAdapter(recyclerView, bounceListener, ratioForwards, ratioBackwards, decelFactor);
        decorate();
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener) {
        adapter = new BounceRecyclerAdapter(this, bounceListener);
        decorate();
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor) {
        adapter = new BounceRecyclerAdapter(this, bounceListener, ratioForwards, ratioBackwards, decelFactor);
        decorate();
    }

    @Override
    public BounceAdapter getBounceAdapter() {
        return adapter;
    }

    @Override
    public void setBounceAdapter(BounceListener bounceListener, AttributeSet attrs, int defStyleAttrs, int defStyleRes){
        adapter = new BounceRecyclerAdapter(this, bounceListener, attrs, defStyleAttrs, defStyleRes);
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
