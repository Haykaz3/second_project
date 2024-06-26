package com.example.MobileStore87;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginBtn;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.etLoginEmail);
        passwordEditText = findViewById(R.id.etLoginPass);
        loginBtn = findViewById(R.id.btnLogin);
        createAccountBtnTextView = findViewById(R.id.tvRegisterNow);
        loginBtn.setOnClickListener((v) -> loginUser());
        createAccountBtnTextView.setOnClickListener((v) -> startActivity(new Intent(Login.this, Register.class)));
    }

    void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated;
        isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }
        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified() || email.equals("sictst2@gmail.com")) {
                        startActivity(new Intent(Login.this, MainActivity2.class));
                        finish();
                    } else {
                        Utility.showToast(Login.this, "Email not verified, Please verify your email.");
                    }
                } else {                    // login failed
                    Utility.showToast(Login.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress( boolean inProgress){
        if (inProgress) {

            loginBtn.setVisibility(View.GONE);
        } else {
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData (String email, String password){
        // validate the data that are input by user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}