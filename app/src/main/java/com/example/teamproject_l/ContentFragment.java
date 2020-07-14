package com.example.teamproject_l;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment {


    public ContentFragment() {
        setRetainInstance(true);
        // Required empty public constructor
    }

    private WebView mWebView;
    private  String heightS;
    private  String weightS;

    private  String url;
    private  int heightN = 177; //초기값
    private  int weightN = 70;  //초기값
    private  int bmi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            heightS = getArguments().getString("height"); // 전달한 key 값
            weightS = getArguments().getString("weight"); // 전달한 key 값


            SharedPreferences pref = getActivity().getSharedPreferences("Preferences이름", 0);    //데이터 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("saveheight", heightS);
            editor.putString("saveweight", weightS);
            editor.commit();

        }

        SharedPreferences prefS = getActivity().getSharedPreferences("Preferences이름", 0);       //저장된 데이터 불러오기
        heightS = prefS.getString("saveheight", "177");
        weightS = prefS.getString("saveweight", "70");

        heightN = Integer.parseInt(heightS);
        weightN = Integer.parseInt(weightS);


        url="https://www.youtube.com";
        bmi = (weightN*10000)/(heightN*heightN);

        if(bmi<19){ //저체중
            url="https://www.youtube.com/results?search_query=%EC%A0%80%EC%B2%B4%EC%A4%91+%EC%9A%B4%EB%8F%99%EB%B2%95";
        }else if(bmi>=19 && bmi<23){ //정상체중
            url="https://www.youtube.com/results?search_query=+%EC%A0%95%EC%83%81%EC%B2%B4%EC%A4%91+%EC%9A%B4%EB%8F%99%EB%B2%95";
        }else if(bmi>=23 && bmi<25){ //과체중
            url="https://www.youtube.com/results?search_query=%EA%B3%BC%EC%B2%B4%EC%A4%91+%EC%9A%B4%EB%8F%99%EB%B2%95";
        }else if(bmi>=25 && bmi<30){ //비만
            url="https://www.youtube.com/results?search_query=%EB%B9%84%EB%A7%8C+%EC%9A%B4%EB%8F%99%EB%B2%95";
        }else if(bmi>=30){ //고도비만
            url="https://www.youtube.com/results?search_query=%EA%B3%A0%EB%8F%84%EB%B9%84%EB%A7%8C+%EC%9A%B4%EB%8F%99%EB%B2%95";
        }




        View v=inflater.inflate(R.layout.fragment_content, container, false);

        mWebView = (WebView) v.findViewById(R.id.webView);
        mWebView.loadUrl(url);

        mWebView.canGoBack();
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        return v;
    }


}