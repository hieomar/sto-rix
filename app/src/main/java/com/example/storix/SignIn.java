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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseAuth mAuth;

    ImageView logoImg;
    TextView greetings;
    TextInputLayout userName, password;
    Button forgotPasswordBtn, signInBtn, signUpNewUserBtn;
    LoadDialog loadDialog;
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), LandingMain.class);
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logoImg = findViewById(R.id.logo);
        greetings = findViewById(R.id.text_view_greetings);
        userName = findViewById(R.id.text_input_username);
        password = findViewById(R.id.text_input_password);
        forgotPasswordBtn = findViewById(R.id.button_forgot_password);
        signInBtn = findViewById(R.id.button_sign_in);
        signUpNewUserBtn = findViewById(R.id.button_new_user);

        loadDialog = new LoadDialog(SignIn.this);

      signInBtn.setOnClickListener(this::signInUser);


        signUpNewUserBtn.setOnClickListener(view -> {

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

    private void signInUser(View view) {
        if (!validateUserName() | !validatePassword()) {
            return;
        } else {
            // Start the loading dialog here
            loadDialog.startLoadingDialog();
            isUser();
        }
    }

    private void isUser() {
        String userEnteredUsername = Objects.requireNonNull(userName.getEditText()).getText().toString().trim();
        String userEnteredPassword = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.child("userName").getValue(String.class), userEnteredUsername)) {
                        String emailFromDB = snapshot.child("email").getValue(String.class);

                        // Sign in with email and password
                        assert emailFromDB != null;
                        mAuth.signInWithEmailAndPassword(emailFromDB, userEnteredPassword)
                                .addOnCompleteListener(SignIn.this, task -> {
                                    // Dismiss the loading dialog here
                                    loadDialog.dismissDialog();
                                    if (task.isSuccessful()) {
                                        // Sign in success
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        assert user != null;
                                        if (user.isEmailVerified()) {
                                            Intent intent = new Intent(getApplicationContext(), LandingMain.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(SignIn.this, "Please verify your email address.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // If sign in fails, handle the exception
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(SignIn.this, "Invalid credentials. Please check your username and password.",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Log the error.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                            // Display a message to the user.
                                            Toast.makeText(SignIn.this, "LogIn failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        return;
                    }
                }
                // Dismiss the loading dialog here
                loadDialog.dismissDialog();
                Toast.makeText(SignIn.this, "No such user exist.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error

                // Dismiss the loading dialog here
                loadDialog.dismissDialog();
            }
        });
    }
}