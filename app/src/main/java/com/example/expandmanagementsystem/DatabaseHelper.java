package com.example.expandmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Các hằng số public static để truy cập từ các lớp khác
    public static final String DATABASE_NAME = "UserDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    // Các hằng số cho bảng expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String EXPENSE_ID = "expense_id";
    public static final String EXPENSE_USER_ID = "user_id";
    public static final String EXPENSE_DESCRIPTION = "description";
    public static final String EXPENSE_AMOUNT = "amount";
    public static final String EXPENSE_DATE = "date";
    public static final String EXPENSE_CATEGORY = "category";

    // Các hằng số cho bảng budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String BUDGET_ID = "budget_id";
    public static final String BUDGET_USER_ID = "user_id";
    public static final String BUDGET_CATEGORY = "category";
    public static final String BUDGET_MONTH = "month";
    public static final String BUDGET_YEAR = "year";
    public static final String BUDGET_AMOUNT = "amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXPENSE_USER_ID + " INTEGER,"
                + EXPENSE_DESCRIPTION + " TEXT,"
                + EXPENSE_AMOUNT + " REAL,"
                + EXPENSE_DATE + " TEXT,"
                + EXPENSE_CATEGORY + " TEXT,"
                + "FOREIGN KEY(" + EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_EXPENSES_TABLE);

        String CREATE_BUDGETS_TABLE = "CREATE TABLE " + TABLE_BUDGETS + "("
                + BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BUDGET_USER_ID + " INTEGER,"
                + BUDGET_CATEGORY + " TEXT,"
                + BUDGET_MONTH + " INTEGER,"
                + BUDGET_YEAR + " INTEGER,"
                + BUDGET_AMOUNT + " REAL,"
                + "FOREIGN KEY(" + BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "UNIQUE(" + BUDGET_USER_ID + ", " + BUDGET_CATEGORY + ", " + BUDGET_MONTH + ", " + BUDGET_YEAR + "))";
        db.execSQL(CREATE_BUDGETS_TABLE);

        db.execSQL("CREATE INDEX idx_expenses_date ON " + TABLE_EXPENSES + "(" + EXPENSE_DATE + ")");
        db.execSQL("CREATE INDEX idx_budgets_month_year ON " + TABLE_BUDGETS + "(" + BUDGET_MONTH + ", " + BUDGET_YEAR + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        onCreate(db);
    }

    // Thêm người dùng mới
    public boolean addUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Kiểm tra xem username đã tồn tại chưa
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Thêm chi tiêu mới
    public boolean addExpense(int userId, String description, double amount, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSE_USER_ID, userId);
        values.put(EXPENSE_DESCRIPTION, description);
        values.put(EXPENSE_AMOUNT, amount);
        values.put(EXPENSE_DATE, date);
        values.put(EXPENSE_CATEGORY, category);
        long result = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return result != -1;
    }

    // Cập nhật chi tiêu
    public boolean updateExpense(int expenseId, String description, double amount, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSE_DESCRIPTION, description);
        values.put(EXPENSE_AMOUNT, amount);
        values.put(EXPENSE_DATE, date);
        values.put(EXPENSE_CATEGORY, category);
        int result = db.update(TABLE_EXPENSES, values, EXPENSE_ID + "=?", new String[]{String.valueOf(expenseId)});
        db.close();
        return result > 0;
    }

    // Lấy danh sách chi tiêu theo userId
    public ArrayList<Expense> getExpenses(int userId) {
        ArrayList<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, null,
                EXPENSE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(EXPENSE_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DATE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_CATEGORY));
                expenseList.add(new Expense(id, userId, description, amount, date, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }

    // Lấy userId từ username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
            db.close();
            return userId;
        }
        if (cursor != null) cursor.close();
        db.close();
        return -1;
    }

    // Lấy danh sách danh mục duy nhất từ bảng expenses theo userId (Dùng trong Budget Setting, Expense Overview, Expense Reports)
    public ArrayList<String> getCategories(int userId) {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + EXPENSE_CATEGORY + " FROM " + TABLE_EXPENSES +
                " WHERE " + EXPENSE_USER_ID + " = ? AND " + EXPENSE_CATEGORY + " IS NOT NULL", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    // Thêm ngân sách (Dùng trong Budget Setting)
    public void addBudget(int userId, String category, int month, int year, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUDGET_USER_ID, userId);
        values.put(BUDGET_CATEGORY, category);
        values.put(BUDGET_MONTH, month);
        values.put(BUDGET_YEAR, year);
        values.put(BUDGET_AMOUNT, amount);
        Cursor cursor = db.rawQuery("SELECT " + BUDGET_ID + " FROM " + TABLE_BUDGETS +
                        " WHERE " + BUDGET_USER_ID + " = ? AND " + BUDGET_CATEGORY + " = ? AND " + BUDGET_MONTH + " = ? AND " + BUDGET_YEAR + " = ?",
                new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)});
        if (cursor.moveToFirst()) {
            db.update(TABLE_BUDGETS, values,
                    BUDGET_USER_ID + " = ? AND " + BUDGET_CATEGORY + " = ? AND " + BUDGET_MONTH + " = ? AND " + BUDGET_YEAR + " = ?",
                    new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)});
        } else {
            db.insert(TABLE_BUDGETS, null, values);
        }
        cursor.close();
        db.close();
    }

    // Lấy ngân sách (Dùng trong Budget Setting và Expense Overview)
    public double getBudget(int userId, String category, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + BUDGET_AMOUNT + " FROM " + TABLE_BUDGETS +
                        " WHERE " + BUDGET_USER_ID + " = ? AND " + BUDGET_CATEGORY + " = ? AND " + BUDGET_MONTH + " = ? AND " + BUDGET_YEAR + " = ?",
                new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)});
        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(0);
            cursor.close();
            db.close();
            return amount;
        }
        cursor.close();
        db.close();
        return 0.0;
    }

    // Tính tổng chi tiêu theo danh mục và tháng (Dùng trong Expense Overview)
    public double getTotalExpenseByCategory(int userId, String category, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + EXPENSE_USER_ID + " = ? AND " + EXPENSE_CATEGORY + " = ? AND strftime('%m', " + EXPENSE_DATE + ") = ? AND strftime('%Y', " + EXPENSE_DATE + ") = ?",
                new String[]{String.valueOf(userId), category, String.format("%02d", month), String.valueOf(year)});
        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(0);
            cursor.close();
            db.close();
            return total;
        }
        cursor.close();
        db.close();
        return 0.0;
    }

    // Lấy tổng chi tiêu theo tháng (Dùng trong Expense Overview)
    public double getTotalExpenseByMonth(int userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + EXPENSE_USER_ID + " = ? AND strftime('%m', " + EXPENSE_DATE + ") = ? AND strftime('%Y', " + EXPENSE_DATE + ") = ?",
                new String[]{String.valueOf(userId), String.format("%02d", month), String.valueOf(year)});
        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(0);
            cursor.close();
            db.close();
            return total;
        }
        cursor.close();
        db.close();
        return 0.0;
    }

    // Lấy chi tiêu theo khoảng thời gian (Dùng trong Expense Reports)
    public ArrayList<HashMap<String, String>> getExpensesByDateRange(int userId, String startDate, String endDate) {
        ArrayList<HashMap<String, String>> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + EXPENSE_ID + ", " + EXPENSE_DESCRIPTION + ", " + EXPENSE_AMOUNT + ", " +
                        EXPENSE_DATE + ", " + EXPENSE_CATEGORY + " FROM " + TABLE_EXPENSES +
                        " WHERE " + EXPENSE_USER_ID + " = ? AND " + EXPENSE_DATE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> expense = new HashMap<>();
                expense.put("expense_id", cursor.getString(0));
                expense.put("description", cursor.getString(1));
                expense.put("amount", cursor.getString(2));
                expense.put("date", cursor.getString(3));
                expense.put("category", cursor.getString(4));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    // Lấy tổng chi tiêu theo tháng cho biểu đồ (Dùng trong Expense Overview)
    public ArrayList<Double> getMonthlyExpenses(int userId, int year) {
        ArrayList<Double> monthlyExpenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        for (int month = 1; month <= 12; month++) {
            Cursor cursor = db.rawQuery("SELECT SUM(" + EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                            " WHERE " + EXPENSE_USER_ID + " = ? AND strftime('%m', " + EXPENSE_DATE + ") = ? AND strftime('%Y', " + EXPENSE_DATE + ") = ?",
                    new String[]{String.valueOf(userId), String.format("%02d", month), String.valueOf(year)});
            if (cursor.moveToFirst()) {
                monthlyExpenses.add(cursor.getDouble(0));
            } else {
                monthlyExpenses.add(0.0);
            }
            cursor.close();
        }
        db.close();
        return monthlyExpenses;
    }
}