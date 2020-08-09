package com.echangole.findme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.echangole.findme.utils.FirebaseMethod;

public class ActivityOtp extends AppCompatActivity {

    private Button butSubmit;
    private PinView pinView;
    private TextView tvResend,tvNumber;
    private String number;
    private String num2;
    private FirebaseMethod fireBaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        getIncomingIntent();
        intWidgets();
        sendVerificationCode();
        submitCode();
        resendCode();
    }
    private void submitCode() {
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinView.getText().toString().trim();
                fireBaseMethods.verifyCode(code);

            }
        });

    }

    private void resendCode() {
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseMethods.sendVerificationCode(number,num2);
            }
        });

    }

    private void sendVerificationCode() {
        fireBaseMethods = new FirebaseMethod(this);
        fireBaseMethods.sendVerificationCode(number,num2);

    }

    private void intWidgets() {
        butSubmit = (Button) findViewById(R.id.butSubmit);
        tvResend = (TextView) findViewById(R.id.tvResend);
        pinView= (PinView) findViewById(R.id.pinView);
        tvNumber=(TextView)findViewById(R.id.tvNumber);
        tvNumber.setText(num2);


    }

    private void getIncomingIntent() {
        number = getIntent().getStringExtra(getString(R.string.intent_number));
        num2=getIntent().getStringExtra("num2");
    }



}