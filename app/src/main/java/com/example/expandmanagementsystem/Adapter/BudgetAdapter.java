package com.example.expandmanagementsystem.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expandmanagementsystem.R;
import com.example.expandmanagementsystem.model.Budget;

import java.util.ArrayList;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    private ArrayList<Budget> budgets; // Danh sách các ngân sách
    private double[] spentAmounts; // Mảng lưu số tiền đã chi tiêu cho từng ngân sách
    private OnItemClickListener listener; // Listener để xử lý sự kiện chỉnh sửa/xóa

    // Interface để xử lý sự kiện khi người dùng nhấn nút Edit hoặc Delete
    public interface OnItemClickListener {
        void onEditClick(Budget budget);
        void onDeleteClick(Budget budget);
    }

    // Constructor: Khởi tạo adapter với danh sách ngân sách, số tiền đã chi, và listener
    public BudgetAdapter(ArrayList<Budget> budgets, double[] spentAmounts, OnItemClickListener listener) {
        this.budgets = budgets;
        this.spentAmounts = spentAmounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng mục ngân sách (item_budget.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy dữ liệu ngân sách và số tiền đã chi tại vị trí hiện tại
        Budget budget = budgets.get(position);
        double spentAmount = spentAmounts[position];
        // Tính số tiền còn lại: Ngân sách - Số tiền đã chi
        double remainingAmount = budget.getAmount() - spentAmount;

        // Hiển thị thông tin ngân sách lên các TextView
        holder.categoryTextView.setText("Category: " + budget.getCategory());
        holder.monthYearTextView.setText("Month/Year: " + budget.getMonth() + "/" + budget.getYear());
        holder.budgetAmountTextView.setText(String.format("Budget: $%.2f", budget.getAmount()));
        holder.spentAmountTextView.setText(String.format("Spent: $%.2f", spentAmount));
        holder.remainingAmountTextView.setText(String.format("Remaining: $%.2f", remainingAmount));

        // Đổi màu chữ "Remaining" nếu vượt ngân sách (âm)
        if (remainingAmount < 0) {
            holder.remainingAmountTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.remainingAmountTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }

        // Xử lý sự kiện khi nhấn nút Edit: Gọi listener để mở dialog chỉnh sửa
        holder.editButton.setOnClickListener(v -> listener.onEditClick(budget));
        // Xử lý sự kiện khi nhấn nút Delete: Gọi listener để xóa ngân sách
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(budget));
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng mục trong danh sách ngân sách
        return budgets.size();
    }

    // ViewHolder: Lớp lưu trữ các view của một mục ngân sách
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, monthYearTextView, budgetAmountTextView, spentAmountTextView, remainingAmountTextView;
        Button editButton, deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            // Khởi tạo các view từ layout item_budget.xml
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            monthYearTextView = itemView.findViewById(R.id.monthYearTextView);
            budgetAmountTextView = itemView.findViewById(R.id.budgetAmountTextView);
            spentAmountTextView = itemView.findViewById(R.id.spentAmountTextView);
            remainingAmountTextView = itemView.findViewById(R.id.remainingAmountTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}