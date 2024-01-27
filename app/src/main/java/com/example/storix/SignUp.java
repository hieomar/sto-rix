package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_up);

        TextInputLayout regFullName, regUserName, regEmail, regPassword;
        Button registerBtn;

        regFullName = findViewById(R.id.text_input_fullname);
        regUserName = findViewById(R.id.text_input_username);
        regEmail= findViewById(R.id.text_input_email);
        regPassword = findViewById(R.id.text_input_password);
        registerBtn =findViewById(R.id.button_sign_in);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = Objects.requireNonNull(regFullName.getEditText()).getText().toString();
                String userName = Objects.requireNonNull(regUserName.getEditText()).getText().toString();
                String email = Objects.requireNonNull(regEmail.getEditText()).getText().toString();
                String password = Objects.requireNonNull(regPassword.getEditText()).getText().toString();

            }
        });
    }
}