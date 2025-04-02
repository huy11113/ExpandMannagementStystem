package com.example.expandmanagementsystem.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.Database.DatabaseHelper;
import com.example.expandmanagementsystem.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseReportActivity extends AppCompatActivity {
    private EditText startDateEditText, endDateEditText;
    private Button generateReportButton;
    private ListView reportListView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> reportList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        generateReportButton = findViewById(R.id.generateReportButton);
        reportListView = findViewById(R.id.reportListView);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        generateReportButton.setOnClickListener(v -> generateReport());
    }

    private void generateReport() {
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ngày bắt đầu và ngày kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap<String, String>> expenses = dbHelper.getExpensesByDateRange(userId, startDate, endDate);
        reportList = new ArrayList<>();

        HashMap<String, Double> categoryTotals = new HashMap<>();
        for (HashMap<String, String> expense : expenses) {
            String category = expense.get("category");
            double amount = Double.parseDouble(expense.get("amount"));
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        reportList.add("Phân tích Theo Danh mục:");
        for (String category : categoryTotals.keySet()) {
            reportList.add(category + ": $" + String.format("%.2f", categoryTotals.get(category)));
        }

        reportList.add("\nChi tiết Chi tiêu:");
        for (HashMap<String, String> expense : expenses) {
            reportList.add(expense.get("description") + " - $" + expense.get("amount") + " (" + expense.get("date") + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportList);
        reportListView.setAdapter(adapter);
    }
}