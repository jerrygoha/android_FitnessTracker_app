package com.example.teamproject_l;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunningFragment extends Fragment {

    protected TextView stepTv, distanceTv;
    protected int step;
    protected double distance;
    protected Button startButton;
    protected Button endButton;

    public RunningFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stepTv = getActivity().findViewById(R.id.steps);
        distanceTv = getActivity().findViewById(R.id.distance);
        step = ((MainActivity)getActivity()).stepCount;
        distance = ((MainActivity) getActivity()).distance;

        // 백그라운드에서 돌아올 때 데이터 유지
        if(savedInstanceState != null) {
            Toast.makeText(getActivity(),"ok",Toast.LENGTH_LONG).show();
            stepTv.setText("걸음 수 : " + savedInstanceState.getInt("stepCount"));
            distanceTv.setText("뛴 거리 : " + savedInstanceState.getDouble("distance") + " m");
        }
        else {
            stepTv.setText("걸음 수 : 0");
            distanceTv.setText("뛴 거리 : 0 m");
        }

        // 밑의 Location을 사용하기 위한 권한 확인
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
// Check Permissions Now
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
// Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1000);
            }
        }
            // 현재 위치의 위치정보를 통해 날씨 설정
            Location location = ((MainActivity) getActivity()).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setWeather(location.getLatitude(), location.getLongitude());

            startButton = getActivity().findViewById(R.id.startButton);
            endButton = getActivity().findViewById(R.id.endButton);

            // startButton이 눌렸을 때 센서 동작과 GPS정보 업데이트
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Toast.makeText(getActivity(),"start!",Toast.LENGTH_LONG).show();
                    ((MainActivity) getActivity()).lastKnownLocation = ((MainActivity) getActivity()).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    ((MainActivity) getActivity()).nowLastLocation = ((MainActivity) getActivity()).locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    ((MainActivity) getActivity()).sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                    ((MainActivity) getActivity()).stepSensor = ((MainActivity) getActivity()).sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                    ((MainActivity) getActivity()).sensorManager.registerListener((MainActivity)getActivity() , ((MainActivity) getActivity()).stepSensor, SensorManager.SENSOR_DELAY_UI);
                    ((MainActivity) getActivity()).locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (MainActivity)getActivity());
                }
            });

            // endButton이 눌리면 센서동작감지 해제 및 GPS 정보 업데이트 중지, 데이터 초기화
            endButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"end!",Toast.LENGTH_LONG).show();
                    ((MainActivity) getActivity()).sensorManager.unregisterListener((MainActivity)getActivity());
                    ((MainActivity) getActivity()).locationManager.removeUpdates(((MainActivity) getActivity()));
                    stepTv.setText("걸음 수 : 0");
                    distanceTv.setText("뛴 거리 : 0 m");
                    ((MainActivity) getActivity()).resetRunning();
                }
            });
        }



    // 어플이 백그라운드로 가기 전 데이터 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("stepCount", step);
        outState.putDouble("distance", distance);
    }

    // Ion 라이브러리를 통해 날씨정보 API를 JSON형태로 받아와 날씨정보 설정
    public void setWeather(double lat, double lon) {
        Ion.with(this).load("http://api.openweathermap.org/data/2.5/weather?lat=" + /*37.8666928*/ lat + "&lon=" + /*127.734662*/ lon+"&appid=" + "1ca874a03cf7f756c34f8b70e42b9c4e").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray weather = json.getJSONArray("weather");
                    JSONObject main = json.getJSONObject("main");

                    String weatherInfo = weather.getJSONObject(0).getString("main");
                    String weatherIconCode = weather.getJSONObject(0).getString("icon");
                    String cityName = json.getString("name");
                    double temp = main.getDouble("temp");
                    int humid = main.getInt("humidity");
                    temp -= 273.15;

                    ImageView iv = getActivity().findViewById(R.id.iv);
                    TextView cityNameTv = getActivity().findViewById(R.id.cityName);
                    TextView weatherTv = getActivity().findViewById(R.id.weather);
                    TextView tempTv = getActivity().findViewById(R.id.temp);
                    TextView humidTv = getActivity().findViewById(R.id.humid);

                    Picasso.get().load("http://openweathermap.org/img/w/" + weatherIconCode + ".png").into(iv);
                    cityNameTv.setText(cityName);
                    weatherTv.setText("날씨 : " + weatherInfo);
                    tempTv.setText("온도 : " + (int)Math.round(temp) +"℃");
                    humidTv.setText("습도 : " + humid + "%");


                } catch (Exception e1) {}
            }
        });
    }

}
