package com.example.expandmanagementsystem.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.Database.DatabaseHelper;
import com.example.expandmanagementsystem.R;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    private EditText etDescription, etAmount, etDate;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnEditExpense;
    private ListView lvExpenses;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<Expense> expenseAdapter;
    private ArrayList<Expense> expenseList;
    private Button btnDeleteExpense;
    private int userId;
    private Expense selectedExpense = null; // Lưu mục chi tiêu được chọn để chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Khởi tạo giao diện
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnEditExpense = findViewById(R.id.btnEditExpense);
        lvExpenses = findViewById(R.id.lvExpenses);
        dbHelper = new DatabaseHelper(this);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);

        // Lấy userId từ Intent
        String username = getIntent().getStringExtra("username");
        userId = dbHelper.getUserId(username);
        if (userId == -1) {
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập Spinner với danh sách danh mục mẫu
        String[] categories = {"Rent", "Groceries", "Transportation", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);



        // Thiết lập danh sách chi tiêu
        expenseList = dbHelper.getExpenses(userId);
        expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        lvExpenses.setAdapter(expenseAdapter);


        // Xử lý chọn mục trong ListView
        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedExpense = expenseList.get(position);
                etDescription.setText(selectedExpense.getDescription());
                etAmount.setText(String.valueOf(selectedExpense.getAmount()));
                etDate.setText(selectedExpense.getDate());
                spinnerCategory.setSelection(((ArrayAdapter<String>) spinnerCategory.getAdapter())
                        .getPosition(selectedExpense.getCategory()));
                btnEditExpense.setEnabled(true); // Bật nút chỉnh sửa
            }
        });


        // Xử lý nút Add Expense
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString().trim();
                String amountStr = etAmount.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                    Toast.makeText(StudentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(StudentActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isAdded = dbHelper.addExpense(userId, description, amount, date, category);
                if (isAdded) {
                    Toast.makeText(StudentActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                    clearFields();
                    refreshExpenseList();
                } else {
                    Toast.makeText(StudentActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //delete
        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedExpense = expenseList.get(position);
                etDescription.setText(selectedExpense.getDescription());
                etAmount.setText(String.valueOf(selectedExpense.getAmount()));
                etDate.setText(selectedExpense.getDate());
                spinnerCategory.setSelection(((ArrayAdapter<String>) spinnerCategory.getAdapter())
                        .getPosition(selectedExpense.getCategory()));
                btnEditExpense.setEnabled(true);
                btnDeleteExpense.setEnabled(true); // Bật nút xóa
            }
        });

        btnDeleteExpense.setOnClickListener(v -> {
            if (selectedExpense == null) {
                Toast.makeText(StudentActivity.this, "Please select an expense to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isDeleted = dbHelper.deleteExpense(selectedExpense.getId());
            if (isDeleted) {
                Toast.makeText(StudentActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                clearFields();
                refreshExpenseList();
                btnEditExpense.setEnabled(false);
                btnDeleteExpense.setEnabled(false);
                selectedExpense = null;
            } else {
                Toast.makeText(StudentActivity.this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteExpense.setOnClickListener(v -> {
            if (selectedExpense == null) {
                Toast.makeText(StudentActivity.this, "Please select an expense to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isDeleted = dbHelper.deleteExpense(selectedExpense.getId());
            if (isDeleted) {
                Toast.makeText(StudentActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                clearFields();
                refreshExpenseList();
                btnEditExpense.setEnabled(false);
                btnDeleteExpense.setEnabled(false);
                selectedExpense = null;
            } else {
                Toast.makeText(StudentActivity.this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        });


        //Xử lý nút Edit Expense
        btnEditExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedExpense == null) {
                    Toast.makeText(StudentActivity.this, "Please select an expense to edit", Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = etDescription.getText().toString().trim();
                String amountStr = etAmount.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
                    Toast.makeText(StudentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(StudentActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isUpdated = dbHelper.updateExpense(selectedExpense.getId(), description, amount, date, category);
                if (isUpdated) {
                    Toast.makeText(StudentActivity.this, "Expense updated", Toast.LENGTH_SHORT).show();
                    clearFields();
                    refreshExpenseList();
                    btnEditExpense.setEnabled(false); // Tắt nút chỉnh sửa sau khi hoàn tất
                    selectedExpense = null;
                } else {
                    Toast.makeText(StudentActivity.this, "Failed to update expense", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    // Xóa các trường nhập liệu
    private void clearFields() {
        etDescription.setText("");
        etAmount.setText("");
        etDate.setText("");
        spinnerCategory.setSelection(0);
    }

    // Làm mới danh sách chi tiêu
    private void refreshExpenseList() {
        expenseList.clear();
        expenseList.addAll(dbHelper.getExpenses(userId));
        expenseAdapter.notifyDataSetChanged();
    }


}

