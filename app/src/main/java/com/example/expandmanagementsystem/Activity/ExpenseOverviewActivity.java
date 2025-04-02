package com.example.expandmanagementsystem.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.Database.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ExpenseOverviewActivity extends AppCompatActivity {
    private Spinner monthSpinner, yearSpinner;
    private Button loadOverviewButton;
    private TextView totalExpenseTextView, remainingBudgetTextView;
    private ListView categoryBreakdownListView;
    private LineChart expenseTrendChart;
    private DatabaseHelper dbHelper;
    private ArrayList<String> breakdownList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_overview);

        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        loadOverviewButton = findViewById(R.id.loadOverviewButton);
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        remainingBudgetTextView = findViewById(R.id.remainingBudgetTextView);
        categoryBreakdownListView = findViewById(R.id.categoryBreakdownListView);
        expenseTrendChart = findViewById(R.id.expenseTrendChart);

        dbHelper = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        String[] years = {"2023", "2024", "2025", "2026"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        loadOverviewButton.setOnClickListener(v -> showOverview());
    }

    private void showOverview() {
        int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        double totalExpense = dbHelper.getTotalExpenseByMonth(userId, month, year);
        totalExpenseTextView.setText("Total Expense: $" + String.format("%.2f", totalExpense));

        breakdownList = new ArrayList<>();
        ArrayList<String> categories = dbHelper.getCategories(userId);
        double totalBudget = 0.0;
        double totalSpent = 0.0;
        for (String category : categories) {
            double budget = dbHelper.getBudget(userId, category, month, year);
            double spent = dbHelper.getTotalExpenseByCategory(userId, category, month, year);
            totalBudget += budget;
            totalSpent += spent;
            breakdownList.add(category + ": Spent $" + String.format("%.2f", spent) + " / Budget $" + String.format("%.2f", budget));
        }

        double remainingBudget = totalBudget - totalSpent;
        remainingBudgetTextView.setText("Remaining Budget: $" + String.format("%.2f", remainingBudget));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, breakdownList);
        categoryBreakdownListView.setAdapter(adapter);

        showExpenseTrend(year);
    }

    private void showExpenseTrend(int year) {
        ArrayList<Double> monthlyExpenses = dbHelper.getMonthlyExpenses(userId, year);
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < monthlyExpenses.size(); i++) {
            entries.add(new Entry(i + 1, monthlyExpenses.get(i).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Monthly Expenses");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        LineData lineData = new LineData(dataSet);
        expenseTrendChart.setData(lineData);
        expenseTrendChart.invalidate();
    }
}