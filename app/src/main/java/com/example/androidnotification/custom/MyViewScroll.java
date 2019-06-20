package com.example.androidnotification.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyViewScroll extends LinearLayout{

    public MyViewScroll(Context context) {
        super(context);
    }

    public MyViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
