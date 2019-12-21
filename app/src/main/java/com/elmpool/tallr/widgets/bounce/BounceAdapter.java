package com.elmpool.tallr.widgets.bounce;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.elmpool.tallr.R;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.OverScrollBounceEffectDecoratorBase;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

import static me.everything.android.ui.overscroll.IOverScrollState.*;

public abstract class BounceAdapter implements IOverScrollDecoratorAdapter {

    public static final int SCROLL_DIRECTION_START = -1;
    public static final int SCROLL_DIRECTION_END = 1;

    private float ratioForwards = OverScrollBounceEffectDecoratorBase.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD;
    private float ratioBackwards = OverScrollBounceEffectDecoratorBase.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK;
    private float decelFactor = OverScrollBounceEffectDecoratorBase.DEFAULT_DECELERATE_FACTOR;

    boolean canScrollStart = true;
    boolean canScrollEnd = true;

    private BounceListener bounceListener;
    private IOverScrollStateListener scrollStateListener;
    private View view;

    private int orientation;

    BounceAdapter(View view, BounceListener bounceListener, int orientation){
        this.view = view;
        this.orientation = orientation;
        setBounceListener(bounceListener);
        this.view.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    BounceAdapter(View view, BounceListener bounceListener, AttributeSet attrs, int defStyleAttr, int defStyleRes, int orientation) {
        this(view, bounceListener, orientation);
        setAttributes(view.getContext(), attrs, defStyleAttr, defStyleRes);
    }

    BounceAdapter(View view, BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor, int orientation) {
        this(view, bounceListener, orientation);
        this.ratioForwards = ratioForwards;
        this.ratioBackwards = ratioBackwards;
        this.decelFactor = decelFactor;
    }

    @Override
    public View getView() {
        return view;
    }

    void setBounceListener(final BounceListener bounceListener){
        this.bounceListener = bounceListener;
        this.scrollStateListener = new IOverScrollStateListener() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                if (bounceListener != null) {
                    bounceListener.onOverScrollStateChange(decor, oldState, newState);
                    if (oldState == STATE_IDLE && newState == STATE_DRAG_START_SIDE) bounceListener.OnOverEnd();
                    if (oldState == STATE_IDLE && newState == STATE_DRAG_END_SIDE) bounceListener.OnOverStart();
                    if (newState == STATE_IDLE || newState == STATE_BOUNCE_BACK) bounceListener.onReturn();
                }
            }
        };
    }

    void removeBounceListener(){
        bounceListener = null;
    }

    private void setAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray values = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BounceView,
                defStyleAttr, defStyleRes);
        try {
            ratioForwards = values.getFloat(R.styleable.BounceView_ratioForwards, ratioForwards);
            ratioBackwards = values.getFloat(R.styleable.BounceView_ratioBackwards, ratioBackwards);
            decelFactor = values.getFloat(R.styleable.BounceView_decelFactor, decelFactor);
        } finally {
            values.recycle();
        }
    }

    public float getRatioForwards() {
        return ratioForwards;
    }

    public float getRatioBackwards() {
        return ratioBackwards;
    }

    public float getDecelFactor() {
        return decelFactor;
    }

    BounceListener getBounceListener() {
        return bounceListener;
    }

    IOverScrollStateListener getScrollStateListener() {
        return scrollStateListener;
    }

    @Override
    public boolean isInAbsoluteStart() {
        if (orientation == LinearLayoutManager.HORIZONTAL){
            return canScrollStart && !getView().canScrollHorizontally(SCROLL_DIRECTION_START);
        }
        return canScrollStart && !getView().canScrollVertically(SCROLL_DIRECTION_START);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        if (orientation == LinearLayoutManager.HORIZONTAL){
            return canScrollStart && !getView().canScrollHorizontally(SCROLL_DIRECTION_END);
        }
        return canScrollStart && !getView().canScrollVertically(SCROLL_DIRECTION_END);
    }

    void setScrollStart(boolean enabled) {
        this.canScrollStart = enabled;
    }

    void setScrollEnd(boolean enabled) {
        this.canScrollEnd = enabled;
    }

    boolean getCanScrollStart() {
        return canScrollStart;
    }

    boolean getCanScrollEnd() {
        return canScrollEnd;
    }

}
