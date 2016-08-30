package com.careons.app.Doctor.Activity.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.careons.app.Doctor.Activity.Main.MainDoctorActivity;
import com.careons.app.R;


public class DoctorLoginActivity extends AppCompatActivity {

    private EditText emailEditTextView, passwordEditTextView;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);


        // Initialize variables
        emailEditTextView = (EditText) findViewById(R.id.email);
        passwordEditTextView = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.email_sign_in_button);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emailEditTextView.getText().toString().equalsIgnoreCase("admin")
                        && passwordEditTextView.getText().toString().equalsIgnoreCase("12345")) {

                    Intent intent = new Intent(DoctorLoginActivity.this, MainDoctorActivity.class);
                    startActivity(intent);

                    finish();

                } else {

                    Toast.makeText(DoctorLoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

