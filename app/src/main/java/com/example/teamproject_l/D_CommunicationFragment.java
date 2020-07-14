package com.example.teamproject_l;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class D_CommunicationFragment extends Fragment {


    public D_CommunicationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d__communication, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button updateLogButton = getActivity().findViewById(R.id.updateLog);
        Button knownIssueButton = getActivity().findViewById(R.id.knownIssue);
        final TextView patchInfo = getActivity().findViewById(R.id.patchInfoDetail);

        updateLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patchInfo.setText("안드로이드 10(Q) 이상 버전에서 음악파일의 저장경로가 비정상적으로 지정되는 현상 수정");
            }
        });

        knownIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patchInfo.setText("특정 음악파일 선택 시, 비정상적으로 종료되는 현상");
            }
        });
    }

}
