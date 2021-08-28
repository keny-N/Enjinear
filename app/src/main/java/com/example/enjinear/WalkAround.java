package com.example.enjinear;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

public class WalkAround extends AppCompatActivity {
    long startingTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);
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
