package com.example.androidnotification.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidnotification.CallBackUI;
import com.example.androidnotification.R;
import com.example.androidnotification.adapter.RecyclerAdapter;
import com.example.androidnotification.custom.CallBackMove;
import com.example.androidnotification.custom.MyEvenTouchListener;
import com.example.androidnotification.custom.MyViewScroll;
import com.example.androidnotification.custom.ResizeAnimation;
import com.example.androidnotification.model.MyItemNotification;

import java.util.ArrayList;

public class MyService3 extends Service implements CallBackUI, View.OnTouchListener {
    private static final int MAX_HEIGHT = 1200;
    public static final String TAG = "SMS_SERVICE";

    private SmsBroadCastReceiver smsBroadCastReceiver;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private LinearLayout myViewGroup;
    private MyViewScroll myViewScroll;
    private LinearLayout panel;
    private View toolbar;
    private RecyclerView recyclerView;

    private ArrayList<MyItemNotification> listNotification;
    private RecyclerAdapter adapter;
    private ViewDragHelper dragHelper;


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
        View subView = minflater.inflate(R.layout.layout_notification3, myViewGroup);

        myViewScroll = subView.findViewById(R.id.sliding_down_toolbar_layout);
        toolbar = subView.findViewById(R.id.toolbar);
        panel = subView.findViewById(R.id.panel);
        recyclerView = subView.findViewById(R.id.recycleView);

        recyclerView.setClickable(true);

        myViewScroll.setOnTouchListener(this);

        dragHelper = ViewDragHelper.create(panel, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View view, int i) {
                Log.e(TAG, "try capture view");
                return true;
            }
        });


//        layoutParams.flags =/* WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.setTouchscreenBlocksFocus(false);
        }

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
    public boolean onTouch(View v, MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    private class OurViewDragHelperCallbacks {
    }
}
