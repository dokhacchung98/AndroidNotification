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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidnotification.CallBackUI;
import com.example.androidnotification.R;
import com.example.androidnotification.adapter.RecyclerAdapter;
import com.example.androidnotification.custom.ResizeAnimation;
import com.example.androidnotification.model.MyItemNotification;

import java.util.ArrayList;

public class MyService extends Service implements CallBackUI, View.OnTouchListener {
    private static final int MAX_HEIGHT = 1200;
    public static final String TAG = "SMS_SERVICE";

    private SmsBroadCastReceiver smsBroadCastReceiver;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private Button btnClose;
    private LinearLayout myViewGroup;
    private LinearLayout layoutLarge;
    private LinearLayout layoutSmall;
    private RecyclerView recyclerView;

    private ArrayList<MyItemNotification> listNotification;
    private RecyclerAdapter adapter;

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        smsBroadCastReceiver = new SmsBroadCastReceiver(this);
        listNotification = new ArrayList<>();

        initWindownManager();

        registerMyReceiver();
        startMyService();
    }

    private void initWindownManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE /*| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN*/,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;

        myViewGroup = new LinearLayout(this);
        LayoutInflater minflater = LayoutInflater.from(this);
        View subView = minflater.inflate(R.layout.layout_notification, myViewGroup);

//        layoutParams.flags =/* WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        btnClose = subView.findViewById(R.id.btnCloseNoti);
        recyclerView = subView.findViewById(R.id.recycleView);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindowManager();
            }
        });
        layoutSmall = subView.findViewById(R.id.layoutSmall);
        layoutLarge = subView.findViewById(R.id.layoutLarge);

        layoutSmall.setVisibility(View.GONE);

        layoutSmall.setOnTouchListener(this);

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
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(getApplication(), "Last", Toast.LENGTH_LONG).show();

                }
            }
        });

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
        updateWindowManager();
    }

    private void updateWindowManager() {
//        layoutParams.flags = (layoutParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        layoutSmall.setVisibility(View.VISIBLE);
        layoutSmall.getLayoutParams().height = 50;
    }

    @Override
    public void onError(String errorMessage) {
        Log.e(TAG, errorMessage);
    }

    private int firstY;
    private int currentY;
    private int lastY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = (int) event.getRawY();
                int height = currentY;
                if (height <= MAX_HEIGHT) {
                    layoutLarge.getLayoutParams().height = height;
                } else {
                    height = MAX_HEIGHT;
                }
//                layoutLarge.getLayoutParams().height = height;
                ResizeAnimation animation = new ResizeAnimation(layoutLarge, layoutLarge.getLayoutParams().height, height);
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(100);
                layoutLarge.setAnimation(animation);
                layoutLarge.startAnimation(animation);
                animation.start();

                break;
            case MotionEvent.ACTION_UP:
                lastY = (int) event.getRawY();
                int oldHeight = layoutLarge.getLayoutParams().height;
                final int newHeight;
                if (lastY < MAX_HEIGHT / 2 - 20) {
                    newHeight = 1;
                } else {
                    newHeight = MAX_HEIGHT;
                }

                ResizeAnimation animation1 = new ResizeAnimation(layoutLarge, oldHeight, newHeight);
                animation1.setInterpolator(new AccelerateInterpolator());
                animation1.setDuration(100);
                layoutLarge.setAnimation(animation1);
                layoutLarge.startAnimation(animation1);
                animation1.start();

                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(myViewGroup);
    }
}
