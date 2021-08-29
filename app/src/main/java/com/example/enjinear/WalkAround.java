package com.example.enjinear;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.enjinear.databinding.WalkAroundBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WalkAround extends AppCompatActivity implements
        LocationListener, OnMapReadyCallback, Runnable, View.OnClickListener{


    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

    private long startTime;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private Position currentPosition = new Position();
    private List<Position> route = new ArrayList<Position>();
    private TextView timerText;
    private GoogleMap mMap;
    private WalkAroundBinding binding;
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    // 'Handler()' is deprecated as of API 30: Android 11.0 (R)
    private volatile boolean stopRun = false;

    private final SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss", Locale.JAPAN);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_around);
        findViews();
        setListeners();

        //タイマー処理
        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(0));

        Thread thread;
        stopRun = false;
        thread = new Thread(this);
        thread.start();

        startTime = System.currentTimeMillis();

        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);


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
            currentPosition.setPosition(location.getLatitude(), location.getLongitude());
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    protected void findViews(){
        button1 = (Button)findViewById(R.id.button1);

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
                            //imageView1.setImageBitmap(capturedImage);
                            MediaStore.Images.Media.insertImage(getContentResolver(),capturedImage,"","");
                            Toast.makeText(getApplicationContext(), "写真を保存しました", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        stopRun = true;
        timerText.setText(dataFormat.format(0));
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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

        LatLng currentLatLng = new LatLng(this.currentPosition.lat, this.currentPosition.lon);
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Marker in Current Pos"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
    }

    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location location){

        LatLng currentLatLng = new LatLng(this.currentPosition.lat, this.currentPosition.lon);
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Marker in Current Pos"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
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


}
