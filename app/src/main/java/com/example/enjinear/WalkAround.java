package com.example.enjinear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;

public class WalkAround extends AppCompatActivity implements
        Runnable, View.OnClickListener{


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

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
        findViews();
        setListeners();

        //タイマー処理
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

    protected void findViews(){
        button1 = (Button)findViewById(R.id.button1);
        imageView1 = (ImageView)findViewById(R.id.imageView1);

    }

    ActivityResultLauncher<Intent> _launcherSelectButton1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent resultData = result.getData();
                        if(resultData != null){
                            Bitmap capturedImage=(Bitmap)resultData.getExtras().get("data");
                            //imageView1.setImageBitmap(capturedImage);
                            MediaStore.Images.Media.insertImage(getContentResolver(),capturedImage,"","");
                        }
                    }
                }
            }
    );

    protected void setListeners(){
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                _launcherSelectButton1.launch(intent);
            }
        });
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
