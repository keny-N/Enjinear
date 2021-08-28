package com.example.enjinear;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rootView = findViewById(R.id.root);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //JSON受け取ってforeachで回す
        //foreach{

        String JSON = "JSON_DATA";
        LinearLayout linearLayout = createLinearLayout(JSON);
        //コンテンツの追加
        rootView.addView(linearLayout);
        //}
    }

    private LinearLayout createLinearLayout(String str){
        // LinearLayoutのインスタンス作成
        LinearLayout linearLayout = new LinearLayout(this);
        // LinearLayoutの特徴としてorientation(方向)を決る
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // setContentViewに設定
        setContentView(linearLayout);

        // TextView インスタンス生成
        TextView textView = new TextView(this);
        textView.setText(str);
        linearLayout.addView(textView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

}
