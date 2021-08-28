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

        // Dummy List for debugging
        String []DummyJsonList = new String[16];
        for ( int i=0; i< 16; i++ ){
            DummyJsonList[i] = "JSON_DATA"+i;
        }

        //JSON受け取ってforeachで回す
        for(String Json : DummyJsonList){
            TextView historyRow = makeHistoryRow(Json);
            //コンテンツの追加
            rootView.addView(historyRow, rootView.getChildCount());
        }
    }


    private TextView makeHistoryRow(String str){
        /* AKASHI TIPS - この関数がやること ***********
        * 戻り値:View型かそれを継承したクラス(現在はデバッグのためTextView)
        * やること
        * 　　rootViewの中に並べる子要素を作ること
        * 　　　現在はTextのみの入力のため、TextVIewで問題ない
        * 　　サブのxmlを利用して、その中に要素等がある場合
        * 　　　View sub = getLayoutInflater().inflate(R.layout.<layout名>, linearLayout);
        * 　　　などとして、layoutを貰ってきて、JSONからの情報をインプットして返す
        * 入力値のJSONは以下URLの"Responses"欄を参照
        * 　https://developers.google.com/maps/documentation/roads/snap?hl=ja
        * */

        // TextView インスタンス生成
        TextView historyRow = new TextView(this);
        historyRow.setText(str);

        return historyRow;
    }

}
