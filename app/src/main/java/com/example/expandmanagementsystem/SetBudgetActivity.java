package com.example.expandmanagementsystem;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SetBudgetActivity extends AppCompatActivity {
    private Spinner categorySpinner, monthSpinner, yearSpinner;
    private EditText amountEditText;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);

        categorySpinner = findViewById(R.id.categorySpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        saveButton = findViewById(R.id.saveButton);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập danh mục
        ArrayList<String> categories = dbHelper.getCategories(userId);
        if (categories.isEmpty()) {
            categories.add("Chưa có danh mục");
            saveButton.setEnabled(false);
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Thiết lập tháng
        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Thiết lập năm
        String[] years = {"2023", "2024", "2025", "2026"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        saveButton.setOnClickListener(v -> saveBudget());
    }

    private void saveBudget() {
        try {
            String category = categorySpinner.getSelectedItem().toString();
            if (category.equals("Chưa có danh mục")) {
                Toast.makeText(this, "Vui lòng thêm chi tiêu trước để tạo danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
            int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
            int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
            double amount = Double.parseDouble(amountEditText.getText().toString());

            if (amount <= 0) {
                Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addBudget(userId, category, month, year, amount);
            Toast.makeText(this, "Đã lưu ngân sách thành công", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}