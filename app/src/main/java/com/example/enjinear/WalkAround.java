package com.example.enjinear;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WalkAround extends AppCompatActivity implements
        Runnable, View.OnClickListener {

    private long startTime;

    private TextView timerText;

    // 'Handler()' is deprecated as of API 30: Android 11.0 (R)
    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean stopRun = false;

    private final SimpleDateFormat dataFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.JAPAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);

        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(0));

        Thread thread;
        stopRun = false;
        thread = new Thread(this);
        thread.start();

        startTime = System.currentTimeMillis();

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        stopRun = true;
        timerText.setText(dataFormat.format(0));
    }

    @Override
    public void run() {
        // 10 msec order
        int period = 10;

        while (!stopRun) {
            // sleep: period msec
            try {
                Thread.sleep(period);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                stopRun = true;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long endTime = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    long diffTime = (endTime - startTime);

                    timerText.setText(dataFormat.format(diffTime));
                }
            });
        }
    }
}