package com.example.enjinear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void moveToWalkAround(android.view.View view){
//        Intent intent = new Intent(this,WalkAround.class);
        Button button = findViewById(R.id.button);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, WalkAround.class));
        });
    }
}