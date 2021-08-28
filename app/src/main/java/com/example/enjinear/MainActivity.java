package com.example.enjinear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    int data = 0;
    static public String EXTRA_DATA= "a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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



}