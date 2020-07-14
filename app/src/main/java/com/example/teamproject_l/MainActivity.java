package com.example.teamproject_l;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    protected FragmentManager fragmentManager = getSupportFragmentManager();
    protected RunningFragment fragmentRunning = new RunningFragment();
    protected MusicFragment fragmentMusic;
    protected ContentFragment fragmentContent;
    protected D_ProfileFragment fragmentProfile;
    protected D_BmiFragment fragmentBMI;
    protected D_CommunicationFragment fragmentCommunication;

    private String height; //추가
    private String weight; //추가

    private String name;
    private String savedName;
    private  String savedHeight;
    private  String savedWeight;
    public SharedPreferences prefs; //추가

    Bundle bundleForContent;
    Bundle bundleForBmi;
    Bundle bundleForProfile; //추가

    protected double lat = 0;
    protected double lon = 0;

    protected LocationManager locationManager;
    protected SensorManager sensorManager;
    protected Sensor stepSensor;
    protected Location lastKnownLocation = null;
    protected Location nowLastLocation = null;

    protected int stepCount = 0;
    protected double distance = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기 권한 설정
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        // 백그라운드에서 돌아올 때 데이터 유지
        if(savedInstanceState != null) {
            stepCount = savedInstanceState.getInt("stepCount");
            distance = savedInstanceState.getDouble("distance");

        }
        // 어플 실행시 첫 화면 RunningFragment로 설정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentRunning).commitAllowingStateLoss();

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.b_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        // NavigationDrawer 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new D_ItemSelectedListener());

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawLayout.addDrawerListener(actionBarDrawerToggle);


                // GPS 권한 설정 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
            // 날씨정보를 얻기 위해 위치정보 업데이트
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        // 첫 실행시 사용자 데이터 입력받는 팝업 화면을 띄움
        checkFirstRun();
    }

    // BottomNavigationView 탭이 눌렸을 때
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch(menuItem.getItemId())
            {
                case R.id.B_navigation_running:
                    if(fragmentRunning != null)  fragmentManager.beginTransaction().show(fragmentRunning).commit();
                    if(fragmentMusic != null)  fragmentManager.beginTransaction().hide(fragmentMusic).commit();
                    if(fragmentContent != null)  fragmentManager.beginTransaction().hide(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().hide(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().hide(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().hide(fragmentCommunication).commit();

                    break;
                case R.id.B_navigation_music:
                    if(fragmentMusic == null) {
                        fragmentMusic = new MusicFragment();
                        fragmentManager.beginTransaction().add(R.id.frameLayout, fragmentMusic).commit();
                    }
                    if(fragmentRunning != null) fragmentManager.beginTransaction().hide(fragmentRunning).commit();
                    if(fragmentMusic != null) fragmentManager.beginTransaction().show(fragmentMusic).commit();
                    if(fragmentContent != null) fragmentManager.beginTransaction().hide(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().hide(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().hide(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().hide(fragmentCommunication).commit();
                    break;
                case R.id.B_navigation_content:
                    if(fragmentContent == null) {
                        fragmentContent = new ContentFragment();
                        fragmentManager.beginTransaction().add(R.id.frameLayout, fragmentContent).commit();
                        fragmentContent.setArguments(bundleForContent);
                    }
                    if(fragmentRunning != null) fragmentManager.beginTransaction().hide(fragmentRunning).commit();
                    if(fragmentMusic != null) fragmentManager.beginTransaction().hide(fragmentMusic).commit();
                    if(fragmentContent != null) fragmentManager.beginTransaction().show(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().hide(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().hide(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().hide(fragmentCommunication).commit();
                    break;
            }
            return true;
        }
    }
    // NavigationDrawer탭이 눌렸을 때
    class D_ItemSelectedListener implements  NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.profile:
                    if(fragmentProfile == null) {
                        fragmentProfile = new D_ProfileFragment();
                        fragmentManager.beginTransaction().add(R.id.frameLayout, fragmentProfile).commit();
                        fragmentProfile.setArguments(bundleForProfile);
                    }
                    if(fragmentRunning != null) fragmentManager.beginTransaction().hide(fragmentRunning).commit();
                    if(fragmentMusic != null) fragmentManager.beginTransaction().hide(fragmentMusic).commit();
                    if(fragmentContent != null) fragmentManager.beginTransaction().hide(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().show(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().hide(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().hide(fragmentCommunication).commit();
                    break;
                case R.id.bmi:
                    if(fragmentBMI == null) {
                        fragmentBMI = new D_BmiFragment();
                        fragmentManager.beginTransaction().add(R.id.frameLayout, fragmentBMI).commit();
                        fragmentBMI.setArguments(bundleForBmi);
                    }
                    if(fragmentRunning != null) fragmentManager.beginTransaction().hide(fragmentRunning).commit();
                    if(fragmentMusic != null) fragmentManager.beginTransaction().hide(fragmentMusic).commit();
                    if(fragmentContent != null) fragmentManager.beginTransaction().hide(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().hide(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().show(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().hide(fragmentCommunication).commit();
                    break;
                case R.id.communication:
                    if(fragmentCommunication == null) {
                        fragmentCommunication = new D_CommunicationFragment();
                        fragmentManager.beginTransaction().add(R.id.frameLayout, fragmentCommunication).commit();
                    }
                    if(fragmentRunning != null) fragmentManager.beginTransaction().hide(fragmentRunning).commit();
                    if(fragmentMusic != null) fragmentManager.beginTransaction().hide(fragmentMusic).commit();
                    if(fragmentContent != null) fragmentManager.beginTransaction().hide(fragmentContent).commit();
                    if(fragmentProfile != null) fragmentManager.beginTransaction().hide(fragmentProfile).commit();
                    if(fragmentBMI != null) fragmentManager.beginTransaction().hide(fragmentBMI).commit();
                    if(fragmentCommunication != null) fragmentManager.beginTransaction().show(fragmentCommunication).commit();
                    break;
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    // NavigationDrawer가 열린 상태로 뒤로가기 키를 누르면 닫히게 함
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 센서가 걸음을 감지하면 걸음수와 뛴거리를 수정
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0] == 1.0f) {
                stepCount++;
                fragmentRunning.stepTv.setText("걸음 수 : " + String.valueOf(stepCount));

                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    if(lastKnownLocation == null) {
                        lastKnownLocation = nowLastLocation;
                    }

                    distance += nowLastLocation.distanceTo(lastKnownLocation);
                    fragmentRunning.distanceTv.setText("뛴 거리 : " + Double.parseDouble(String.format("%.3f", distance) )+ " m Test");
                    lastKnownLocation = nowLastLocation;
                }
            }
        }
    }
    // SensorEventListener 필수구현 메소드
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // finish 버튼이 눌렷을 때 데이터 리셋
    public void resetRunning() {
        this.stepCount = 0;
        this.distance = 0.0;
    }

    // 백그라운드에서 돌아왔을 때 센서와 위치정보 갱신
    @Override
    public void onResume() {
        super.onResume();
        if(sensorManager != null && stepSensor != null)
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);

    }

    // 데이터 저장을 위해 어플 재시작
    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        System.exit(0);
    }

    // 어플이 종료될 때 센서와 위치정보서비스 종료
    @Override
    public void onPause() {
        super.onPause();
        if(sensorManager != null && locationManager != null) {
            sensorManager.unregisterListener(this);
            locationManager.removeUpdates(this);
        }
    }

    // 위치정보 권한 확인
    @Override
    public void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }
    }

    //첫실행체크 및 PopupActivity 클래스로 연결
    public void checkFirstRun(){
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            Intent newIntent = new Intent(this, PopupActivity.class);
            startActivityForResult(newIntent, 1);

            prefs.edit().putBoolean("isFirstRun",false).apply();
        }
    }

    //PopupActivity에서 받아온 데이터 검증 및 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //추가
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==2){
            //데이터 받기
            Toast.makeText(getApplicationContext(), "데이터받았음", Toast.LENGTH_SHORT).show();

            name = data.getExtras().getString("name");
            height = data.getExtras().getString("height");
            weight = data.getExtras().getString("weight");

            SharedPreferences pref = getSharedPreferences("Preferences이름", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("savename", name);
            editor.putString("saveheight", height);
            editor.putString("saveweight", weight);
            editor.commit();

            savedName = pref.getString("savename", name);
            savedHeight = pref.getString("saveheight", height);
            savedWeight = pref.getString("saveweight", weight);


            bundleForContent = new Bundle();
            bundleForContent.putString("height", savedHeight); // Key, Value
            bundleForContent.putString("weight", savedWeight); // Key, Value


            bundleForBmi = new Bundle();
            bundleForBmi.putString("height", savedHeight); // Key, Value
            bundleForBmi.putString("weight", savedWeight); // Key, Value

            bundleForProfile = new Bundle();
            bundleForProfile.putString("name", savedName);
            bundleForProfile.putString("height", savedHeight); // Key, Value
            bundleForProfile.putString("weight", savedWeight); // Key, Value

        }

    }

    // 어플이 백그라운드로 가기 전 데이터 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("stepCount", stepCount);
        outState.putDouble("distance", distance);

    }

    // 위치가 변화되면 현재 위치정보와 위도 경도 갱신
    public void onLocationChanged(Location location) {
        nowLastLocation = location;
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    // LocationListener 필수구현 메소드
    public void onProviderDisabled(String provider) { }

    // LocationListener 필수구현 메소드
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
    }
    // LocationListener 필수구현 메소드
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    // 권한 관리
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}


// 권한 핸들러
class PermissionHandler{
    public static boolean isPermissionGranted(Activity mContext, String Permission, String Text, int PermissionCode) {
        if (ContextCompat.checkSelfPermission(mContext, Permission) != PackageManager.PERMISSION_GRANTED) {
            reqPermission(mContext, Text, PermissionCode, Permission);
            return false;
        }
        return true;
    }
    public static void reqPermission(Activity mContext, String Text, int PermissionCode, String Permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, Permission)) {
            ActivityCompat.requestPermissions(mContext, new String[]{Permission}, PermissionCode);
        }
    }
}