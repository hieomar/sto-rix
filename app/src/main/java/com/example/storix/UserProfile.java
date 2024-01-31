package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    TextView fullNameLabel, userNameLabel;
    TextInputLayout fullName, userName, userEmail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Hooks
        fullNameLabel = findViewById(R.id.user_name);
        userNameLabel = findViewById(R.id.user_full_name);
        fullName = findViewById(R.id.text_input_fullname);
        userName = findViewById(R.id.text_input_username);
        userEmail = findViewById(R.id.text_input_email);
        password = findViewById(R.id.text_input_password);

        // ShowAllData
        showAllUserData();

    }

    private void showAllUserData() {
        Intent intent = getIntent();

        String user_username = intent.getStringExtra("fullName");
        String user_fullname = intent.getStringExtra("userName");
        String user_email = intent.getStringExtra("email");
        String user_password = intent.getStringExtra("password");

        fullNameLabel.setText(user_fullname);
        userNameLabel.setText(user_username);
        Objects.requireNonNull(fullName.getEditText()).setText(user_fullname);
        Objects.requireNonNull(userName.getEditText()).setText(user_username);
        Objects.requireNonNull(userEmail.getEditText()).setText(user_email);
        Objects.requireNonNull(password.getEditText()).setText(user_password);
    }
}