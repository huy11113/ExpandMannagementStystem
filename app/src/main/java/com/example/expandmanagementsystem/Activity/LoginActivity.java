package com.example.expandmanagementsystem.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expandmanagementsystem.DataBase.DatabaseHelper;
import com.example.expandmanagementsystem.R;
import com.example.expandmanagementsystem.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các thành phần giao diện
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Xử lý nút Login
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            // Kiểm tra các trường nhập liệu
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra thông tin đăng nhập
            User user = dbHelper.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                String role = user.getRole();
                int userId = user.getId();

                // Lưu thông tin người dùng vào SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userId", userId);
                editor.putString("username", username);
                editor.putString("role", role);
                editor.apply();

                Log.d("LoginActivity", "Login successful: userId=" + userId + ", role=" + role);
                Toast.makeText(LoginActivity.this, "Login successful as " + role, Toast.LENGTH_SHORT).show();

                // Chuyển đến màn hình tương ứng dựa trên role
                Intent intent;
                if (role.equals("admin")) {
                    // Chuyển đến màn hình dành cho admin
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                } else if (role.equals("student")) {
                    intent = new Intent(LoginActivity.this, MenuActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid role", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // Đóng LoginActivity
            } else {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Chuyển đến màn hình Register
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Sửa MainActivity thành RegisterActivity
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Đóng LoginActivity
        });
    }
}