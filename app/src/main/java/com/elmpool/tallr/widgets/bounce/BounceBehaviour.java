package com.elmpool.tallr.widgets.bounce;

import android.util.AttributeSet;

public interface BounceBehaviour {

    int a = 2;

    void setBounceAdapter(BounceListener bounceListener);

    void setBounceAdapter(BounceListener bounceListener, AttributeSet attrs, int defStyleAttrs, int defStyleRes);

    void setBounceAdapter(BounceListener bounceListener, float ratioForwards, float ratioBackwards, float decelFactor);

    BounceAdapter getBounceAdapter();

    void decorate();

    void setBounceListener(BounceListener bounceListener);

    BounceListener getBounceListener();

    void removeBounceListener();

    void setCanScrollStart(boolean enabled);

    void setCanScrollEnd(boolean enabled);

    boolean getCanScrollStart();

    boolean getCanScrollEnd();

}
