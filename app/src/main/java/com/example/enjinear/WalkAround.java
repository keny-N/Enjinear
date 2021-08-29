package com.example.enjinear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

public class WalkAround extends AppCompatActivity {


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

    long startingTime = 0;

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

    ActivityResultLauncher<Intent> _launcherSelectButton1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent resultData = result.getData();
                        if(resultData != null){
                            Bitmap capturedImage=(Bitmap)resultData.getExtras().get("data");
                            imageView1.setImageBitmap(capturedImage);
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




    public void startTimeCount(android.view.View view){
        startingTime = System.currentTimeMillis();
        LocalDateTime startingLocalTime = LocalDateTime.now();
        System.out.println(startingLocalTime);
    }
    public void stopTimeCount(android.view.View view){
        long stoppingTime = System.currentTimeMillis();
        LocalDateTime stoppingLocalTime = LocalDateTime.now();
        System.out.println(stoppingLocalTime);
        long walkingTime = stoppingTime - startingTime;
        System.out.println(walkingTime);

    }


}
