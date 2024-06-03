package com.syrtsiob.worknet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;

public class StartActivity extends AppCompatActivity {

    Button buttonRegister, buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String email = sharedPreferences.getString("email", null);


        if (jwtToken != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(getString(R.string.e_mail), email);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_start);

            buttonRegister = findViewById(R.id.buttonRegister);
            buttonLogin = findViewById(R.id.buttonLogin);

            SetupButtonListeners();
        }
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