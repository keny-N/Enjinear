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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.enjinear.databinding.WalkAroundBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalkAround extends AppCompatActivity implements LocationListener, OnMapReadyCallback, Runnable, View.OnClickListener {
    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button button1;
    ImageView imageView1;

    long startingTime = 0;
    long endTime;
    long startTime;
    private TextView timerText;
    boolean isWalking = false;

    Timer timer;
    private volatile boolean stopRun;
    // 'Handler()' is deprecated as of API 30: Android 11.0 (R)
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Position currentPosition = new Position();
    private List<Position> route = new ArrayList<Position>();

    private GoogleMap mMap;
    private WalkAroundBinding binding;
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private List<Polyline> polyline1 = new ArrayList<Polyline>();;
    private Gson responsePosData = new Gson();

    private final SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss", Locale.JAPAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WalkAroundBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.walk_around);

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
            currentPosition.setPosition(location.getLatitude(), location.getLongitude());
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        timer = new Timer();
        timer.scheduleAtFixedRate(
            new TimerTask()
            {
                @Override
                public void run()
                {
                    addPolyline(currentPosition);
                }
            }, 10, 60000);

        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(0));

        startTime = System.currentTimeMillis();

        Thread thread;
        stopRun = false;
        thread = new Thread(this);
        thread.start();


        Button stopButton = findViewById(R.id.button_integrated_startnstop);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        stopRun = true;
        timerText.setText(dataFormat.format(0));
    }

    @Override
    public void run() {
        // 10 msec order
        int period = 100;

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
                    endTime = System.currentTimeMillis();
                    // カウント時間 = 経過時間 - 開始時間
                    long diffTime = (endTime - startTime);

                    timerText.setText(dataFormat.format(diffTime));
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
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

    public void integratedStartAndStop(View view){
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

    public void addPolyline(Position pos){
        // 大雑把な経路情報を作成
        route.add(pos);
        deletePolyline();
        int i = 1;
        for (Position p : route) {
            if(i == route.size()) break;
            LatLng currentLatLng = new LatLng(route.get(i-1).lat, route.get(i-1).lon);
            LatLng nextLatLng = new LatLng(route.get(i).lat, route.get(i).lon);
            polyline1.add(mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(currentLatLng, nextLatLng)));
            i++;
        }

        // TODO: 位置情報から取り出した大雑把なルートを位置情報に変換(getRoadRoute())
        //https://developers.google.com/maps/documentation/roads/snap?hl=ja

    }

    public void deletePolyline(){
        for(Polyline line : polyline1)
        {
            line.remove();
        }
    }

    public List<Position> getRouteFromResponse(String resbody){
        List<Position> posList = new ArrayList<Position>();
        // TODO: JsonのStringからGSONでクラスに変換する。
        // TODO: 保管するクラスの実装
        // https://codechacha.com/ja/java-gson/

        // TODO: クラスからPositionを取り出しリスト化
        //https://developers.google.com/maps/documentation/roads/snap?hl=ja

        return posList;
    }

    public String getRoadRoute(Position positionList[] ) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = "https://roads.googleapis.com/v1/snapToRoads?path=";
        for (Position pos: positionList){
            url += pos.lat+"."+pos.lon+"|";
        }
        url = url.substring(0,url.length()-1);
        url += "&interpolate=true&key=YOUR_API_KEY";

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        String response = client.newCall(request).execute().body().string();

        return response;
    }

    public Position getCurrentPosition(){
        Position pos = new Position();

        return pos;
    }
}
