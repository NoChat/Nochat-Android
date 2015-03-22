package com.nexters.nochat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CertifyActivity extends ActionBarActivity {

    private static final String TAG = "CertifyActivity";

    private Button backMembership; //뒤로가기
    private EditText inputPhoneNumber; //폰번호 입력
    private Button certifyBtn; // 인증번호 전송
    private String phoneNumberValue; //폰번호 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certify);

        backMembership = (Button) findViewById(R.id.backMembership);
        inputPhoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);
        certifyBtn = (Button) findViewById(R.id.certifyBtn);

        backMembership.setOnClickListener(backMembershipListener);
        certifyBtn.setOnClickListener(certifyBtnListener);

    }

    View.OnClickListener backMembershipListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.i(TAG,"뒤로가기 버튼");
            Intent intent = new Intent(CertifyActivity.this,MemberShipActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener certifyBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            phoneNumberValue = inputPhoneNumber.getText().toString();

        }
    };
}
