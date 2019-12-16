package com.elmpool.tallr.widgets;

import android.view.View;
import androidx.core.widget.NestedScrollView;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

public class BounceScrollAdapter implements IOverScrollDecoratorAdapter {

    private final NestedScrollView scrollView;

    private boolean canScrollTop = true;
    private boolean canScrollBottom = true;

    public BounceScrollAdapter(NestedScrollView view) {
        this.scrollView = view;
    }

    @Override
    public View getView() {
        return this.scrollView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return canScrollTop && !this.scrollView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return canScrollBottom && !this.scrollView.canScrollVertically(1);
    }

    public void setScrollTopEnabled(boolean enabled) {
        this.canScrollTop = enabled;
    }

    public void setScrollBottomEnabled(boolean enabled) {
        this.canScrollBottom = enabled;
    }

}