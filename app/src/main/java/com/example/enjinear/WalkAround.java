package com.example.enjinear;

<<<<<<< HEAD
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
=======
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
>>>>>>> fca1f042a900d0cc11f0c4f6dda35b89ed198eb3

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.time.LocalDateTime;

<<<<<<< HEAD
public class WalkAround extends AppCompatActivity implements LocationListener {
=======
public class WalkAround extends AppCompatActivity {


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

>>>>>>> fca1f042a900d0cc11f0c4f6dda35b89ed198eb3
    long startingTime = 0;
    boolean isWalking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);
<<<<<<< HEAD

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
=======
        findViews();
        setListeners();
>>>>>>> fca1f042a900d0cc11f0c4f6dda35b89ed198eb3
    }

    protected void findViews(){
        button1 = (Button)findViewById(R.id.button1);
        imageView1 = (ImageView)findViewById(R.id.imageView1);

    }

    protected void setListeners(){
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
            }
        });
    }
    //TODO 呼び出しは出来てるから、表示か保存したい
    protected void OnActivityResult(
            int requestCode,int resultCode,Intent data){
        if(REQUEST_CAPTURE_IMAGE== requestCode&& resultCode == Activity.RESULT_OK){
            Bitmap capturedImage=(Bitmap)data.getExtras().get("data");
            imageView1.setImageBitmap(capturedImage);
        }
    }

<<<<<<< HEAD
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
=======
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
>>>>>>> fca1f042a900d0cc11f0c4f6dda35b89ed198eb3

    }
}
