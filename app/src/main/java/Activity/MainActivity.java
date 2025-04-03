package com.example.expandmanagementsystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        dbHelper = new DatabaseHelper(this);

        // Xử lý nút Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String role = "student"; // Mặc định vai trò là student

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.addUser(username, password, role)) {
                    Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Chuyển đến màn hình đăng nhập sau khi đăng ký thành công
                    Intent intent = new Intent(MainActivity.this, com.example.expandmanagementsystem.Activity.LoginActivity.class);
                    startActivity(intent);
                    finish(); // Đóng RegisterActivity
                } else {
                    Toast.makeText(MainActivity.this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Chuyển đến màn hình Login khi nhấn TextView
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.expandmanagementsystem.Activity.LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng RegisterActivity
            }
        });
    }
}