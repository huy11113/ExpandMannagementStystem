package com.example.expandmanagementsystem;

public class Expense {
    private int id;
    private int userId;
    private String description;
    private double amount;
    private String date;
    private String category;

    public Expense(int id, int userId, String description, double amount, String date, String category) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // Getter
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return description + " - $" + amount + " - " + date + " - " + category;
    }
}
