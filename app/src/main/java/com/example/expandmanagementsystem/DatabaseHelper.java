package com.example.expandmanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

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

        // Tạo bảng expenses
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXPENSE_USER_ID + " INTEGER,"
                + EXPENSE_DESCRIPTION + " TEXT,"
                + EXPENSE_AMOUNT + " REAL,"
                + EXPENSE_DATE + " TEXT,"
                + EXPENSE_CATEGORY + " TEXT,"
                + "FOREIGN KEY(" + EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);

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
        return result != -1; // Trả về true nếu thêm thành công
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
}
