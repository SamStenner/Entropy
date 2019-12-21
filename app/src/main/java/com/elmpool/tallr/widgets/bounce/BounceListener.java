package com.elmpool.tallr.widgets.bounce;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;

public interface BounceListener extends IOverScrollStateListener {

    @Override
    void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState);

    void OnOverStart();

    void OnOverEnd();

    void onReturn();

}
