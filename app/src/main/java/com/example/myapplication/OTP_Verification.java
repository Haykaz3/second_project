package com.example.myapplication;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OTP_Verification extends AppCompatActivity {

    private EditText otpEt1, otpEt2, otpEt3, otpEt4;
    private TextView resendBtn;
    private Button verifyBtn;

    //true after every 60 seconds
    private boolean resendEnabled = false;

    // resend time in seconds
    private int resendTime = 60;

    private int selectedEtPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEt1 = findViewById(R.id.otpEt1);
        otpEt2 = findViewById(R.id.otpEt2);
        otpEt3 = findViewById(R.id.otpEt3);
        otpEt4 = findViewById(R.id.otpEt4);

        verifyBtn = findViewById(R.id.verifyBtn);

        resendBtn = findViewById(R.id.resendBtn);

        final TextView otpEmail = findViewById(R.id.otpEmail);

        //getting email from Login activity through intent

        final String getEmail = getIntent().getStringExtra("email");

        // setting email to TextView
        otpEmail.setText(getEmail);

        otpEt1.addTextChangedListener(textWatcher);
        otpEt2.addTextChangedListener(textWatcher);
        otpEt3.addTextChangedListener(textWatcher);
        otpEt4.addTextChangedListener(textWatcher);

        // default open keyboard at otpEt1
        showKeyboard(otpEt1);

        // start resend count down timer
        startCountDownTimer();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resendEnabled) {

                    //handle your resend code here

                    //start new resend count down timer
                    startCountDownTimer();
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String generateOtp = otpEt1.getText().toString() + otpEt2.getText().toString() + otpEt3.getText().toString() + otpEt4.getText().toString();

                if (generateOtp.length() == 4) {

                    //handle your otp verification here
                }
            }
        });

    }

    private void showKeyboard(EditText otpET) {
        otpET.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer() {

        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("99000000"));

        new CountDownTimer(resendTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend Code (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {

                resendEnabled = true;
                resendBtn.setText("Resend Code");
            }
        }.start();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() > 0) {

                if (selectedEtPosition == 0) {

                    selectedEtPosition = 1;
                    showKeyboard(otpEt2);

                } else if (selectedEtPosition == 1) {

                    selectedEtPosition = 2;
                    showKeyboard(otpEt3);

                } else if (selectedEtPosition == 2) {

                    selectedEtPosition = 3;
                    showKeyboard(otpEt4);
                }

            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DEL) {

            if (selectedEtPosition == 3) {

                selectedEtPosition = 2;
                showKeyboard(otpEt3);
            } else if (selectedEtPosition == 2) {

                selectedEtPosition = 1;
                showKeyboard(otpEt2);
            } else if (selectedEtPosition == 1) {

                selectedEtPosition = 0;
                showKeyboard(otpEt1);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}