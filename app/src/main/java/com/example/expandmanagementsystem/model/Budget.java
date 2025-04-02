package com.example.expandmanagementsystem.model;

public class Budget {
    private int id;
    private int userId;
    private String category;
    private int month;
    private int year;
    private double amount;

    public Budget(int id, int userId, String category, int month, int year, double amount) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.month = month;
        this.year = year;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}