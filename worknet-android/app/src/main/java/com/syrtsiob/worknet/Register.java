package com.syrtsiob.worknet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.syrtsiob.worknet.LiveData.AuthResultLiveData;
import com.syrtsiob.worknet.LiveData.RegisterResultLiveData;
import com.syrtsiob.worknet.interfaces.UserService;
import com.syrtsiob.worknet.model.RegisterUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register extends AppCompatActivity {

    Button buttonRegister, pickImage;
    EditText inputEmail, inputPhone, inputPassword, inputRepeatPassword, inputName, inputSurname;
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
        inputPhone = findViewById(R.id.editTextTextPhone);
        inputPassword = findViewById(R.id.editTextTextPassword);
        inputRepeatPassword = findViewById(R.id.editTextTextPasswordRepeat);
        inputName = findViewById(R.id.editTextName);
        inputSurname = findViewById(R.id.editTextSurname);


        // =========================================================================================
        // Image picker
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
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
            someActivityResultLauncher.launch(photoPickerIntent);
        });
    }

    private void AttemptRegister() {
        String email = inputEmail.getText().toString();
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();
        String name = inputName.getText().toString();
        String surname = inputSurname.getText().toString();

        if(!ValidatePasswordRepeat(password, repeatPassword))
            return;

        if(!ValidatePasswordRequirements(password))
            return;

        RegisterUserDTO user = new RegisterUserDTO();
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(password);
        user.setFirstName(name);
        user.setLastName(surname);

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        userService.registerUser(user).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Register.this, "Register successful!", Toast.LENGTH_LONG).show();
                    RegisterResultLiveData.getInstance().setValue(true);
                } else {
                    Toast.makeText(Register.this, "Register failed! your email exists in the database.", Toast.LENGTH_LONG).show();
                    RegisterResultLiveData.getInstance().setValue(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(Register.this, "Register failed! Server failure.", Toast.LENGTH_LONG).show();
                RegisterResultLiveData.getInstance().setValue(false);
            }
        });

        RegisterResultLiveData.getInstance().observe(this, isRegistered -> {
            if (isRegistered) {
                // Handle register success
                Intent intent = new Intent(this, Login.class);
                intent.putExtra(getResources().getString(R.string.e_mail), email); // TODO add any other extras
                startActivity(intent);
            } else {
                // Handle register failure
                Toast.makeText(Register.this, "Try entering another email.", Toast.LENGTH_LONG).show();
            }
        });
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