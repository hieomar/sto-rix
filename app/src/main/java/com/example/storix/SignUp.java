package com.example.storix;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    TextInputLayout regFullName, regUserName, regEmail, regPassword;
    Button registerBtn, signInBtn;

    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    LoadDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);
        // Firebase
        database = FirebaseDatabase.getInstance("https://sto-rix-default-rtdb.firebaseio.com/");
        reference = database.getReference("Users");

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_up);

        // Hooks
        regFullName = findViewById(R.id.text_input_fullname);
        regUserName = findViewById(R.id.text_input_username);
        regEmail = findViewById(R.id.text_input_email);
        regPassword = findViewById(R.id.text_input_password);
        registerBtn = findViewById(R.id.button_sign_up);
        signInBtn = findViewById(R.id.button_new_user);

        loadDialog = new LoadDialog(SignUp.this);

        registerBtn.setOnClickListener(this::registerUser);

        signInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });

    }

    private Boolean validateFullName() {
        String val = Objects.requireNonNull(regFullName.getEditText()).getText().toString();

        if (val.isEmpty()) {
            regFullName.setError("Field cannot be empty");
            return false;
        } else {
            regFullName.setError(null);
            regFullName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUserName() {
        String val = Objects.requireNonNull(regUserName.getEditText()).getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regUserName.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            regUserName.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regUserName.setError("White spaces are not allowed");
            return false;
        } else {
            regUserName.setError(null);
            regUserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = Objects.requireNonNull(regEmail.getEditText()).getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = Objects.requireNonNull(regPassword.getEditText()).getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" + // any letter
                "(?=.*[@#$%^&+=])" + // at least 1 special character
                "(?=\\S+$)" + // no white spaces
                ".{4,}" + // at least 4 characters
                "$";

        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }

    }

    public void registerUser(View view) {
        if (!validateFullName() | !validateUserName() | !validateEmail() | !validatePassword()) {
            return;
        } else {
            // Start the loading dialog here
            loadDialog.startLoadingDialog();
            signUpUser();
        }
    }

    private void signUpUser() {
        SignUpClass signUpClass = new SignUpClass(Objects.requireNonNull(regFullName.getEditText()).getText().toString(),
                Objects.requireNonNull(regUserName.getEditText()).getText().toString(), Objects.requireNonNull(regEmail.getEditText()).getText().toString(),
                Objects.requireNonNull(regPassword.getEditText()).getText().toString());

        mAuth.createUserWithEmailAndPassword(signUpClass.getEmail(), signUpClass.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                user.sendEmailVerification().addOnCompleteListener(sendEmail -> {
                    if (sendEmail.isSuccessful()) {
                        Toast.makeText(SignUp.this, "Account created. Please verify your email", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the exception here
                        if (sendEmail.getException() instanceof FirebaseAuthException) {
                            FirebaseAuthException e = (FirebaseAuthException)sendEmail.getException();
                            Toast.makeText(SignUp.this, "Failed to send verification email. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUp.this, "Error: " + Objects.requireNonNull(sendEmail.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // save all the data in Firebase
                reference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(signUpClass);

                // Dismiss the loading dialog here
                loadDialog.dismissDialog();

                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            } else {
                // Dismiss the loading dialog here
                loadDialog.dismissDialog();
                Toast.makeText(SignUp.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}