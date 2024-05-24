package com.syrtsiob.worknet;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.syrtsiob.worknet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.menuHome)
                replaceFragment(new HomeFragment());
            else if(itemID == R.id.menuPost)
                replaceFragment(new PostFragment());
            else if(itemID == R.id.menuJobPostings)
                replaceFragment(new JobPostingsFragment());
            else if(itemID == R.id.menuNetwork)
                replaceFragment(new NetworkFragment());
            else if(itemID == R.id.menuNotifications)
                replaceFragment(new NotificationsFragment());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}