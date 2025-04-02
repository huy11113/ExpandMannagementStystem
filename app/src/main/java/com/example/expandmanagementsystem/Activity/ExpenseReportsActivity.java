package com.example.expandmanagementsystem.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.expandmanagementsystem.CategoryBreakdown;
import com.example.expandmanagementsystem.CategoryBreakdownAdapter;
import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseReportsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;
    private Spinner reportTypeSpinner, monthSpinner, yearSpinner, yearOnlySpinner;
    private LinearLayout monthYearSelection;
    private TextView totalExpenseTextView;
    private RecyclerView categoryBreakdownRecyclerView;
    private CategoryBreakdownAdapter categoryAdapter;
    private ArrayList<CategoryBreakdown> categoryBreakdownList;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_reports);

        // Khởi tạo DatabaseHelper và lấy userId từ Intent
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view từ layout
        reportTypeSpinner = findViewById(R.id.reportTypeSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        yearOnlySpinner = findViewById(R.id.yearOnlySpinner);
        monthYearSelection = findViewById(R.id.monthYearSelection);
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        categoryBreakdownRecyclerView = findViewById(R.id.categoryBreakdownRecyclerView);

        // Thiết lập RecyclerView để hiển thị phân tích chi tiêu theo danh mục
        categoryBreakdownList = new ArrayList<>();
        categoryAdapter = new CategoryBreakdownAdapter(categoryBreakdownList);
        categoryBreakdownRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryBreakdownRecyclerView.setAdapter(categoryAdapter);

        // Thiết lập các Spinner (loại báo cáo, tháng, năm)
        setupSpinners();

        // Lắng nghe sự kiện khi chọn loại báo cáo (Monthly/Yearly)
        reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateReportTypeVisibility(); // Cập nhật giao diện (hiển thị/ẩn Spinner tháng hoặc năm)
                updateReport(); // Cập nhật báo cáo dựa trên loại báo cáo
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lắng nghe sự kiện khi chọn tháng
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateReport(); // Cập nhật báo cáo khi chọn tháng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lắng nghe sự kiện khi chọn năm (cho báo cáo Monthly)
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateReport(); // Cập nhật báo cáo khi chọn năm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Lắng nghe sự kiện khi chọn năm (cho báo cáo Yearly)
        yearOnlySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateReport(); // Cập nhật báo cáo khi chọn năm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Cập nhật báo cáo ngay khi activity được tạo
        updateReport();
    }

    // Thiết lập các Spinner để chọn loại báo cáo, tháng và năm
    private void setupSpinners() {
        // Tạo danh sách loại báo cáo (Monthly, Yearly)
        ArrayList<String> reportTypes = new ArrayList<>();
        reportTypes.add("Monthly");
        reportTypes.add("Yearly");
        ArrayAdapter<String> reportTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reportTypes);
        reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(reportTypeAdapter);

        // Tạo danh sách các tháng (1-12)
        ArrayList<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Tạo danh sách các năm (từ 2020 đến năm hiện tại)
        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2020; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearOnlySpinner.setAdapter(yearAdapter);

        // Đặt giá trị mặc định: tháng và năm hiện tại
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        yearSpinner.setSelection(years.size() - 1);
        yearOnlySpinner.setSelection(years.size() - 1);
    }

    // Cập nhật giao diện hiển thị/ẩn các Spinner dựa trên loại báo cáo
    private void updateReportTypeVisibility() {
        String reportType = reportTypeSpinner.getSelectedItem().toString();
        // Nếu chọn báo cáo Monthly, hiển thị Spinner tháng và năm, ẩn Spinner chỉ chọn năm
        if (reportType.equals("Monthly")) {
            monthYearSelection.setVisibility(View.VISIBLE);
            yearOnlySpinner.setVisibility(View.GONE);
        } else {
            // Nếu chọn báo cáo Yearly, ẩn Spinner tháng và năm, hiển thị Spinner chỉ chọn năm
            monthYearSelection.setVisibility(View.GONE);
            yearOnlySpinner.setVisibility(View.VISIBLE);
        }
    }

    // Cập nhật báo cáo chi tiêu dựa trên loại báo cáo (Monthly/Yearly)
    private void updateReport() {
        String reportType = reportTypeSpinner.getSelectedItem().toString();
        double totalExpense = 0.0;
        categoryBreakdownList.clear(); // Xóa danh sách phân tích danh mục cũ

        // Xử lý báo cáo theo loại
        if (reportType.equals("Monthly")) {
            // Lấy tháng và năm từ Spinner
            int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
            int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
            // Tính tổng chi tiêu trong tháng
            totalExpense = dbHelper.getTotalExpenseByMonth(userId, month, year);

            // Lấy danh sách danh mục và tính chi tiêu cho từng danh mục
            ArrayList<String> categories = dbHelper.getCategories(userId);
            for (String category : categories) {
                double amount = dbHelper.getTotalExpenseByCategory(userId, category, month, year);
                if (amount > 0) {
                    categoryBreakdownList.add(new CategoryBreakdown(category, amount));
                }
            }
        } else {
            // Báo cáo Yearly: Lấy năm từ Spinner
            int year = Integer.parseInt(yearOnlySpinner.getSelectedItem().toString());
            // Tính tổng chi tiêu trong năm bằng cách cộng chi tiêu hàng tháng
            ArrayList<Double> monthlyExpenses = dbHelper.getMonthlyExpenses(userId, year);
            for (Double amount : monthlyExpenses) {
                totalExpense += amount;
            }

            // Tính chi tiêu theo danh mục trong cả năm
            ArrayList<String> categories = dbHelper.getCategories(userId);
            for (String category : categories) {
                double amount = 0.0;
                // Cộng chi tiêu của danh mục này trong 12 tháng
                for (int month = 1; month <= 12; month++) {
                    amount += dbHelper.getTotalExpenseByCategory(userId, category, month, year);
                }
                if (amount > 0) {
                    categoryBreakdownList.add(new CategoryBreakdown(category, amount));
                }
            }
        }

        // Hiển thị tổng chi tiêu
        totalExpenseTextView.setText("Total: $" + currencyFormat.format(totalExpense));
        // Hiển thị thông báo nếu không có chi tiêu
        if (categoryBreakdownList.isEmpty()) {
            Toast.makeText(this, "No expenses found for this period", Toast.LENGTH_SHORT).show();
        }
        // Cập nhật RecyclerView
        categoryAdapter.notifyDataSetChanged();

        // Xử lý sự kiện khi nhấn nút Back (FloatingActionButton)
        FloatingActionButton backFab = findViewById(R.id.backFab);
        backFab.setOnClickListener(v -> {
            // Chuyển về MenuActivity và xóa các activity khác trong stack
            Intent intent = new Intent(ExpenseReportsActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Hiệu ứng chuyển cảnh
            finish();
        });
    }
}