package com.example.expandmanagementsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
public class ExpenseNotificationHelper {
    private Context context;
    private DatabaseHelper dbHelper;

    public ExpenseNotificationHelper(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    // Kiểm tra chi tiêu và gửi thông báo nếu vượt quá mức
    public void checkAndNotify(String category) {
        double totalExpense = dbHelper.getTotalExpenseByCategory(category);
        double limit = dbHelper.getExpenseLimit(category);

        if (limit > 0 && totalExpense >= limit) {
            sendNotification(category, totalExpense, limit);
        }
    }

    private void sendNotification(String category, double totalExpense, double limit) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "expense_alert";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Expense Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alert) // Đặt icon phù hợp trong res/drawable
                .setContentTitle("Cảnh báo chi tiêu!")
                .setContentText("Bạn đã chi tiêu " + totalExpense + " VND cho " + category + ", vượt giới hạn " + limit + " VND.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager.notify(1, builder.build());
    }
}
