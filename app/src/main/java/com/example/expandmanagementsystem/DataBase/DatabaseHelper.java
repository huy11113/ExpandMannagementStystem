package com.example.expandmanagementsystem.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expandmanagementsystem.model.Budget;
import com.example.expandmanagementsystem.model.Expense;
import com.example.expandmanagementsystem.model.RecurringExpense;
import com.example.expandmanagementsystem.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Hằng số cho database
    public static final String DATABASE_NAME = "UserDB"; // Tên database
    public static final int DATABASE_VERSION = 3; // Phiên bản database, tăng khi thay đổi cấu trúc

    // Hằng số cho bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id"; // Cột ID, khóa chính
    public static final String COLUMN_USERNAME = "username"; // Cột tên người dùng
    public static final String COLUMN_PASSWORD = "password"; // Cột mật khẩu
    public static final String COLUMN_ROLE = "role"; // Cột vai trò (admin/user)

    // Hằng số cho bảng expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String EXPENSE_ID = "expense_id"; // Cột ID chi tiêu
    public static final String EXPENSE_USER_ID = "user_id"; // Cột ID người dùng (khóa ngoại)
    public static final String EXPENSE_DESCRIPTION = "description"; // Cột mô tả chi tiêu
    public static final String EXPENSE_AMOUNT = "amount"; // Cột số tiền chi tiêu
    public static final String EXPENSE_DATE = "date"; // Cột ngày chi tiêu (định dạng yyyy-MM-dd)
    public static final String EXPENSE_CATEGORY = "category"; // Cột danh mục chi tiêu
    public static final String EXPENSE_RECURRING_ID = "recurring_id"; // Cột ID chi tiêu định kỳ (nếu có)

    // Hằng số cho bảng budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String BUDGET_ID = "budget_id"; // Cột ID ngân sách
    public static final String BUDGET_USER_ID = "user_id"; // Cột ID người dùng (khóa ngoại)
    public static final String BUDGET_CATEGORY = "category"; // Cột danh mục ngân sách
    public static final String BUDGET_MONTH = "month"; // Cột tháng ngân sách (1-12)
    public static final String BUDGET_YEAR = "year"; // Cột năm ngân sách
    public static final String BUDGET_AMOUNT = "amount"; // Cột số tiền ngân sách

    // Hằng số cho bảng recurring_expenses
    public static final String TABLE_RECURRING_EXPENSES = "recurring_expenses";
    public static final String RECURRING_ID = "recurring_id"; // Cột ID chi tiêu định kỳ
    public static final String RECURRING_USER_ID = "user_id"; // Cột ID người dùng (khóa ngoại)
    public static final String RECURRING_DESCRIPTION = "description"; // Cột mô tả chi tiêu định kỳ
    public static final String RECURRING_AMOUNT = "amount"; // Cột số tiền chi tiêu định kỳ
    public static final String RECURRING_CATEGORY = "category"; // Cột danh mục chi tiêu định kỳ
    public static final String RECURRING_START_DATE = "start_date"; // Cột ngày bắt đầu (yyyy-MM-dd)
    public static final String RECURRING_END_DATE = "end_date"; // Cột ngày kết thúc (yyyy-MM-dd)
    public static final String RECURRING_FREQUENCY = "frequency"; // Cột tần suất (monthly, v.v.)

    // Constructor: Khởi tạo database với context
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users: Lưu thông tin người dùng
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // ID tự tăng
                + COLUMN_USERNAME + " TEXT NOT NULL," // Tên người dùng, không null
                + COLUMN_PASSWORD + " TEXT NOT NULL," // Mật khẩu, không null
                + COLUMN_ROLE + " TEXT NOT NULL" + ")"; // Vai trò, không null
        db.execSQL(CREATE_USERS_TABLE);

        // Tạo bảng expenses: Lưu thông tin chi tiêu
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXPENSE_USER_ID + " INTEGER NOT NULL,"
                + EXPENSE_DESCRIPTION + " TEXT NOT NULL,"
                + EXPENSE_AMOUNT + " REAL NOT NULL," // REAL để lưu số thực (double)
                + EXPENSE_DATE + " TEXT NOT NULL,"
                + EXPENSE_CATEGORY + " TEXT NOT NULL,"
                + EXPENSE_RECURRING_ID + " INTEGER," // Có thể null nếu không liên kết chi tiêu định kỳ
                + "FOREIGN KEY(" + EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE)"; // Khóa ngoại, xóa người dùng sẽ xóa chi tiêu
        db.execSQL(CREATE_EXPENSES_TABLE);

        // Tạo bảng budgets: Lưu thông tin ngân sách
        String CREATE_BUDGETS_TABLE = "CREATE TABLE " + TABLE_BUDGETS + "("
                + BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BUDGET_USER_ID + " INTEGER NOT NULL,"
                + BUDGET_CATEGORY + " TEXT NOT NULL,"
                + BUDGET_MONTH + " INTEGER NOT NULL,"
                + BUDGET_YEAR + " INTEGER NOT NULL,"
                + BUDGET_AMOUNT + " REAL NOT NULL,"
                + "FOREIGN KEY(" + BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE,"
                + "UNIQUE(" + BUDGET_USER_ID + ", " + BUDGET_CATEGORY + ", " + BUDGET_MONTH + ", " + BUDGET_YEAR + "))"; // Ràng buộc UNIQUE để tránh trùng ngân sách
        db.execSQL(CREATE_BUDGETS_TABLE);

        // Tạo bảng recurring_expenses: Lưu thông tin chi tiêu định kỳ
        String CREATE_RECURRING_EXPENSES_TABLE = "CREATE TABLE " + TABLE_RECURRING_EXPENSES + "("
                + RECURRING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RECURRING_USER_ID + " INTEGER NOT NULL,"
                + RECURRING_DESCRIPTION + " TEXT NOT NULL,"
                + RECURRING_AMOUNT + " REAL NOT NULL,"
                + RECURRING_CATEGORY + " TEXT NOT NULL,"
                + RECURRING_START_DATE + " TEXT NOT NULL,"
                + RECURRING_END_DATE + " TEXT NOT NULL,"
                + RECURRING_FREQUENCY + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + RECURRING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_RECURRING_EXPENSES_TABLE);

        // Tạo index để tăng tốc độ truy vấn theo ngày và tháng/năm
        db.execSQL("CREATE INDEX idx_expenses_date ON " + TABLE_EXPENSES + "(" + EXPENSE_DATE + ")"); // Index cho cột date
        db.execSQL("CREATE INDEX idx_budgets_month_year ON " + TABLE_BUDGETS + "(" + BUDGET_MONTH + ", " + BUDGET_YEAR + ")"); // Index cho month và year
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu database được nâng cấp phiên bản
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_EXPENSES);
        onCreate(db); // Tạo lại các bảng
    }

    // --- Users ---
    // Thêm người dùng mới vào bảng users
    public boolean addUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase(); // Mở database để ghi
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);
            values.put(COLUMN_ROLE, role);
            long result = db.insertOrThrow(TABLE_USERS, null, values); // Thêm dữ liệu, throw exception nếu lỗi
            return result != -1; // Trả về true nếu thêm thành công
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding user: " + e.getMessage());
            return false;
        } finally {
            db.close(); // Đóng database
        }
    }

    // Kiểm tra xem username đã tồn tại trong database chưa
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase(); // Mở database để đọc
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                    COLUMN_USERNAME + "=?", new String[]{username},
                    null, null, null);
            return cursor.getCount() > 0; // Trả về true nếu username đã tồn tại
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking username: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Lấy thông tin người dùng theo username
    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_ROLE},
                    COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
                return new User(id, username, password, role); // Trả về đối tượng User
            }
            return null; // Trả về null nếu không tìm thấy
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user by username: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Lấy ID người dùng theo username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                    COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)); // Trả về ID
            }
            return -1; // Trả về -1 nếu không tìm thấy
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user ID: " + e.getMessage());
            return -1;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // --- Expenses ---
    // Thêm chi tiêu mới vào bảng expenses
    public boolean addExpense(int userId, String description, double amount, String date, String category, Integer recurringId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(EXPENSE_USER_ID, userId);
            values.put(EXPENSE_DESCRIPTION, description);
            values.put(EXPENSE_AMOUNT, amount);
            values.put(EXPENSE_DATE, date);
            values.put(EXPENSE_CATEGORY, category);
            if (recurringId != null) values.put(EXPENSE_RECURRING_ID, recurringId); // Liên kết với chi tiêu định kỳ nếu có
            long result = db.insertOrThrow(TABLE_EXPENSES, null, values);
            return result != -1; // Trả về true nếu thêm thành công
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding expense: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Cập nhật thông tin chi tiêu
    public boolean updateExpense(int expenseId, String description, double amount, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(EXPENSE_DESCRIPTION, description);
            values.put(EXPENSE_AMOUNT, amount);
            values.put(EXPENSE_DATE, date);
            values.put(EXPENSE_CATEGORY, category);
            int result = db.update(TABLE_EXPENSES, values, EXPENSE_ID + "=?", new String[]{String.valueOf(expenseId)});
            return result > 0; // Trả về true nếu cập nhật ít nhất 1 bản ghi
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating expense: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Xóa chi tiêu
    public boolean deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsAffected = db.delete(TABLE_EXPENSES, EXPENSE_ID + "=?", new String[]{String.valueOf(expenseId)});
            return rowsAffected > 0; // Trả về true nếu xóa thành công
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting expense: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Lấy danh sách tất cả chi tiêu của một người dùng
    public ArrayList<Expense> getExpenses(int userId) {
        ArrayList<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_EXPENSES, null, EXPENSE_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, EXPENSE_DATE + " DESC"); // Sắp xếp theo ngày giảm dần
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_ID));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DESCRIPTION));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(EXPENSE_AMOUNT));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DATE));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_CATEGORY));
                    Integer recurringId = cursor.isNull(cursor.getColumnIndexOrThrow(EXPENSE_RECURRING_ID)) ? null :
                            cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_RECURRING_ID));
                    expenseList.add(new Expense(id, userId, description, amount, date, category, recurringId));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting expenses: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return expenseList;
    }

    // Lấy danh sách các danh mục chi tiêu duy nhất của một người dùng
    public ArrayList<String> getCategories(int userId) {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT " + EXPENSE_CATEGORY + " FROM " + TABLE_EXPENSES +
                    " WHERE " + EXPENSE_USER_ID + "=?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(0);
                    if (category != null) categories.add(category);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting categories: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return new ArrayList<>(new HashSet<>(categories)); // Loại bỏ trùng lặp bằng HashSet
    }

    // Tính tổng chi tiêu của một người dùng trong một tháng
    public double getTotalExpenseByMonth(int userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(" + EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                            " WHERE " + EXPENSE_USER_ID + "=?" +
                            " AND strftime('%m', " + EXPENSE_DATE + ")=? AND strftime('%Y', " + EXPENSE_DATE + ")=?",
                    new String[]{String.valueOf(userId), String.format("%02d", month), String.valueOf(year)});
            if (cursor.moveToFirst()) {
                return cursor.isNull(0) ? 0.0 : cursor.getDouble(0); // Trả về 0 nếu không có chi tiêu
            }
            return 0.0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting total expense by month: " + e.getMessage());
            return 0.0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Tính tổng chi tiêu theo danh mục trong một tháng
    public double getTotalExpenseByCategory(int userId, String category, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(" + EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                            " WHERE " + EXPENSE_USER_ID + "=?" +
                            " AND " + EXPENSE_CATEGORY + "=?" +
                            " AND strftime('%m', " + EXPENSE_DATE + ")=? AND strftime('%Y', " + EXPENSE_DATE + ")=?",
                    new String[]{String.valueOf(userId), category, String.format("%02d", month), String.valueOf(year)});
            if (cursor.moveToFirst()) {
                return cursor.isNull(0) ? 0.0 : cursor.getDouble(0);
            }
            return 0.0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting total expense by category: " + e.getMessage());
            return 0.0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Lấy danh sách chi tiêu hàng tháng trong một năm
    public ArrayList<Double> getMonthlyExpenses(int userId, int year) {
        ArrayList<Double> monthlyExpenses = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) monthlyExpenses.add(0.0); // Khởi tạo 12 tháng với giá trị 0
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT strftime('%m', " + EXPENSE_DATE + ") AS month, SUM(" + EXPENSE_AMOUNT + ")" +
                            " FROM " + TABLE_EXPENSES +
                            " WHERE " + EXPENSE_USER_ID + "=?" +
                            " AND strftime('%Y', " + EXPENSE_DATE + ")=?" +
                            " GROUP BY strftime('%m', " + EXPENSE_DATE + ")",
                    new String[]{String.valueOf(userId), String.valueOf(year)});
            while (cursor.moveToNext()) {
                int month = Integer.parseInt(cursor.getString(0)) - 1; // Tháng từ 1-12, index từ 0-11
                double total = cursor.isNull(1) ? 0.0 : cursor.getDouble(1);
                monthlyExpenses.set(month, total);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting monthly expenses: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return monthlyExpenses;
    }

    // Lấy danh sách chi tiêu trong một khoảng thời gian
    public ArrayList<Expense> getExpensesByDateRange(int userId, String startDate, String endDate) {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_EXPENSES +
                            " WHERE " + EXPENSE_USER_ID + "=?" +
                            " AND " + EXPENSE_DATE + " BETWEEN ? AND ?",
                    new String[]{String.valueOf(userId), startDate, endDate});
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(EXPENSE_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_DATE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_CATEGORY));
                Integer recurringId = cursor.isNull(cursor.getColumnIndexOrThrow(EXPENSE_RECURRING_ID)) ? null :
                        cursor.getInt(cursor.getColumnIndexOrThrow(EXPENSE_RECURRING_ID));
                expenses.add(new Expense(id, userId, description, amount, date, category, recurringId));
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting expenses by date range: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return expenses;
    }

    // --- Budgets ---
    // Thêm hoặc cập nhật ngân sách
    public boolean addBudget(int userId, String category, int month, int year, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu transaction để đảm bảo tính toàn vẹn
        try {
            ContentValues values = new ContentValues();
            values.put(BUDGET_USER_ID, userId);
            values.put(BUDGET_CATEGORY, category);
            values.put(BUDGET_MONTH, month);
            values.put(BUDGET_YEAR, year);
            values.put(BUDGET_AMOUNT, amount);

            // Kiểm tra xem ngân sách đã tồn tại chưa
            Cursor cursor = db.query(TABLE_BUDGETS, new String[]{BUDGET_ID},
                    BUDGET_USER_ID + "=?" + " AND " + BUDGET_CATEGORY + "=?" +
                            " AND " + BUDGET_MONTH + "=?" + " AND " + BUDGET_YEAR + "=?",
                    new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)},
                    null, null, null);

            boolean success;
            if (cursor.moveToFirst()) {
                // Nếu tồn tại, cập nhật ngân sách
                int rowsAffected = db.update(TABLE_BUDGETS, values,
                        BUDGET_USER_ID + "=?" + " AND " + BUDGET_CATEGORY + "=?" +
                                " AND " + BUDGET_MONTH + "=?" + " AND " + BUDGET_YEAR + "=?",
                        new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)});
                success = rowsAffected > 0;
                Log.d("DatabaseHelper", "Updated budget: rowsAffected=" + rowsAffected);
            } else {
                // Nếu không tồn tại, thêm mới ngân sách
                long result = db.insertOrThrow(TABLE_BUDGETS, null, values);
                success = result != -1;
                Log.d("DatabaseHelper", "Inserted budget: result=" + result);
            }
            cursor.close();
            db.setTransactionSuccessful(); // Đánh dấu transaction thành công
            return success;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding/updating budget: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction(); // Kết thúc transaction
            db.close();
        }
    }

    // Lấy danh sách tất cả ngân sách của một người dùng
    public ArrayList<Budget> getAllBudgets(int userId) {
        ArrayList<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BUDGETS, null,
                    BUDGET_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, BUDGET_YEAR + " DESC, " + BUDGET_MONTH + " DESC"); // Sắp xếp theo năm, tháng giảm dần
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(BUDGET_ID));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(BUDGET_CATEGORY));
                int month = cursor.getInt(cursor.getColumnIndexOrThrow(BUDGET_MONTH));
                int year = cursor.getInt(cursor.getColumnIndexOrThrow(BUDGET_YEAR));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(BUDGET_AMOUNT));
                budgets.add(new Budget(id, userId, category, month, year, amount));
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all budgets: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return budgets;
    }

    // Lấy một ngân sách cụ thể theo userId, category, month, year
    public Budget getBudget(int userId, String category, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BUDGETS, null,
                    BUDGET_USER_ID + "=?" + " AND " + BUDGET_CATEGORY + "=?" +
                            " AND " + BUDGET_MONTH + "=?" + " AND " + BUDGET_YEAR + "=?",
                    new String[]{String.valueOf(userId), category, String.valueOf(month), String.valueOf(year)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(BUDGET_ID));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(BUDGET_AMOUNT));
                return new Budget(id, userId, category, month, year, amount);
            }
            return null; // Trả về null nếu không tìm thấy
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting budget: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // --- Recurring Expenses ---
    // Thêm chi tiêu định kỳ mới
    public boolean addRecurringExpense(RecurringExpense recurringExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(RECURRING_USER_ID, recurringExpense.getUserId());
            values.put(RECURRING_DESCRIPTION, recurringExpense.getDescription());
            values.put(RECURRING_AMOUNT, recurringExpense.getAmount());
            values.put(RECURRING_CATEGORY, recurringExpense.getCategory());
            values.put(RECURRING_START_DATE, recurringExpense.getStartDate());
            values.put(RECURRING_END_DATE, recurringExpense.getEndDate());
            values.put(RECURRING_FREQUENCY, recurringExpense.getFrequency());
            long id = db.insertOrThrow(TABLE_RECURRING_EXPENSES, null, values);
            recurringExpense.setId((int) id); // Cập nhật ID cho đối tượng
            Log.d("DatabaseHelper", "Added recurring expense: id=" + id);
            return true;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding recurring expense: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Lấy danh sách tất cả chi tiêu định kỳ của một người dùng
    public ArrayList<RecurringExpense> getRecurringExpenses(int userId) {
        ArrayList<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RECURRING_EXPENSES, null,
                    RECURRING_USER_ID + "=?", new String[]{String.valueOf(userId)},
                    null, null, RECURRING_START_DATE + " DESC"); // Sắp xếp theo ngày bắt đầu giảm dần
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(RECURRING_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(RECURRING_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_CATEGORY));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_END_DATE));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_FREQUENCY));
                recurringExpenses.add(new RecurringExpense(id, userId, description, amount, category, startDate, endDate, frequency));
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting recurring expenses: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return recurringExpenses; // Trả về danh sách chi tiêu định kỳ
    }

    // Cập nhật thông tin chi tiêu định kỳ
    public boolean updateRecurringExpense(RecurringExpense recurringExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(RECURRING_DESCRIPTION, recurringExpense.getDescription());
            values.put(RECURRING_AMOUNT, recurringExpense.getAmount());
            values.put(RECURRING_CATEGORY, recurringExpense.getCategory());
            values.put(RECURRING_START_DATE, recurringExpense.getStartDate());
            values.put(RECURRING_END_DATE, recurringExpense.getEndDate());
            values.put(RECURRING_FREQUENCY, recurringExpense.getFrequency());
            int rowsAffected = db.update(TABLE_RECURRING_EXPENSES, values,
                    RECURRING_ID + "=?", new String[]{String.valueOf(recurringExpense.getId())});
            Log.d("DatabaseHelper", "Updated recurring expense: rowsAffected=" + rowsAffected);
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating recurring expense: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    // Xóa chi tiêu định kỳ và các chi tiêu liên quan
    public boolean deleteRecurringExpense(int recurringExpenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu transaction để xóa đồng bộ
        try {
            // Xóa tất cả chi tiêu liên quan trong bảng expenses
            db.delete(TABLE_EXPENSES, EXPENSE_RECURRING_ID + "=?", new String[]{String.valueOf(recurringExpenseId)});
            // Xóa chi tiêu định kỳ trong bảng recurring_expenses
            int rowsAffected = db.delete(TABLE_RECURRING_EXPENSES, RECURRING_ID + "=?", new String[]{String.valueOf(recurringExpenseId)});
            db.setTransactionSuccessful(); // Đánh dấu transaction thành công
            return rowsAffected > 0; // Trả về true nếu xóa thành công
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting recurring expense: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction(); // Kết thúc transaction
            db.close();
        }
    }

    // Xử lý chi tiêu định kỳ: Tự động tạo chi tiêu dựa trên tần suất
    public void processRecurringExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            // Lấy tất cả chi tiêu định kỳ từ bảng recurring_expenses
            cursor = db.query(TABLE_RECURRING_EXPENSES, null, null, null, null, null, null);
            if (!cursor.moveToFirst()) {
                Log.d("DatabaseHelper", "No recurring expenses to process");
                return; // Thoát nếu không có chi tiêu định kỳ
            }

            // Định dạng ngày và lấy ngày hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false); // Không cho phép định dạng ngày linh hoạt
            Date currentDate = Calendar.getInstance().getTime();

            // Duyệt qua từng chi tiêu định kỳ
            do {
                int recurringId = cursor.getInt(cursor.getColumnIndexOrThrow(RECURRING_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(RECURRING_USER_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(RECURRING_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_CATEGORY));
                String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_START_DATE));
                String endDateStr = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_END_DATE));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(RECURRING_FREQUENCY));

                // Kiểm tra dữ liệu null để tránh lỗi
                if (description == null || category == null || frequency == null || startDateStr == null || endDateStr == null) {
                    Log.e("DatabaseHelper", "Invalid recurring expense data (null): recurringId=" + recurringId);
                    continue; // Bỏ qua nếu dữ liệu không hợp lệ
                }

                // Chuyển đổi chuỗi ngày thành đối tượng Date
                Date startDate, endDate;
                try {
                    startDate = sdf.parse(startDateStr);
                    endDate = sdf.parse(endDateStr);
                } catch (ParseException e) {
                    Log.e("DatabaseHelper", "Date parse error for recurringId=" + recurringId +
                            ": startDate=" + startDateStr + ", endDate=" + endDateStr + ", error=" + e.getMessage());
                    continue; // Bỏ qua nếu ngày không hợp lệ
                }

                // Kiểm tra xem ngày hiện tại có nằm trong khoảng thời gian của chi tiêu định kỳ không
                if (!currentDate.after(startDate) || !currentDate.before(endDate)) continue;

                // Xử lý chi tiêu định kỳ theo tần suất "monthly"
                if (frequency.equalsIgnoreCase("monthly")) {
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTime(startDate);
                    Calendar currentCal = Calendar.getInstance();
                    currentCal.setTime(currentDate);
                    Calendar tempCal = (Calendar) startCal.clone();

                    // Tạo chi tiêu định kỳ cho từng tháng từ startDate đến currentDate
                    while (tempCal.getTime().before(currentCal.getTime()) || tempCal.getTime().equals(currentCal.getTime())) {
                        int month = tempCal.get(Calendar.MONTH) + 1; // Tháng từ 0-11, cộng 1 để thành 1-12
                        int year = tempCal.get(Calendar.YEAR);
                        String expenseDate = String.format(Locale.getDefault(), "%d-%02d-01", year, month); // Ngày đầu tháng

                        // Kiểm tra xem chi tiêu đã tồn tại chưa
                        Cursor checkCursor = null;
                        try {
                            checkCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXPENSES +
                                            " WHERE " + EXPENSE_RECURRING_ID + "=?" + " AND " + EXPENSE_DATE + "=?",
                                    new String[]{String.valueOf(recurringId), expenseDate});
                            if (checkCursor.moveToFirst() && checkCursor.getInt(0) == 0) {
                                // Nếu chưa tồn tại, thêm chi tiêu mới
                                boolean success = addExpense(userId, description, amount, expenseDate, category, recurringId);
                                if (!success) {
                                    Log.e("DatabaseHelper", "Failed to add recurring expense: recurringId=" + recurringId + ", date=" + expenseDate);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DatabaseHelper", "Error checking existing expense: " + e.getMessage());
                        } finally {
                            if (checkCursor != null) checkCursor.close();
                        }
                        tempCal.add(Calendar.MONTH, 1); // Tăng tháng lên 1
                    }
                } else {
                    // Ghi log cảnh báo nếu tần suất không được hỗ trợ
                    Log.w("DatabaseHelper", "Unsupported frequency: " + frequency + " (recurringId=" + recurringId + ")");
                }
            } while (cursor.moveToNext());
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error processing recurring expenses: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}