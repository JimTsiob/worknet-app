package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    Button buttonRegister, buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);

        SetupButtonListeners();
    }

    void SetupButtonListeners() {
        buttonRegister.setOnClickListener(listener -> {
            startActivity(new Intent(this, Register.class));
        });

        buttonLogin.setOnClickListener(listener -> {
            startActivity(new Intent(this, Login.class));
        });
    }
}