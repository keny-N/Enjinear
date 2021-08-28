package com.example.enjinear;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

public class WalkAround extends AppCompatActivity {
    long startingTime = 0;
    boolean isWalking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);
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
