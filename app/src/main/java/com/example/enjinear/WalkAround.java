package com.example.enjinear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

public class WalkAround extends AppCompatActivity {


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

    long startingTime = 0;
    boolean isWalking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);
        findViews();
        setListeners();
    }

    protected void findViews(){
        button1 = (Button)findViewById(R.id.button1);
        imageView1 = (ImageView)findViewById(R.id.imageView1);

    }

    protected void setListeners(){
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
            }
        });
    }
    //TODO 呼び出しは出来てるから、表示か保存したい
    protected void OnActivityResult(
            int requestCode,int resultCode,Intent data){
        if(REQUEST_CAPTURE_IMAGE== requestCode&& resultCode == Activity.RESULT_OK){
            Bitmap capturedImage=(Bitmap)data.getExtras().get("data");
            imageView1.setImageBitmap(capturedImage);
        }
    }

    public void integratedStartAndStop(android.view.View view){
        if(isWalking == true){
            long stoppingTime = System.currentTimeMillis();
            LocalDateTime stoppingLocalTime = LocalDateTime.now();
            System.out.println(stoppingLocalTime);
            long walkingTime = stoppingTime - startingTime;
            System.out.println(walkingTime);
            isWalking = false;
            ((TextView) findViewById(R.id.button_integrated_startnstop)).setText("散歩時間計測開始");
        }else{
            startingTime = System.currentTimeMillis();
            LocalDateTime startingLocalTime = LocalDateTime.now();
            System.out.println(startingLocalTime);
            isWalking = true;
            ((TextView) findViewById(R.id.button_integrated_startnstop)).setText("散歩時間計測終了");
        }

    }
}
