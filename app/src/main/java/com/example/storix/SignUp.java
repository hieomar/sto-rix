package com.example.storix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    TextInputLayout regFullName, regUserName, regEmail, regPassword;
    Button registerBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // to hide the status bar
        setContentView(R.layout.activity_sign_up);

        // Hooks
        regFullName = findViewById(R.id.text_input_fullname);
        regUserName = findViewById(R.id.text_input_username);
        regEmail= findViewById(R.id.text_input_email);
        regPassword = findViewById(R.id.text_input_password);
        registerBtn =findViewById(R.id.button_sign_in);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);
            }
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
    private  Boolean validateUserName() {
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
        }

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        // Get all the values
        String fullName = Objects.requireNonNull(regFullName.getEditText()).getText().toString();
        String userName = Objects.requireNonNull(regUserName.getEditText()).getText().toString();
        String email = Objects.requireNonNull(regEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(regPassword.getEditText()).getText().toString();

        // Create a UserHelperClass object
        UserHelperClass helperClass = new UserHelperClass(fullName, userName, email, password);
        reference.child(userName).setValue(helperClass);


    }

}