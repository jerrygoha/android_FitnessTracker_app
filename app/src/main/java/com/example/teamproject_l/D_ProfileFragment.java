package com.example.teamproject_l;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class D_ProfileFragment extends Fragment {


    public D_ProfileFragment() {
        // Required empty public constructor
    }


    private Bundle bundleForBmi;

    private String name;
    private String heightS;
    private String weightS;
    private String weightB;

    TextView userNameZ;
    TextView userHeightZ;
    TextView userWeightZ;
    EditText weight;
    Button addbutton;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d__profile, container, false);



        // Inflate the layout for this fragment
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userNameZ = getActivity().findViewById(R.id.userName);
        userHeightZ = getActivity().findViewById(R.id.userHeight);
        userWeightZ = getActivity().findViewById(R.id.userWeight);


        if(getArguments() != null){
            name = getArguments().getString("name");
            heightS = getArguments().getString("height"); // 전달한 key 값
            weightS = getArguments().getString("weight"); // 전달한 key 값

            SharedPreferences pref = getActivity().getSharedPreferences("Preferences이름", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", name);
            editor.putString("saveheight", heightS);
            editor.putString("saveweight", weightS);
            editor.commit();

        }

        SharedPreferences prefS = getActivity().getSharedPreferences("Preferences이름", 0);
        name = prefS.getString("name", "홍길동");
        heightS = prefS.getString("saveheight", "177");
        weightS = prefS.getString("saveweight", "70");
        weightB = prefS.getString("saveNewWeight", "70");

        userNameZ.setText(name);
        userHeightZ.setText(heightS);
        userWeightZ.setText(weightS);

        addbutton = getActivity().findViewById(R.id.newWeight);
        addbutton.setOnClickListener(new View.OnClickListener() {       //버튼 클릭시 실행되는 부분
            @Override
            public void onClick(View view) {

                weight = getActivity().findViewById((R.id.addWeight));
                weightB = weight.getText().toString();
                userWeightZ.setText(weightB);

                SharedPreferences pref = getActivity().getSharedPreferences("Preferences이름", 0);    //데이터 저장
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", name);
                editor.putString("saveheight", heightS);
                editor.putString("saveweight", weightB);
                editor.putString("saveNewWeight", weightB);
                editor.commit();


                bundleForBmi = new Bundle();                                //BMIFragment로 데이터 보냄

                bundleForBmi.putString("weightFromProfile", weightB);
                bundleForBmi.putString("height", heightS);
                bundleForBmi.putString("weight", weightS);

                if(((MainActivity) getActivity()).fragmentBMI == null) {
                    ((MainActivity) getActivity()).fragmentBMI = new D_BmiFragment();
                    ((MainActivity) getActivity()).fragmentManager.beginTransaction().add(R.id.frameLayout, ((MainActivity) getActivity()).fragmentBMI).commit();
                    ((MainActivity) getActivity()).fragmentBMI.setArguments(bundleForBmi);
                }
                else
                    ((MainActivity) getActivity()).fragmentBMI.setArguments(bundleForBmi);
                ((MainActivity) getActivity()).restartApp(getActivity());

            }
        });


    }

}