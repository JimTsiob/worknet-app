package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.LiveData.AuthResultLiveData;
import com.syrtsiob.worknet.services.UserService;
import com.syrtsiob.worknet.model.LoginUserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

      authenticateUser(email,password);

      AuthResultLiveData.getInstance().observe(this, isAuthenticated -> {
          if (isAuthenticated) {
              // Handle authentication success
              finishAffinity();
              Intent intent = new Intent(this, MainActivity.class);
              intent.putExtra(getResources().getString(R.string.e_mail), email); // TODO add any other extras
              startActivity(intent);
          } else {
              // Handle authentication failure
              Toast.makeText(Login.this, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show();
          }
      });
    }

    private void authenticateUser(String email, String password) {
        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setEmail(email);
        loginUserDTO.setPassword(password);

        userService.loginUser(loginUserDTO).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    AuthResultLiveData.getInstance().setValue(true);
                } else {
                    AuthResultLiveData.getInstance().setValue(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                AuthResultLiveData.getInstance().setValue(false);
            }
        });
    }
}