package com.example.expandmanagementsystem.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.example.expandmanagementsystem.model.Expense;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

// Activity quản lý theo dõi chi tiêu
public class StudentActivity extends AppCompatActivity {
    private EditText descriptionEditText, amountEditText, dateEditText, categoryEditText;
    private FloatingActionButton addExpenseButton, backFab;
    private ListView expenseListView;
    private DatabaseHelper dbHelper;
    private ArrayList<Expense> expenseList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student); // Đảm bảo tên layout khớp với XML

        // Khởi tạo các view từ layout
        descriptionEditText = findViewById(R.id.descriptionEditText);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        backFab = findViewById(R.id.backFab);
        expenseListView = findViewById(R.id.expenseListView);

        // Khởi tạo DatabaseHelper để làm việc với database
        dbHelper = new DatabaseHelper(this);

        // Lấy userId từ Intent, nếu không có thì thoát activity
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập DatePicker cho dateEditText: Khi người dùng nhấn vào trường ngày, hiển thị DatePicker
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Load danh sách chi tiêu từ database khi activity khởi động
        loadExpenses();

        // Xử lý sự kiện khi nhấn nút Add Expense
        addExpenseButton.setOnClickListener(v -> addExpense());

        // Xử lý sự kiện khi nhấn nút Back: Chuyển về MenuActivity
        backFab.setOnClickListener(v -> {
            Intent intent = new Intent(StudentActivity.this, com.example.expandmanagementsystem.Activity.MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Xử lý sự kiện khi nhấn vào một mục trong danh sách chi tiêu: Hiển thị dialog sửa/xóa
        expenseListView.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog(position));
    }

    // Hiển thị DatePicker để người dùng chọn ngày
    private void showDatePickerDialog() {
        // Lấy ngày hiện tại để hiển thị mặc định trong DatePicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog và thiết lập sự kiện khi người dùng chọn ngày
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày thành "yyyy-MM-dd" (tháng + 1 vì tháng trong Calendar bắt đầu từ 0)
                    String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    dateEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Thêm chi tiêu mới vào database
    private void addExpense() {
        // Lấy dữ liệu từ các trường nhập liệu
        String description = descriptionEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào: Không được để trống
        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra và chuyển đổi số tiền
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm chi tiêu vào database
            boolean success = dbHelper.addExpense(userId, description, amount, date, category, null);
            if (success) {
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                clearInputs(); // Xóa các trường nhập liệu
                loadExpenses(); // Cập nhật danh sách chi tiêu
            } else {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            // Hiển thị thông báo nếu số tiền không hợp lệ
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    // Xóa các ô nhập liệu sau khi thêm chi tiêu thành công
    private void clearInputs() {
        descriptionEditText.setText("");
        amountEditText.setText("");
        dateEditText.setText("");
        categoryEditText.setText("");
    }

    // Load danh sách chi tiêu từ database và hiển thị lên ListView
    private void loadExpenses() {
        // Lấy danh sách chi tiêu từ database
        expenseList = dbHelper.getExpenses(userId);
        displayList = new ArrayList<>();
        // Định dạng dữ liệu để hiển thị: Mô tả - Số tiền (Ngày, Danh mục)
        for (Expense expense : expenseList) {
            displayList.add(expense.getDescription() + " - $" + String.format("%.2f", expense.getAmount()) +
                    " (" + expense.getDate() + ", " + expense.getCategory() + ")");
        }
        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        expenseListView.setAdapter(adapter);
    }

    // Hiển thị dialog để sửa hoặc xóa chi tiêu
    private void showEditDeleteDialog(int position) {
        // Lấy chi tiêu tại vị trí được chọn
        Expense expense = expenseList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Expense");
        builder.setMessage("Choose an action for: " + expense.getDescription());

        // Nút sửa: Mở dialog chỉnh sửa chi tiêu
        builder.setPositiveButton("Edit", (dialog, which) -> showEditDialog(expense));

        // Nút xóa: Xóa chi tiêu khỏi database
        builder.setNegativeButton("Delete", (dialog, which) -> {
            boolean success = dbHelper.deleteExpense(expense.getId());
            if (success) {
                Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                loadExpenses(); // Cập nhật danh sách sau khi xóa
            } else {
                Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút hủy: Đóng dialog
        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    // Hiển thị dialog để sửa chi tiêu
    private void showEditDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate layout riêng cho dialog chỉnh sửa chi tiêu
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_expense, null);
        builder.setView(dialogView);

        // Khởi tạo các view trong dialog
        EditText editDescription = dialogView.findViewById(R.id.editDescriptionEditText);
        EditText editAmount = dialogView.findViewById(R.id.editAmountEditText);
        EditText editDate = dialogView.findViewById(R.id.editDateEditText);
        EditText editCategory = dialogView.findViewById(R.id.editCategoryEditText);

        // Điền thông tin chi tiêu hiện tại vào dialog
        editDescription.setText(expense.getDescription());
        editAmount.setText(String.valueOf(expense.getAmount()));
        editDate.setText(expense.getDate());
        editCategory.setText(expense.getCategory());

        // Thiết lập DatePicker cho trường ngày trong dialog
        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Xử lý nút Save trong dialog: Cập nhật chi tiêu
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Lấy dữ liệu từ các trường trong dialog
            String description = editDescription.getText().toString().trim();
            String amountStr = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String category = editCategory.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra và chuyển đổi số tiền
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật chi tiêu trong database
                boolean success = dbHelper.updateExpense(expense.getId(), description, amount, date, category);
                if (success) {
                    Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                    loadExpenses(); // Cập nhật danh sách sau khi sửa
                } else {
                    Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Cancel: Đóng dialog
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}