package com.example.teamproject_l;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class PopupActivity extends Activity {

    private String heightS;
    private String weightS;
    private String nameS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기

        EditText name = findViewById((R.id.fullname));
        EditText height = findViewById((R.id.height));
        EditText weight = findViewById((R.id.weight));



        if (height.getText().toString().length() == 0 ) {
            //공백일 때 처리할 내용
            Toast.makeText(getApplicationContext(), "이름 값이 입력되지 않았습니다. 향후 일부 컨텐츠를 이용할 수 없습니다.", Toast.LENGTH_LONG).show();
            nameS = "홍길동";
        } else {
            //공백이 아닐 때 처리할 내용
            nameS = name.getText().toString();
            //Toast.makeText(getApplicationContext(), "이름 정상입력", Toast.LENGTH_SHORT).show();
        }
        if (height.getText().toString().length() == 0 ) {
            //공백일 때 처리할 내용
            Toast.makeText(getApplicationContext(), "키 값이 입력되지 않았습니다. 향후 일부 컨텐츠를 이용할 수 없습니다.", Toast.LENGTH_LONG).show();
            heightS = "0";
        } else {
            //공백이 아닐 때 처리할 내용
            heightS = height.getText().toString();
            //Toast.makeText(getApplicationContext(), "키 정상입력", Toast.LENGTH_SHORT).show();
        }
        if (weight.getText().toString().length() == 0 ) {
            //공백일 때 처리할 내용
            Toast.makeText(getApplicationContext(), "체중 값이 입력되지 않았습니다. 향후 일부 컨텐츠를 이용할 수 없습니다.", Toast.LENGTH_LONG).show();
            weightS = "0";
        } else {
            //공백이 아닐 때 처리할 내용
            weightS = weight.getText().toString();
            //Toast.makeText(getApplicationContext(), "체중 정상입력", Toast.LENGTH_SHORT).show();
        }


        Intent intent = new Intent();
        intent.putExtra("name", nameS);
        intent.putExtra("height", heightS);
        intent.putExtra("weight", weightS);
        setResult(2, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
