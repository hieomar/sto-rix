package com.example.storix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class SignIn extends AppCompatActivity {

    ImageView logoImg;
    TextView greetings;
    TextInputLayout userName, password;
    Button forgotPasswordBtn, signInBtn, signUpNewUserBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_in);



        logoImg = findViewById(R.id.logo);
        greetings = findViewById(R.id.text_view_greetings);
        userName = findViewById(R.id.text_input_username);
        password = findViewById(R.id.text_input_password);
        forgotPasswordBtn = findViewById(R.id.button_forgot_password);
        signInBtn = findViewById(R.id.button_sign_in);
        signUpNewUserBtn = findViewById(R.id.button_new_user);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(view);
            }
        });

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

    private Boolean validateUserName() {
        String val = Objects.requireNonNull(userName.getEditText()).getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            userName.setError("Field cannot be empty");
            return false;
        } else {
            userName.setError(null);
            userName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = Objects.requireNonNull(password.getEditText()).getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

    private void loginUser(View view) {
        if (!validateUserName() | !validatePassword()) {
            return;
        }else {
            isUser();
        }

    }

    private void isUser() {
        String userUserName = Objects.requireNonNull(userName.getEditText()).getText().toString().trim();
        String userPassword = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Query checkUser = databaseReference.orderByChild("userName").equalTo(userUserName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userNameFromDB = userSnapshot.child("userName").getValue(String.class);
                    if (userUserName.equals(userNameFromDB)) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (userPassword.equals(passwordFromDB)) {
                            String nameFromDB = userSnapshot.child("fullName").getValue(String.class);
                            userNameFromDB = userSnapshot.child("userName").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);


                            Intent intent = new Intent(getApplicationContext(), UserProfile.class);

                            intent.putExtra("fullName", nameFromDB);
                            intent.putExtra("userName", userNameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("password", passwordFromDB);

                            startActivity(intent);
                        }else {
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                        return;
                    }
                }
                userName.setError("No such User exists");
                userName.requestFocus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}