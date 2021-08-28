package com.example.enjinear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_map).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Do something in response to button click
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

}