package com.example.expandmanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SetLimitActivity extends AppCompatActivity {
    private EditText edtCategory, edtLimit;
    private Button btnSetLimit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);

        dbHelper = new DatabaseHelper(this);
        edtCategory = findViewById(R.id.edtCategory);
        edtLimit = findViewById(R.id.edtLimit);
        btnSetLimit = findViewById(R.id.btnSetLimit);

        btnSetLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = edtCategory.getText().toString();
                double limit = Double.parseDouble(edtLimit.getText().toString());
                dbHelper.setExpenseLimit(category, limit);
                Toast.makeText(SetLimitActivity.this, "Đã đặt giới hạn!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}