package com.example.expandmanagementsystem;

// Lớp CategoryBreakdown: Đại diện cho một bản ghi phân tích chi tiêu theo danh mục trong ứng dụng quản lý chi tiêu
public class CategoryBreakdown {
    // Thuộc tính category: Tên danh mục chi tiêu (ví dụ: "Ăn uống", "Di chuyển")
    private String category;

    // Thuộc tính amount: Tổng số tiền chi tiêu thuộc danh mục này (kiểu double để hỗ trợ số thực)
    private double amount;

    // Constructor: Khởi tạo một đối tượng CategoryBreakdown với danh mục và số tiền
    public CategoryBreakdown(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    // Getter cho category: Trả về tên danh mục chi tiêu
    public String getCategory() {
        return category;
    }

    // Getter cho amount: Trả về tổng số tiền chi tiêu thuộc danh mục
    public double getAmount() {
        return amount;
    }
}