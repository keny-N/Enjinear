package com.example.enjinear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.provider.Settings;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WalkAround extends AppCompatActivity implements
        Runnable, View.OnClickListener {

    private long startTime;

    private TextView timerText;

    // 'Handler()' is deprecated as of API 30: Android 11.0 (R)
    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean stopRun = false;

    private final SimpleDateFormat dataFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.JAPAN);


public class WalkAround extends AppCompatActivity implements LocationListener {


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

    long startingTime = 0;
    boolean isWalking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);


        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(0));

        Thread thread;
        stopRun = false;
        thread = new Thread(this);
        thread.start();

        startTime = System.currentTimeMillis();

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        stopRun = true;
        timerText.setText(dataFormat.format(0));


        // Fine か Coarseのいずれかのパーミッションが得られているかチェックする
        // 本来なら、Android6.0以上かそうでないかで実装を分ける必要がある
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** fine location のリクエストコード（値は他のパーミッションと被らなければ、なんでも良い）*/
            final int requestCode = 1;

            // いずれも得られていない場合はパーミッションのリクエストを要求する
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode );
            return;
        }

        // 位置情報を管理している LocationManager のインスタンスを生成する
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = null;

        // GPSが利用可能になっているかどうかをチェック
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        // GPSプロバイダーが有効になっていない場合は基地局情報が利用可能になっているかをチェック
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        // いずれも利用可能でない場合は、GPSを設定する画面に遷移する
        else {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            return;
        }

        /** 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minTime = 500;
        /** 位置情報を通知するための最小距離間隔（メートル）*/
        final long minDistance = 1;

        // 利用可能なロケーションプロバイダによる位置情報の取得の開始
        // FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        // 最新の位置情報
        Location location = locationManager.getLastKnownLocation(locationProvider);

        if (location != null) {
            TextView textView = (TextView) findViewById(R.id.textView4);
            textView.setText(String.valueOf( "onCreate() : " + location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        }
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

    @Override
    public void run() {
        // 10 msec order
        int period = 10;


        while (!stopRun) {
            // sleep: period msec
            try {
                Thread.sleep(period);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                stopRun = true;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long endTime = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    long diffTime = (endTime - startTime);

                    timerText.setText(dataFormat.format(diffTime));
                }
            });
        }
    }
}

    //TODO 呼び出しは出来てるから、表示か保存したい
    protected void OnActivityResult(
            int requestCode,int resultCode,Intent data){
        if(REQUEST_CAPTURE_IMAGE== requestCode&& resultCode == Activity.RESULT_OK){
            Bitmap capturedImage=(Bitmap)data.getExtras().get("data");
            imageView1.setImageBitmap(capturedImage);
        }
    }

    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location){
        TextView textView = (TextView) findViewById(R.id.textView4);
        textView.setText(String.valueOf("onLocationChanged() : " + location.getLatitude()) + ":" + String.valueOf(location.getLongitude()));
    }

    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String provider) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String provider) {
        //プロバイダが利用可能になったら呼ばれる
    }

    //ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
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

