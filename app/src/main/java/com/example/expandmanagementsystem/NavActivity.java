package com.example.expandmanagementsystem;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.expandmanagementsystem.Fragment.BudgetFragment;
import com.example.expandmanagementsystem.Fragment.ExpenseFragment;
import com.example.expandmanagementsystem.Fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load Fragment mặc định (HomeFragment)
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_expense:
                    selectedFragment = new ExpenseFragment();
                    break;
                case R.id.nav_budget:
                    selectedFragment = new BudgetFragment();
                    break;
                case R.id.nav_setting:
                    selectedFragment = new SettingFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();
            return true;
        });
    }
}