package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {

    Button buttonRegister;
    EditText inputEmail, inputPassword, inputRepeatPassword, inputName, inputSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(listener -> {
            AttemptRegister();
        });

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPassword = findViewById(R.id.editTextTextPassword);
        inputRepeatPassword = findViewById(R.id.editTextTextPasswordRepeat);
        inputName = findViewById(R.id.editTextName);
        inputSurname = findViewById(R.id.editTextSurname);
    }

    private void AttemptRegister() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();
        String name = inputName.getText().toString();
        String surname = inputSurname.getText().toString();

        if(!ValidatePasswordRepeat(password, repeatPassword))
            return;

        if(!ValidatePasswordRequirements(password))
            return;

        // TODO database call - if email already exists -> error, else register

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.e_mail), email); // TODO add any other extras
        startActivity(intent);
    }

    private boolean ValidatePasswordRepeat(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)){
            Toast.makeText(this, R.string.repeat_pass_validation_error,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean ValidatePasswordRequirements(String password){
        // TODO implement possible requirements including Toast for failure
        return true;
    }

}