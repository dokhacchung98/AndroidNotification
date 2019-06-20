package com.example.androidnotification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidnotification.service.MyService2;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart;
    private Button btnStop;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SYSTEM_ALERT_WINDOW},
                1);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStopService);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void startSV() {
        Intent intent = new Intent(this, MyService2.class);
        startService(intent);
        Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
    }

    private void stopSV() {
        Intent intent = new Intent(this, MyService2.class);
        stopService(intent);
        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                startSV();
                break;
            case R.id.btnStopService:
                stopSV();
                break;
        }
    }
}
