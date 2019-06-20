package com.example.androidnotification.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyRecyclerView extends RecyclerView implements NestedScrollingParent {

    private View netedScrollTarget;
    private boolean netedScrollTargetIsBeingDragged = false;
    private boolean nestedScrollTargetWasInableToScroll = false;
    private boolean skipsTouchIterception = false;

    public MyRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean temporarilySkipsInterception = netedScrollTarget != null;
        if (temporarilySkipsInterception) {
            skipsTouchIterception = true;
        }
        boolean handled = super.dispatchTouchEvent(ev);

        if (temporarilySkipsInterception) {
            skipsTouchIterception = false;
            if (!handled || nestedScrollTargetWasInableToScroll) {
                handled = super.dispatchTouchEvent(ev);
            }
        }
        return handled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return !skipsTouchIterception && super.onInterceptTouchEvent(e);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (target == netedScrollTarget && !netedScrollTargetIsBeingDragged) {
            if (dyConsumed != 0) {
                netedScrollTargetIsBeingDragged = true;
                nestedScrollTargetWasInableToScroll = false;
            } else if (dyConsumed == 0 && dyUnconsumed != 0) {
                nestedScrollTargetWasInableToScroll = true;
                target.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        if (axes != 0 && View.SCROLL_AXIS_VERTICAL != 0) {
            netedScrollTarget = target;
            netedScrollTargetIsBeingDragged = false;
            nestedScrollTargetWasInableToScroll = false;
        }
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes != 0 && View.SCROLL_AXIS_VERTICAL != 0;
    }

    @Override
    public void onStopNestedScroll(View child) {
//        super.onStopNestedScroll(child);
        netedScrollTarget = null;
        netedScrollTargetIsBeingDragged = false;
        nestedScrollTargetWasInableToScroll = false;
    }
}
