package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    Button buttonLogin;
    EditText inputEmail;
    EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPassword = findViewById(R.id.editTextTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(listener -> {
            AttemptLogin();
        });
    }

    private void AttemptLogin() {
      String email = inputEmail.getText().toString();
      String password = inputPassword.getText().toString();

      if(AuthenticateUser(email, password)) {
        finishAffinity();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.e_mail), email); // TODO add any other extras
        startActivity(intent);
      }
      else {
          Toast.makeText(this, R.string.authentication_fail_msg, Toast.LENGTH_LONG).show();
      }
    }

    private boolean AuthenticateUser(String email, String password) {
        // TODO implement user authentication
        return true;
    }
}