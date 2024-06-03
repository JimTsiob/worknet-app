package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.syrtsiob.worknet.LiveData.UserDtoResultLiveData;
import com.syrtsiob.worknet.databinding.ActivityMainBinding;
import com.syrtsiob.worknet.interfaces.UserService;
import com.syrtsiob.worknet.model.UserDTO;
import com.syrtsiob.worknet.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(HomeFragment.newInstance());

        Intent intent = getIntent();
        String email = intent.getStringExtra(getResources().getString(R.string.e_mail));

        Retrofit retrofit = RetrofitService.getRetrofitInstance(this);
        UserService userService = retrofit.create(UserService.class);

        userService.getUserByEmail(email).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    UserDtoResultLiveData.getInstance().setValue(response.body());
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
        findViewById(R.id.profileImage).setOnClickListener(listener -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.navigationView.setNavigationItemSelectedListener(item->{
            int itemID = item.getItemId();
            if(itemID == R.id.profile)
                replaceFragment(ProfileFragment.newInstance());
            else if(itemID == R.id.settings)
                replaceFragment(SettingsFragment.newInstance());
            drawerLayout.close();
            return true;
        });

        findViewById(R.id.messages).setOnClickListener(listener -> {
            replaceFragment(MessagesFragment.newInstance());
        });

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                replaceFragment(SearchResultsFragment.newInstance());
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
}