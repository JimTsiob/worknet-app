package com.syrtsiob.worknet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.syrtsiob.worknet.LiveData.ApplicantUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.ConnectionUserDtoResultLiveData;
import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.databinding.ActivityMainBinding;
import com.syrtsiob.worknet.services.UserService;
import com.syrtsiob.worknet.model.CustomFileDTO;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(HomeFragment.newInstance());

        // TESTING - in case of emergency
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("jwt_token");
//        editor.remove("email");
//        editor.apply();
//        Intent intent = new Intent(MainActivity.this, Login.class);
//        startActivity(intent);
//        finish();



        // do this to show user image on profile
        ConnectionUserDtoResultLiveData.getInstance().setValue(null);
        ApplicantUserDtoResultLiveData.getInstance().setValue(null);

        Intent intent = getIntent();
        String email = intent.getStringExtra(getResources().getString(R.string.e_mail));

        // used for sending user back to messages with the back button
        String fragmentToReplace = intent.getStringExtra("fragment_to_replace");
        if ("message_fragment".equals(fragmentToReplace)) {
            replaceFragment(MessagesFragment.newInstance());
        }

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        userService.getUserByEmail(email).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    UserDtoResultLiveData.getInstance().setValue(response.body());

                    // Logic for logged in user to always go to the main activity instead of login screen
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("jwt_token", response.body().getJwtToken());
                    editor.putString("email", response.body().getEmail());
                    editor.apply();
                } else {
                    Toast.makeText(MainActivity.this, "user by email failed!", Toast.LENGTH_LONG).show();
                    UserDtoResultLiveData.getInstance().setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e("fail: ", t.getLocalizedMessage());
                // Handle the error
                Toast.makeText(MainActivity.this, "user by email failed! Server failure.", Toast.LENGTH_LONG).show();
                UserDtoResultLiveData.getInstance().setValue(null);
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.menuHome)
                replaceFragment(HomeFragment.newInstance());
            else if(itemID == R.id.menuPost)
                replaceFragment(PostFragment.newInstance());
            else if(itemID == R.id.menuJobPostings)
                replaceFragment(JobPostingsFragment.newInstance());
            else if(itemID == R.id.menuNetwork)
                replaceFragment(NetworkFragment.newInstance());
            else if(itemID == R.id.menuNotifications)
                replaceFragment(NotificationsFragment.newInstance());

            return true;
        });

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        UserDtoResultLiveData.getInstance().observe(this, userDto -> {
            profileImage = findViewById(R.id.profileImage);
            if (userDto != null){
                String profilePicName = userDto.getProfilePicture();
                List<CustomFileDTO> files = userDto.getFiles();
                Optional<CustomFileDTO> profilePicture = files.stream()
                        .filter(file -> file.getFileName().equals(profilePicName))
                        .findFirst();
                if (profilePicture.isPresent()){
                    Bitmap bitmap = loadImageFromFile(profilePicture.get());
                    profileImage.setImageBitmap(bitmap);
                    profileImage.setOnClickListener(listener -> {
                        drawerLayout.openDrawer(GravityCompat.START);
                    });
                }else{
                    findViewById(R.id.profileImage).setOnClickListener(listener -> {
                        drawerLayout.openDrawer(GravityCompat.START);
                    });
                }
            }else{
                findViewById(R.id.profileImage).setOnClickListener(listener -> {
                    drawerLayout.openDrawer(GravityCompat.START);
                });
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(item->{
            int itemID = item.getItemId();
            if(itemID == R.id.profile)
                replaceFragment(ProfileFragment.newInstance());
            else if(itemID == R.id.settings)
                replaceFragment(SettingsFragment.newInstance());
            else if (itemID == R.id.logout)
                logout();
            drawerLayout.close();
            return true;
        });

        findViewById(R.id.messages).setOnClickListener(listener -> {
            replaceFragment(MessagesFragment.newInstance());
        });

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                HomeFragment homeFragmentReference = (HomeFragment) getSupportFragmentManager()
                        .findFragmentByTag("HomeFragment");
                String searchText = searchBar.getText().toString();

                if (homeFragmentReference != null && homeFragmentReference.isVisible()) {
                    replaceFragment(SearchResultsFragment
                            .newInstance(SearchResultsFragment.HOME_FRAG_MODE, searchText));
                }
                else {
                    replaceFragment(SearchResultsFragment
                            .newInstance(SearchResultsFragment.DEFAULT_MODE, searchText));
                }
                searchBar.clearFocus();
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }

    public void logout() {

        UserDtoResultLiveData.getInstance().observe(this, userDTO -> {
            if (userDTO != null){

                Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
                UserService userService = retrofit.create(UserService.class);


                userService.logoutUser(userDTO.getEmail()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("jwt_token");
                            editor.remove("email");
                            editor.apply();

                            // Navigate back to the login screen
                            Intent intent = new Intent(MainActivity.this, StartActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "user by email failed!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("fail: ", t.getLocalizedMessage());
                        // Handle the error
                        Toast.makeText(MainActivity.this, "user by email failed! Server failure.", Toast.LENGTH_LONG).show();
                    }
                });

            }else{
                Toast.makeText(MainActivity.this, "user by email failed! Server failure.", Toast.LENGTH_LONG).show();
            }
        });

    }

    // method that returns images from the db.
    private Bitmap loadImageFromFile(CustomFileDTO file) {
        InputStream inputStream = decodeBase64ToInputStream(file.getFileContent());
        return BitmapFactory.decodeStream(inputStream);
    }

    // used to decode the image's base64 string from the db.
    private InputStream decodeBase64ToInputStream(String base64Data) {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        return new ByteArrayInputStream(bytes);
    }
}