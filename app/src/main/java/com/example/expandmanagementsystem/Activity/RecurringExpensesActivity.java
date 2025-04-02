package com.example.expandmanagementsystem.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expandmanagementsystem.Adapter.RecurringExpenseAdapter;
import com.example.expandmanagementsystem.model.RecurringExpense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecurringExpensesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;
    private RecyclerView recurringExpensesRecyclerView;
    private RecurringExpenseAdapter adapter;
    private ArrayList<RecurringExpense> recurringExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expenses);

        // Khởi tạo DatabaseHelper và lấy userId từ Intent
        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Log.e("RecurringExpenses", "User ID not found in Intent");
            Toast.makeText(this, "Không tìm thấy User ID. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Xử lý chi tiêu định kỳ khi activity khởi động
        try {
            // Gọi hàm processRecurringExpenses để tự động tạo các chi tiêu định kỳ dựa trên ngày hiện tại
            dbHelper.processRecurringExpenses();
        } catch (Exception e) {
            // Ghi log và hiển thị thông báo nếu có lỗi khi xử lý chi tiêu định kỳ
            Log.e("RecurringExpenses", "Error processing recurring expenses: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi xử lý chi tiêu định kỳ", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện khi nhấn nút Back (FloatingActionButton)
        FloatingActionButton backFab = findViewById(R.id.backFab);
        backFab.setOnClickListener(v -> {
            // Chuyển về MenuActivity và xóa các activity khác trong stack
            Intent intent = new Intent(RecurringExpensesActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Khởi tạo RecyclerView và nút thêm chi tiêu định kỳ
        recurringExpensesRecyclerView = findViewById(R.id.recurringExpensesRecyclerView);
        FloatingActionButton addRecurringExpenseButton = findViewById(R.id.addRecurringExpenseButton);

        // Lấy danh sách chi tiêu định kỳ từ database và thiết lập adapter
        recurringExpenses = dbHelper.getRecurringExpenses(userId);
        adapter = new RecurringExpenseAdapter(recurringExpenses, new RecurringExpenseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(RecurringExpense expense) {
                // Mở dialog để chỉnh sửa chi tiêu định kỳ
                showAddEditDialog(expense);
            }

            @Override
            public void onDeleteClick(RecurringExpense expense) {
                // Hiển thị dialog xác nhận xóa chi tiêu định kỳ
                new AlertDialog.Builder(RecurringExpensesActivity.this)
                        .setTitle("Xóa Chi tiêu Định kỳ")
                        .setMessage("Bạn có chắc muốn xóa chi tiêu định kỳ này? Tất cả chi tiêu liên quan cũng sẽ bị xóa.")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Xóa chi tiêu định kỳ và cập nhật danh sách
                            dbHelper.deleteRecurringExpense(expense.getId());
                            refreshList();
                            Toast.makeText(RecurringExpensesActivity.this, "Đã xóa chi tiêu định kỳ", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Không", null)
                        .show();
            }
        });

        // Thiết lập RecyclerView với LinearLayoutManager và adapter
        recurringExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recurringExpensesRecyclerView.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút thêm chi tiêu định kỳ
        addRecurringExpenseButton.setOnClickListener(v -> showAddEditDialog(null));
    }

    // Hiển thị dialog để thêm hoặc chỉnh sửa chi tiêu định kỳ
    private void showAddEditDialog(RecurringExpense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_recurring_expense, null);
        builder.setView(dialogView);

        // Khởi tạo các view trong dialog
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        EditText amountEditText = dialogView.findViewById(R.id.amountEditText);
        EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
        EditText startDateEditText = dialogView.findViewById(R.id.startDateEditText);
        EditText endDateEditText = dialogView.findViewById(R.id.endDateEditText);
        EditText frequencyEditText = dialogView.findViewById(R.id.frequencyEditText);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);

        // Kiểm tra xem là thêm mới hay chỉnh sửa
        boolean isEdit = expense != null;
        if (isEdit) {
            // Nếu là chỉnh sửa, điền thông tin chi tiêu định kỳ vào các trường
            dialogTitle.setText("Sửa Chi tiêu Định kỳ");
            descriptionEditText.setText(expense.getDescription());
            amountEditText.setText(String.valueOf(expense.getAmount()));
            categoryEditText.setText(expense.getCategory());
            startDateEditText.setText(expense.getStartDate());
            endDateEditText.setText(expense.getEndDate());
            frequencyEditText.setText(expense.getFrequency());
        } else {
            dialogTitle.setText("Thêm Chi tiêu Định kỳ");
        }

        AlertDialog dialog = builder.create();

        // Xử lý sự kiện khi nhấn nút Save
        saveButton.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String description = descriptionEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();
            String startDate = startDateEditText.getText().toString().trim();
            String endDate = endDateEditText.getText().toString().trim();
            String frequency = frequencyEditText.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (description.isEmpty() || amountStr.isEmpty() || category.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || frequency.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra và chuyển đổi số tiền
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng và hợp lệ của ngày bắt đầu và ngày kết thúc
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false); // Không cho phép định dạng ngày linh hoạt
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                if (start == null || end == null || start.after(end)) {
                    // Kiểm tra nếu ngày bắt đầu sau ngày kết thúc
                    Toast.makeText(this, "Khoảng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Định dạng ngày không đúng (dùng YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng RecurringExpense mới
            RecurringExpense newExpense = new RecurringExpense();
            newExpense.setUserId(userId);
            newExpense.setDescription(description);
            newExpense.setAmount(amount);
            newExpense.setCategory(category);
            newExpense.setStartDate(startDate);
            newExpense.setEndDate(endDate);
            newExpense.setFrequency(frequency);

            // Lưu vào database: cập nhật nếu là chỉnh sửa, thêm mới nếu không
            if (isEdit) {
                newExpense.setId(expense.getId());
                dbHelper.updateRecurringExpense(newExpense);
                Toast.makeText(this, "Đã cập nhật chi tiêu định kỳ", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addRecurringExpense(newExpense);
                Toast.makeText(this, "Đã thêm chi tiêu định kỳ", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật danh sách và đóng dialog
            refreshList();
            dialog.dismiss();
        });

        // Xử lý sự kiện khi nhấn nút Cancel
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Hiển thị dialog
        dialog.show();
    }

    // Cập nhật danh sách chi tiêu định kỳ
    private void refreshList() {
        // Lấy lại danh sách chi tiêu định kỳ từ database
        recurringExpenses = dbHelper.getRecurringExpenses(userId);
        // Cập nhật dữ liệu cho adapter
        adapter.updateData(recurringExpenses);
        // Xử lý lại chi tiêu định kỳ để đảm bảo các chi tiêu được tạo tự động nếu cần
        dbHelper.processRecurringExpenses();
    }
}