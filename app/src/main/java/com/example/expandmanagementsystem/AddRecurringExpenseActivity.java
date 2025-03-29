package com.example.expandmanagementsystem;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddRecurringExpenseActivity extends AppCompatActivity {

    private EditText etTitle, etAmount, etStartDate;
    private Spinner spinnerRecurrence;
    private Button btnSave;
    private RecurringExpenseDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recurring_expense);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etStartDate = findViewById(R.id.etStartDate);
        spinnerRecurrence = findViewById(R.id.spinnerRecurrence);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new RecurringExpenseDBHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Daily", "Weekly", "Monthly"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecurrence.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String title = etTitle.getText().toString();
        String amountStr = etAmount.getText().toString();
        String startDate = etStartDate.getText().toString();
        String recurrenceType = spinnerRecurrence.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty() || startDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        RecurringExpense expense = new RecurringExpense(0, title, amount, recurrenceType, startDate);
        dbHelper.addRecurringExpense(expense);
        Toast.makeText(this, "Đã lưu chi phí định kỳ", Toast.LENGTH_SHORT).show();
        finish();
    }
}
