package com.example.androidnotification.custom;

import android.view.MotionEvent;
import android.view.View;

public class MyEvenTouchListener implements View.OnTouchListener {
    private CallBackMove callBackMove;

    public MyEvenTouchListener(CallBackMove callBackMove) {
        this.callBackMove = callBackMove;
    }

    private int _initPoint = 0;
    private int _currentPoint = 0;
    private int _lastPoint = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _initPoint = (int) event.getRawY();
                callBackMove.mouseDown();
                break;
            case MotionEvent.ACTION_MOVE:
                _currentPoint = (int) event.getRawY();
                callBackMove.move(_currentPoint - _initPoint);
                break;
            case MotionEvent.ACTION_UP:
                _lastPoint = (int) event.getRawY();
                callBackMove.mouseUp();
                if ((_lastPoint - _initPoint) >= 0) {
                    callBackMove.goToBot();
                } else callBackMove.goToTop();
                break;
        }
        return true;
    }
}
