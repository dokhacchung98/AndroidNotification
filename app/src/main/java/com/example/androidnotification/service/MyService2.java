package com.example.androidnotification.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidnotification.CallBackUI;
import com.example.androidnotification.R;
import com.example.androidnotification.adapter.RecyclerAdapter;
import com.example.androidnotification.custom.CallBackMove;
import com.example.androidnotification.custom.MyEvenTouchListener;
import com.example.androidnotification.custom.MyViewScroll;
import com.example.androidnotification.custom.ResizeAnimation;
import com.example.androidnotification.model.MyItemNotification;

import java.util.ArrayList;

public class MyService2 extends Service implements CallBackUI, CallBackMove, View.OnTouchListener {
    private static final int MAX_HEIGHT = 1200;
    public static final String TAG = "SMS_SERVICE";

    private SmsBroadCastReceiver smsBroadCastReceiver;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private LinearLayout myViewGroup;
    private MyViewScroll myViewScroll;
    private LinearLayout panel;
    private RecyclerView recyclerView;
    private View toolbar;
    private TextView txtNotification;

    private ArrayList<MyItemNotification> listNotification;
    private RecyclerAdapter adapter;

    private MyEvenTouchListener myEvenTouchListener;

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        smsBroadCastReceiver = new SmsBroadCastReceiver(this);
        listNotification = new ArrayList<>();
        myEvenTouchListener = new MyEvenTouchListener(this);
        initWindownManager();

        registerMyReceiver();
        startMyService();
    }

    private void initWindownManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= 26
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;

        myViewGroup = new LinearLayout(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.layout_notification2, myViewGroup);

        myViewScroll = subView.findViewById(R.id.sliding_down_toolbar_layout);
        panel = subView.findViewById(R.id.panel);
        recyclerView = subView.findViewById(R.id.recycleView);
        toolbar = subView.findViewById(R.id.toolbar);
        txtNotification = subView.findViewById(R.id.txtNoti);
        currentHeight = panel.getHeight();


        myViewScroll.setOnTouchListener(myEvenTouchListener);

        recyclerView.setOnTouchListener(this);

        toolbar.setVisibility(View.INVISIBLE);

        adapter = new RecyclerAdapter(listNotification);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                listNotification.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                updateToolBar();
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        windowManager.addView(myViewGroup, layoutParams);
    }

    private void closeWindowManager() {
        windowManager.removeView(myViewGroup);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startMyService() {
        try {
            Intent myServiceIntent = new Intent(this, SmsBroadCastReceiver.class);
            startService(myServiceIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerMyReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(smsBroadCastReceiver, intentFilter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String from, String value) {
        listNotification.add(new MyItemNotification(from, value));
        adapter.notifyDataSetChanged();
        toolbar.setVisibility(View.VISIBLE);
        updateToolBar();
    }

    private void updateToolBar() {
        if (listNotification.size() > 0)
            txtNotification.setText("Bạn có " + listNotification.size() + " thông báo mới");
        else txtNotification.setText("Không có thông báo");
    }

    @Override
    public void onError(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeWindowManager();
    }

    @Override
    public void goToTop() {
        ResizeAnimation animation = new ResizeAnimation(panel, panel.getHeight(), 1);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(100);
        panel.setAnimation(animation);
        panel.startAnimation(animation);
        animation.start();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toolbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void goToBot() {
        ResizeAnimation animation = new ResizeAnimation(panel, panel.getHeight(), MAX_HEIGHT);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(100);
        panel.setAnimation(animation);
        panel.startAnimation(animation);
        animation.start();
    }

    private int currentHeight = 0;
    private int totalHeight = 0;

    @Override
    public void move(int distance) {
        totalHeight = currentHeight + distance;
        if (totalHeight > 1 && totalHeight < MAX_HEIGHT) {
            panel.getLayoutParams().height = totalHeight;
            panel.requestLayout();
        }
    }

    @Override
    public void mouseDown() {
        toolbar.setVisibility(View.VISIBLE);
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        currentHeight = panel.getHeight();
    }

    @Override
    public void mouseUp() {
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private int currentPosition;
    private int initPositionDrag = -1;
    private boolean isLastIndex = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentHeight = panel.getHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                currentPosition = (int) event.getRawY();
                if (!recyclerView.canScrollVertically(1)) {
                    isLastIndex = true;
                }
                if (isLastIndex) {
                    if (initPositionDrag == -1) {
                        initPositionDrag = currentPosition;
                    }
                    move(currentPosition - initPositionDrag);
                }
                break;
            case MotionEvent.ACTION_UP:

                if (currentPosition - initPositionDrag >= 0) {
                    goToBot();
                } else {
                    goToTop();
                }
                initPositionDrag = -1;
                isLastIndex = false;
                break;
        }
        return false;
    }
}
