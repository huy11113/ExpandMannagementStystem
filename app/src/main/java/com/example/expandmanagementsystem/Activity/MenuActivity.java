package com.example.expandmanagementsystem.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.expandmanagementsystem.R;

public class MenuActivity extends AppCompatActivity {

    private int userId;
    private String username;
    private String role;
    private SharedPreferences sharedPreferences;
    private LinearLayout budgetSubmenu;
    private boolean isBudgetSubmenuVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Lấy thông tin người dùng từ SharedPreferences
        userId = sharedPreferences.getInt("userId", -1);
        username = sharedPreferences.getString("username", null);
        role = sharedPreferences.getString("role", null);

        // Kiểm tra thông tin người dùng
        if (userId == -1 || username == null || role == null) {
            handleInvalidUser();
            return;
        }

        // Kiểm tra vai trò (chỉ cho phép "student" truy cập MenuActivity)
        if (!role.equals("student")) {
            Toast.makeText(this, "Access denied. This page is for students only.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Cập nhật tiêu đề chào mừng
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome " + username + "!");

        // Khởi tạo các CardView
        CardView expenseCard = findViewById(R.id.expenseCard);
        CardView budgetCard = findViewById(R.id.budgetCard);
        CardView recurringExpenseCard = findViewById(R.id.recurringExpenseCard);
        CardView logoutCard = findViewById(R.id.logoutCard);

        // Khởi tạo các CardView trong submenu
        budgetSubmenu = findViewById(R.id.budgetSubmenu);
        CardView budgetSettingCard = findViewById(R.id.budgetSettingCard);
        CardView expenseOverviewCard = findViewById(R.id.expenseOverviewCard);
        CardView expenseReportsCard = findViewById(R.id.expenseReportsCard);

        // Xử lý sự kiện nhấn cho các CardView chính
        expenseCard.setOnClickListener(v -> navigateToActivity(StudentActivity.class));
        recurringExpenseCard.setOnClickListener(v -> navigateToActivity(RecurringExpensesActivity.class));
        logoutCard.setOnClickListener(v -> logout());

        // Xử lý sự kiện nhấn cho Manage Budget (hiển thị/ẩn submenu)
        budgetCard.setOnClickListener(v -> {
            isBudgetSubmenuVisible = !isBudgetSubmenuVisible;
            budgetSubmenu.setVisibility(isBudgetSubmenuVisible ? View.VISIBLE : View.GONE);
        });

        // Xử lý sự kiện nhấn cho các mục con trong submenu
        budgetSettingCard.setOnClickListener(v -> navigateToActivity(BudgetSettingActivity.class));
        expenseOverviewCard.setOnClickListener(v -> navigateToActivity(ExpenseOverviewActivity.class));
        expenseReportsCard.setOnClickListener(v -> navigateToActivity(ExpenseReportsActivity.class));
    }

    // Phương thức điều hướng chung
    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(MenuActivity.this, activityClass);
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Phương thức xử lý đăng xuất
    private void logout() {
        // Xóa thông tin người dùng khỏi SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    // Phương thức xử lý khi thông tin người dùng không hợp lệ
    private void handleInvalidUser() {
        Toast.makeText(this, "User information not found. Please log in again.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}