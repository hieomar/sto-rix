package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_in);

        ImageView logoImg;
        TextView greetings;
        TextInputLayout userName, password;
        Button forgotPasswordBtn, signInBtn, signUpNewUserBtn ;

        logoImg = findViewById(R.id.logo);
        greetings = findViewById(R.id.text_view_greetings);
        userName = findViewById(R.id.text_input_username);
        password =findViewById(R.id.text_input_password);
        forgotPasswordBtn = findViewById(R.id.button_forgot_password);
        signInBtn= findViewById(R.id.button_sign_in);
        signUpNewUserBtn = findViewById(R.id.button_new_user);

        signUpNewUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Pair<View, String>> pairs = new ArrayList<>();
                pairs.add(new Pair<View, String>(logoImg, "logo_img"));
                pairs.add(new Pair<View, String>(greetings, "Sto_rif"));
                pairs.add(new Pair<View, String>(userName, "Username"));
                pairs.add(new Pair<View, String>(password, "Password"));
                pairs.add(new Pair<View, String>(forgotPasswordBtn, "Forgot Password"));
                pairs.add(new Pair<View, String>(signInBtn, "Sign In"));
                pairs.add(new Pair<View, String>(signUpNewUserBtn, "New User? Sign Up"));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs.toArray(new Pair[pairs.size()]));

                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent, options.toBundle());
            }
        });
    }
}