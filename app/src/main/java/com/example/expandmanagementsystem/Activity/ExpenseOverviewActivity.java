package com.example.expandmanagementsystem.Activity;

// Import các thư viện cần thiết
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expandmanagementsystem.model.Budget;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Class ExpenseOverviewActivity để hiển thị tổng quan chi tiêu
public class ExpenseOverviewActivity extends AppCompatActivity {

    // Khai báo các biến instance
    private DatabaseHelper dbHelper; // Đối tượng để tương tác với database
    private int userId; // ID của người dùng, lấy từ Intent
    private Spinner monthSpinner, yearSpinner; // Spinner để chọn tháng và năm
    private TextView totalExpenseTextView, remainingBudgetTextView; // TextView hiển thị tổng chi tiêu và ngân sách còn lại
    private RecyclerView categoryBreakdownRecyclerView; // RecyclerView hiển thị phân tích chi tiêu theo danh mục
    private LineChart expenseTrendChart; // Biểu đồ hiển thị xu hướng chi tiêu
    private CategoryBreakdownAdapter categoryAdapter; // Adapter cho RecyclerView phân tích danh mục
    private ArrayList<CategoryBreakdown> categoryBreakdownList; // Danh sách dữ liệu phân tích theo danh mục
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00"); // Định dạng tiền tệ (ví dụ: 1,234.56)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_overview); // Gán layout cho activity

        // Khởi tạo DatabaseHelper để làm việc với database
        dbHelper = new DatabaseHelper(this);

        // Lấy userId từ Intent, nếu không có thì thoát activity
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view và thiết lập các thành phần giao diện
        initializeViews();
        setupRecyclerView();
        setupSpinners();
        setupChart();

        // Xử lý sự kiện khi nhấn nút Back (FloatingActionButton)
        FloatingActionButton backFab = findViewById(R.id.backFab);
        backFab.setOnClickListener(v -> {
            // Chuyển về MenuActivity và xóa các activity khác trong stack
            Intent intent = new Intent(ExpenseOverviewActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Hiệu ứng chuyển cảnh
            finish();
        });

        // Lắng nghe sự kiện khi người dùng chọn tháng từ Spinner
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateOverview(); // Cập nhật tổng quan khi chọn tháng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không có lựa chọn
            }
        });

        // Lắng nghe sự kiện khi người dùng chọn năm từ Spinner
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateOverview(); // Cập nhật tổng quan khi chọn năm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không có lựa chọn
            }
        });

        // Cập nhật tổng quan ngay khi activity được tạo
        updateOverview();
    }

    // Khởi tạo các view từ layout
    private void initializeViews() {
        monthSpinner = findViewById(R.id.monthSpinner); // Spinner chọn tháng
        yearSpinner = findViewById(R.id.yearSpinner); // Spinner chọn năm
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView); // TextView hiển thị tổng chi tiêu
        remainingBudgetTextView = findViewById(R.id.remainingBudgetTextView); // TextView hiển thị ngân sách còn lại
        categoryBreakdownRecyclerView = findViewById(R.id.categoryBreakdownRecyclerView); // RecyclerView phân tích danh mục
        expenseTrendChart = findViewById(R.id.expenseTrendChart); // Biểu đồ xu hướng chi tiêu
    }

    // Thiết lập RecyclerView để hiển thị phân tích chi tiêu theo danh mục
    private void setupRecyclerView() {
        categoryBreakdownList = new ArrayList<>(); // Khởi tạo danh sách phân tích danh mục
        categoryAdapter = new CategoryBreakdownAdapter(categoryBreakdownList); // Khởi tạo adapter
        categoryBreakdownRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Sử dụng LinearLayoutManager
        categoryBreakdownRecyclerView.setAdapter(categoryAdapter); // Gán adapter cho RecyclerView
    }

    // Thiết lập Spinner để chọn tháng và năm
    private void setupSpinners() {
        // Tạo danh sách các tháng (1-12)
        ArrayList<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        // Thiết lập adapter cho Spinner tháng
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Tạo danh sách các năm (từ 2020 đến năm hiện tại)
        ArrayList<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2020; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        // Thiết lập adapter cho Spinner năm
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Đặt giá trị mặc định cho Spinner: tháng và năm hiện tại
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        yearSpinner.setSelection(years.size() - 1);
    }

    // Thiết lập biểu đồ xu hướng chi tiêu
    private void setupChart() {
        expenseTrendChart.getDescription().setEnabled(false); // Tắt mô tả của biểu đồ
        expenseTrendChart.setTouchEnabled(true); // Cho phép tương tác với biểu đồ
        expenseTrendChart.setPinchZoom(true); // Cho phép zoom bằng cách pinch

        // Cấu hình trục X của biểu đồ
        XAxis xAxis = expenseTrendChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt trục X ở dưới cùng
        xAxis.setGranularity(1f); // Khoảng cách giữa các điểm trên trục X
        // Định dạng nhãn trục X thành tên tháng
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}));
    }

    // Cập nhật tổng quan chi tiêu dựa trên tháng và năm được chọn
    private void updateOverview() {
        // Lấy tháng và năm từ Spinner
        int month = Integer.parseInt(monthSpinner.getSelectedItem().toString());
        int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

        // Tính tổng chi tiêu trong tháng
        double totalExpense = dbHelper.getTotalExpenseByMonth(userId, month, year);
        totalExpenseTextView.setText("Total Expense: $" + currencyFormat.format(totalExpense));

        // Tính tổng ngân sách trong tháng
        double totalBudget = 0.0;
        ArrayList<Budget> budgets = dbHelper.getAllBudgets(userId);
        for (Budget budget : budgets) {
            if (budget.getMonth() == month && budget.getYear() == year) {
                totalBudget += budget.getAmount();
            }
        }
        // Tính ngân sách còn lại và hiển thị
        double remainingBudget = totalBudget - totalExpense;
        remainingBudgetTextView.setText("Remaining Budget: $" + currencyFormat.format(remainingBudget));
        // Đổi màu chữ: đỏ nếu âm, xanh nếu dương
        if (remainingBudget < 0) {
            remainingBudgetTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            remainingBudgetTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

        // Cập nhật phân tích theo danh mục và biểu đồ xu hướng
        updateCategoryBreakdown(month, year);
        updateExpenseTrendChart(year);
    }

    // Cập nhật danh sách phân tích chi tiêu theo danh mục
    private void updateCategoryBreakdown(int month, int year) {
        categoryBreakdownList.clear(); // Xóa danh sách cũ
        // Lấy danh sách các danh mục từ database
        ArrayList<String> categories = dbHelper.getCategories(userId);
        // Tính tổng chi tiêu cho từng danh mục
        for (String category : categories) {
            double amount = dbHelper.getTotalExpenseByCategory(userId, category, month, year);
            if (amount > 0) {
                categoryBreakdownList.add(new CategoryBreakdown(category, amount));
            }
        }
        // Hiển thị thông báo nếu không có chi tiêu
        if (categoryBreakdownList.isEmpty()) {
            Toast.makeText(this, "No expenses found for this month", Toast.LENGTH_SHORT).show();
        }
        categoryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    // Cập nhật biểu đồ xu hướng chi tiêu theo năm
    private void updateExpenseTrendChart(int year) {
        // Lấy dữ liệu chi tiêu hàng tháng trong năm
        ArrayList<Double> monthlyExpenses = dbHelper.getMonthlyExpenses(userId, year);
        List<Entry> entries = new ArrayList<>();
        // Tạo các điểm dữ liệu cho biểu đồ
        for (int i = 0; i < monthlyExpenses.size(); i++) {
            entries.add(new Entry(i + 1, monthlyExpenses.get(i).floatValue()));
        }

        // Thiết lập dữ liệu cho biểu đồ
        LineDataSet dataSet = new LineDataSet(entries, "Monthly Expenses ($)");
        dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark)); // Màu đường biểu đồ
        dataSet.setValueTextSize(10f); // Kích thước chữ giá trị
        dataSet.setLineWidth(2f); // Độ dày đường
        dataSet.setCircleColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark)); // Màu điểm
        dataSet.setCircleRadius(4f); // Bán kính điểm
        dataSet.setDrawCircleHole(false); // Không vẽ lỗ trong điểm

        // Gán dữ liệu vào biểu đồ và vẽ lại
        LineData lineData = new LineData(dataSet);
        expenseTrendChart.setData(lineData);
        expenseTrendChart.invalidate();
    }
}