package com.example.expandmanagementsystem;

import static android.content.Intent.getIntent;

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

        // Khởi tạo các thành phần giao diện
        categorySpinner = findViewById(R.id.categorySpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        saveButton = findViewById(R.id.saveButton);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        // Kiểm tra userId hợp lệ
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập danh sách danh mục
        ArrayList<String> categories = dbHelper.getCategories(userId);
        if (categories.isEmpty()) {
            categories.add("No categories available");
            saveButton.setEnabled(false); // Vô hiệu hóa nút lưu nếu không có danh mục
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Thiết lập danh sách tháng
        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Thiết lập danh sách năm
        String[] years = {"2023", "2024", "2025", "2026"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Xử lý sự kiện nhấn nút lưu
        saveButton.setOnClickListener(v -> saveBudget());
    }

    // Lưu ngân sách vào cơ sở dữ liệu
    private void saveBudget() {
        try {
            String category = categorySpinner.getSelectedItem().toString();
            if (category.equals("No categories available")) {
                Toast.makeText(this, "Please add expenses first to create categories", Toast.LENGTH_SHORT).show();
                return;
            }
            int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
            int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
            double amount = Double.parseDouble(amountEditText.getText().toString());

            // Kiểm tra số tiền hợp lệ
            if (amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addBudget(userId, category, month, year, amount);
            Toast.makeText(this, "Budget saved successfully", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity sau khi lưu
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }
}