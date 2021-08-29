package com.example.enjinear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;


import android.widget.EditText;

import android.widget.Button;
import android.widget.Toast;

import com.example.enjinear.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    int data = 0;
    static public String EXTRA_DATA= "a";
    static final int PERMISSION_FINE_LOC_STR = 1;
    static final int PERMISSION_COARSE_LOC_STR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_map).setOnClickListener(this);

        /// パーミッション許可を取る
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        PERMISSION_FINE_LOC_STR);
            }
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        PERMISSION_COARSE_LOC_STR);
            }
        }

    }

    public void moveToWalkAround(android.view.View view){
//        Intent intent = new Intent(this,WalkAround.class);
        Button button = findViewById(R.id.button_walkAround);
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
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    // 権限取得画面結果取得
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permission, int[] grantResults
    ){
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (grantResults.length <= 0) { return; }
        switch(requestCode){
            case PERMISSION_FINE_LOC_STR: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /// 許可が取れた場合・・・
                    /// 必要な処理を書いておく
                } else {
                    /// 許可が取れなかった場合・・・
                    Toast.makeText(this,
                            "アプリを起動できません....", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            case PERMISSION_COARSE_LOC_STR: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /// 許可が取れた場合・・・
                    /// 必要な処理を書いておく
                } else {
                    /// 許可が取れなかった場合・・・
                    Toast.makeText(this,
                            "アプリを起動できません....", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            return;
        }
    }

}