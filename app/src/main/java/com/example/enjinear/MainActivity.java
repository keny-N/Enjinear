package com.example.enjinear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;


import android.widget.EditText;

import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    int data = 0;
    static public String EXTRA_DATA= "a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_map).setOnClickListener(this);
    }


    public void moveToWalkAround(android.view.View view){
//        Intent intent = new Intent(this,WalkAround.class);
        Button button = findViewById(R.id.button);
        button.setOnClickListener((View v) -> {
            startActivity(new Intent(this, WalkAround.class));
        });
    }


     public void transitionHistory(android.view.View view){
        //インデントの作成
        Intent intent = new Intent(this,HistoryActivity.class);
        //データをセット
        //EditText editText = (EditText)this.findViewById(R.id.editText);
        //intent.putExtra("sendText",editText.getText().toString());
         intent.putExtra(EXTRA_DATA, data);

         //遷移先の画面を起動
        startActivity(intent);
     }


    @Override
    public void onClick(View v) {
        // Do something in response to button click
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }


}