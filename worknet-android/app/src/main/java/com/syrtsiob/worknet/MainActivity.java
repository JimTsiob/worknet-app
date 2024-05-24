package com.syrtsiob.worknet;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.syrtsiob.worknet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

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

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        findViewById(R.id.profileImage).setOnClickListener(listener -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.navigationView.setNavigationItemSelectedListener(item->{
            int itemID = item.getItemId();
            if(itemID == R.id.profile)
                replaceFragment(new ProfileFragment());
            else if(itemID == R.id.settings)
                replaceFragment(new SettingsFragment());
            drawerLayout.close();
            return true;
        });

        findViewById(R.id.messages).setOnClickListener(listener -> {
            replaceFragment(new MessagesFragment());
            clearBottomMenuSelection();
        });

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                replaceFragment(new SearchResultsFragment());
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

    private void clearBottomMenuSelection(){
        Menu menu = binding.bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }
}