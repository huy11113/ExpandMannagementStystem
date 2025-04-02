package com.example.expandmanagementsystem.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expandmanagementsystem.Adapter.BudgetAdapter;
import com.example.expandmanagementsystem.model.Budget;

import java.util.ArrayList;
import java.util.Calendar;

public class BudgetSettingActivity extends AppCompatActivity {

    private int userId;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    private RecyclerView budgetsRecyclerView;
    private BudgetAdapter adapter;
    private ArrayList<Budget> budgets;
    private double[] spentAmounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setting);

        // Khởi tạo SharedPreferences và lấy userId
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User information not found. Please log in again.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BudgetSettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        // Xử lý nút Back (FloatingActionButton)
        FloatingActionButton backFab = findViewById(R.id.backFab);
        backFab.setOnClickListener(v -> {
            Intent intent = new Intent(BudgetSettingActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Khởi tạo RecyclerView
        budgetsRecyclerView = findViewById(R.id.budgetsRecyclerView);
        budgetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tải danh sách ngân sách
        loadBudgets();

        // Xử lý nút thêm ngân sách
        FloatingActionButton addBudgetButton = findViewById(R.id.addBudgetButton);
        addBudgetButton.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadBudgets() {
        // Lấy danh sách ngân sách từ database
        budgets = dbHelper.getAllBudgets(userId);

        // Tính tổng chi tiêu cho từng ngân sách
        spentAmounts = new double[budgets.size()];
        for (int i = 0; i < budgets.size(); i++) {
            Budget budget = budgets.get(i);
            spentAmounts[i] = dbHelper.getTotalExpenseByCategory(userId, budget.getCategory(), budget.getMonth(), budget.getYear());
        }

        // Cập nhật RecyclerView
        adapter = new BudgetAdapter(budgets, spentAmounts, new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Budget budget) {
                showAddEditDialog(budget);
            }

            @Override
            public void onDeleteClick(Budget budget) {
                new AlertDialog.Builder(BudgetSettingActivity.this)
                        .setTitle("Delete Budget")
                        .setMessage("Are you sure you want to delete this budget?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Xóa ngân sách
                            budgets.remove(budget);
                            loadBudgets(); // Tải lại danh sách
                            Toast.makeText(BudgetSettingActivity.this, "Budget deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        budgetsRecyclerView.setAdapter(adapter);
    }

    private void showAddEditDialog(Budget budget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_budget, null);
        builder.setView(dialogView);

        EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
        EditText monthEditText = dialogView.findViewById(R.id.monthEditText);
        EditText yearEditText = dialogView.findViewById(R.id.yearEditText);
        EditText amountEditText = dialogView.findViewById(R.id.amountEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);

        // Điền dữ liệu nếu là chỉnh sửa
        boolean isEdit = budget != null;
        if (isEdit) {
            dialogTitle.setText("Edit Budget");
            categoryEditText.setText(budget.getCategory());
            monthEditText.setText(String.valueOf(budget.getMonth()));
            yearEditText.setText(String.valueOf(budget.getYear()));
            amountEditText.setText(String.valueOf(budget.getAmount()));
        } else {
            dialogTitle.setText("Add Budget");
            // Đặt giá trị mặc định là tháng và năm hiện tại
            Calendar calendar = Calendar.getInstance();
            monthEditText.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
            yearEditText.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        }

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String category = categoryEditText.getText().toString().trim();
            String monthStr = monthEditText.getText().toString().trim();
            String yearStr = yearEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();

            // Validate input
            if (category.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int month, year;
            double amount;
            try {
                month = Integer.parseInt(monthStr);
                year = Integer.parseInt(yearStr);
                amount = Double.parseDouble(amountStr);

                if (month < 1 || month > 12) {
                    Toast.makeText(this, "Month must be between 1 and 12", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (year < 2000 || year > 2100) {
                    Toast.makeText(this, "Year must be between 2000 and 2100", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount <= 0) {
                    Toast.makeText(this, "Budget amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu ngân sách vào database
            boolean success = dbHelper.addBudget(userId, category, month, year, amount);
            if (success) {
                Toast.makeText(this, isEdit ? "Budget updated" : "Budget added", Toast.LENGTH_SHORT).show();
                loadBudgets(); // Tải lại danh sách
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to save budget", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}