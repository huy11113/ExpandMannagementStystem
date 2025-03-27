package com.example.expandmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DatabaseHelper(this);

        // Thiết lập Spinner với danh sách vai trò
        String[] roles = {"admin", "student"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String role = spinnerRole.getSelectedItem().toString();

                // Kiểm tra dữ liệu đầu vào
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (dbHelper.checkUsername(username)) {
                    Toast.makeText(MainActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Thêm người dùng vào cơ sở dữ liệu
                    boolean isAdded = dbHelper.addUser(username, password, role);
                    if (isAdded) {
                        Toast.makeText(MainActivity.this, "Registration successful as " + role, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Đóng MainActivity
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}