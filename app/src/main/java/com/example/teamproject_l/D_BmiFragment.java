package com.example.teamproject_l;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class D_BmiFragment extends Fragment {


    public D_BmiFragment() {
        // Required empty public constructor
    }

    private String heightS;
    private String weightS;
    private String newWeight = "nodefalt";
    private String checkWeight = "nodefalt";
    private int bmi;

    private int heightN;
    private int weightN;
    private int newWeightN;

    private int[] arrayWeight = new int[20]; //몸무게 변화 그래프를 위한 몸무게 배열

    private LineChart lineChart;
    private  List<Entry> entries;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_d__bmi, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        if(getArguments() != null){
            heightS = getArguments().getString("height"); // 전달한 key 값
            weightS = getArguments().getString("weight"); // 전달한 key 값
            newWeight = getArguments().getString("weightFromProfile");

            SharedPreferences pref = getActivity().getSharedPreferences("Preferences이름", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("saveheight", heightS);
            editor.putString("saveweight", weightS);
            editor.putString("saveNewWeight", newWeight);
            editor.commit();

        }
        SharedPreferences prefS = getActivity().getSharedPreferences("Preferences이름", 0);
        heightS = prefS.getString("saveheight", "177");
        weightS = prefS.getString("saveweight", "70");
        newWeight = prefS.getString("saveNewWeight", "nodefalt");


        heightN = Integer.parseInt(heightS);
        weightN = Integer.parseInt(weightS);
        int bb = weightN*10000;                 //몸무게 곱하기 10000

        if(newWeight!="nodefalt"){                          //newWeight에 값이 들어오면 실행
            newWeightN = Integer.parseInt(newWeight);
            if(weightN != newWeightN){
                weightN = newWeightN;
            }
        }


        int hh = (int) Math.pow(heightN, 2);    //키 제곱
        bmi = bb/hh;


        arrayWeight[0] = bmi;


        SharedPreferences prefs2 = getActivity().getSharedPreferences("SHARED_PREFS_FILE", Context.MODE_PRIVATE); // 저장된 List를  불러와서 비직렬화한후 arrayWeight에 다시 저장
        try {
            arrayWeight = (int[]) ObjectSerializer.deserialize(prefs2.getString("TASKS", String.valueOf(bmi)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if(newWeight != "nodefalt") {                       //newWeight에 값이 들어오면 실행
            for (int k = 0; k < arrayWeight.length; k++) {
                if (arrayWeight[k] == 0) {
                    arrayWeight[k] = bmi;
                    break;
                }
            }
        }

        SharedPreferences prefss = getActivity().getSharedPreferences("SHARED_PREFS_FILE", Context.MODE_PRIVATE); // List을 직렬화 된 객체로 SharedPreferences에 저장
        SharedPreferences.Editor editors = prefss.edit();
        try {
            editors.putString("TASKS", ObjectSerializer.serialize(arrayWeight));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editors.commit();

        entries = new ArrayList<>();


        for(int i = 0; i<arrayWeight.length; i++ ){

            entries.add(new Entry(i, arrayWeight[i]));

        }

        lineChart = getActivity().findViewById(R.id.chart);

        LineDataSet lineDataSet = new LineDataSet(entries, "BMI");


        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        SharedPreferences prefs3 = getActivity().getSharedPreferences("Preferences이름", 0);
        SharedPreferences.Editor editors3 = prefs3.edit();
        editors3.putString("saveNewWeight", checkWeight);
        editors3.commit();

        SharedPreferences prefS3 = getActivity().getSharedPreferences("Preferences이름", 0);  //차트 실행 후 newWeight값 nodefalt로 변경
        newWeight = prefS3.getString("saveNewWeight", "nodefalt");

        lineChart.animateY(250);

    }

}