package com.syrtsiob.worknet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ContentResolver;
import android.database.Cursor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.syrtsiob.worknet.LiveData.RegisterResultLiveData;
import com.syrtsiob.worknet.LiveData.UserEmailResultLiveData;
import com.syrtsiob.worknet.interfaces.CustomFileService;
import com.syrtsiob.worknet.interfaces.UserService;
import com.syrtsiob.worknet.model.RegisterUserDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register extends AppCompatActivity {

    Button buttonRegister, pickImage;
    EditText inputEmail, inputPhone, inputPassword, inputRepeatPassword, inputName, inputSurname;
    ImageView selectedImage;
    Bitmap selectedImageBitmap;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(listener -> {
            if (this.imageUri != null){
                AttemptRegister();
            }else{
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_LONG).show();
            }
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
        ActivityResultLauncher<Intent> pickImageActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImageURI = Objects.requireNonNull(data).getData();
                        this.imageUri = selectedImageURI; // used for method
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
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();
        String name = inputName.getText().toString();
        String surname = inputSurname.getText().toString();

        if(!ValidatePasswordRepeat(password, repeatPassword))
            return;

        if(!ValidatePasswordRequirements(password))
            return;

        if(!ValidateEmail(email))
            return;

        if (password.isEmpty()){
            Toast.makeText(Register.this, "password cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        if (phone.isEmpty()){
            Toast.makeText(Register.this, "phone cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        if (name.isEmpty()){
            Toast.makeText(Register.this, "name cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        if (surname.isEmpty()){
            Toast.makeText(Register.this, "surname cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }



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
                getUserByEmailForImageUpload(email);

                UserEmailResultLiveData.getInstance().observe(this, userId -> {
                    // Convert image to MultipartBody.Part and upload
                    try {
                        File file = createTempFileFromUri(this.imageUri);

                        try {
                            ImageDecoder.Source source = ImageDecoder
                                    .createSource(this.getContentResolver(), this.imageUri);
                            selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                            // save image to android folder so we can show it on profile page and home.
                            saveImageToInternalStorage(selectedImageBitmap, userId + "_profile_picture.jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        uploadImage(file, userId); // upload to PC folder.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Intent intent = new Intent(this, Login.class);
                intent.putExtra(getResources().getString(R.string.e_mail), email);
                startActivity(intent);
            } else {
                // Handle register failure
                Toast.makeText(Register.this, "Try entering another email.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserByEmailForImageUpload(String email){
        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        userService.getUserByEmail(email).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    UserEmailResultLiveData.getInstance().setValue(response.body().getId());
                } else {
                    Toast.makeText(Register.this, "user by email failed!", Toast.LENGTH_LONG).show();
                    UserEmailResultLiveData.getInstance().setValue(0L);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(Register.this, "user by email failed! Server failure.", Toast.LENGTH_LONG).show();
                UserEmailResultLiveData.getInstance().setValue(0L);
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

    private boolean ValidateEmail(String email){
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        if (email.matches(emailPattern)){
            return true;
        }

        Toast.makeText(this, "email format is wrong.", Toast.LENGTH_LONG).show();
        return false;
    }

    private File createTempFileFromUri(Uri uri) throws IOException {
        File file = new File(getCacheDir(), "temp_image.jpg");
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
        }
        return file;
    }

    private void uploadImage(File file, Long userId) {

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        CustomFileService customFileService = retrofit.create(CustomFileService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        customFileService.uploadImage(body, userId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("Image success", "image uploaded successfully.");
                } else {
                    Toast.makeText(Register.this, "Image upload failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(Register.this, "Image upload failed! Server failure.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean ValidatePasswordRequirements(String password){
        // TODO implement possible requirements including Toast for failure
        return true;
    }

    private void saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        File directory = new File(getFilesDir(), "FileStorage/images");
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it does not exist
        }
        File imageFile = new File(directory, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            Log.d("SaveImage", "Image saved to internal storage: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SaveImage", "Failed to save image", e);
        }
    }
}