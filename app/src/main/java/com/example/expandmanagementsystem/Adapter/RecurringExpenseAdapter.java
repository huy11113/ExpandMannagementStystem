package com.example.expandmanagementsystem.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expandmanagementsystem.R;
import com.example.expandmanagementsystem.model.RecurringExpense;

import java.util.ArrayList;

public class RecurringExpenseAdapter extends RecyclerView.Adapter<RecurringExpenseAdapter.ViewHolder> {

    private ArrayList<RecurringExpense> recurringExpenses; // Danh sách các chi tiêu định kỳ
    private final OnItemClickListener listener; // Listener để xử lý sự kiện chỉnh sửa/xóa

    // Interface để xử lý sự kiện khi người dùng nhấn nút Edit hoặc Delete
    public interface OnItemClickListener {
        void onEditClick(RecurringExpense expense);
        void onDeleteClick(RecurringExpense expense);
    }

    // Constructor: Khởi tạo adapter với danh sách chi tiêu định kỳ và listener
    public RecurringExpenseAdapter(ArrayList<RecurringExpense> recurringExpenses, OnItemClickListener listener) {
        // Kiểm tra null để đảm bảo danh sách không null, nếu null thì tạo danh sách rỗng
        this.recurringExpenses = recurringExpenses != null ? recurringExpenses : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng mục chi tiêu định kỳ (item_recurring_expense.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recurring_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy chi tiêu định kỳ tại vị trí hiện tại
        RecurringExpense expense = recurringExpenses.get(position);

        // Hiển thị thông tin chi tiêu định kỳ lên các TextView
        holder.descriptionTextView.setText(expense.getDescription());
        // Định dạng số tiền với dấu phân cách hàng nghìn, không hiển thị phần thập phân
        holder.amountTextView.setText(String.format("%,.0f VNĐ", expense.getAmount()));
        holder.categoryTextView.setText(expense.getCategory());
        // Hiển thị khoảng thời gian (ngày bắt đầu - ngày kết thúc)
        holder.dateRangeTextView.setText(expense.getStartDate() + " - " + expense.getEndDate());
        holder.frequencyTextView.setText(expense.getFrequency());

        // Xử lý sự kiện khi nhấn nút Edit: Gọi listener để mở dialog chỉnh sửa
        holder.editButton.setOnClickListener(v -> listener.onEditClick(expense));
        // Xử lý sự kiện khi nhấn nút Delete: Gọi listener để xóa chi tiêu định kỳ
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(expense));
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng mục trong danh sách chi tiêu định kỳ
        return recurringExpenses.size();
    }

    // Cập nhật dữ liệu mới cho adapter
    public void updateData(ArrayList<RecurringExpense> newExpenses) {
        // Xóa dữ liệu cũ trong danh sách
        this.recurringExpenses.clear();
        // Thêm dữ liệu mới, nếu null thì thêm danh sách rỗng
        this.recurringExpenses.addAll(newExpenses != null ? newExpenses : new ArrayList<>());
        // Thông báo RecyclerView cập nhật giao diện
        notifyDataSetChanged();
    }

    // ViewHolder: Lớp lưu trữ các view của một mục chi tiêu định kỳ
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView, amountTextView, categoryTextView, dateRangeTextView, frequencyTextView;
        Button editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            // Khởi tạo các view từ layout item_recurring_expense.xml
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
            frequencyTextView = itemView.findViewById(R.id.frequencyTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}