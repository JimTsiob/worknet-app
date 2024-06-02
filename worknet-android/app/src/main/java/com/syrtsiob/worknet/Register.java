package com.syrtsiob.worknet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Stack;

public class Register extends AppCompatActivity {

    Button buttonRegister, pickImage;
    EditText inputEmail, inputPassword, inputRepeatPassword, inputName, inputSurname;
    ImageView selectedImage;
    Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(listener -> {
            AttemptRegister();
        });

        selectedImage = findViewById(R.id.selectedImage);

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPassword = findViewById(R.id.editTextTextPassword);
        inputRepeatPassword = findViewById(R.id.editTextTextPasswordRepeat);
        inputName = findViewById(R.id.editTextName);
        inputSurname = findViewById(R.id.editTextSurname);


        // =========================================================================================
        // Image picker
        ActivityResultLauncher<Intent> pickImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImageURI = Objects.requireNonNull(data).getData();
                        selectedImage.setImageURI(selectedImageURI);
                        try {
                            ImageDecoder.Source source = ImageDecoder
                                    .createSource(this.getContentResolver(), selectedImageURI);
                            selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        pickImage = findViewById(R.id.pickImage);
        pickImage.setOnClickListener(listener ->{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            pickImageActivity.launch(photoPickerIntent);
        });
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