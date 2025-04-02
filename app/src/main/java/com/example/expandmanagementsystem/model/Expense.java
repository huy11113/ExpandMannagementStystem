package com.example.expandmanagementsystem.model;

public class Expense {
    private int id;
    private int userId;
    private String description;
    private double amount;
    private String date;
    private String category;
    private Integer recurringId; // Có thể là null nếu không liên quan đến chi tiêu định kỳ

    // Constructor mới với 7 tham số
    public Expense(int id, int userId, String description, double amount, String date, String category, Integer recurringId) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.recurringId = recurringId;
    }

    // Constructor cũ (6 tham số) để tương thích với code cũ nếu cần
    public Expense(int id, int userId, String description, double amount, String date, String category) {
        this(id, userId, description, amount, date, category, null);
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getRecurringId() { return recurringId; }
    public void setRecurringId(Integer recurringId) { this.recurringId = recurringId; }
}