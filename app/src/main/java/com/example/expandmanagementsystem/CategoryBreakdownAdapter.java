package com.example.expandmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Lớp CategoryBreakdownAdapter: Adapter cho RecyclerView để hiển thị danh sách phân tích chi tiêu theo danh mục
public class CategoryBreakdownAdapter extends RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder> {
    // Danh sách các đối tượng CategoryBreakdown để hiển thị
    private ArrayList<CategoryBreakdown> categoryBreakdownList;

    // DecimalFormat: Định dạng số tiền với dấu phân cách hàng nghìn và 2 chữ số thập phân (ví dụ: 1,234.56)
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    // Constructor: Khởi tạo adapter với danh sách phân tích chi tiêu
    public CategoryBreakdownAdapter(ArrayList<CategoryBreakdown> categoryBreakdownList) {
        this.categoryBreakdownList = categoryBreakdownList; // Gán danh sách dữ liệu
    }

    // onCreateViewHolder: Tạo ViewHolder mới bằng cách inflate layout cho từng item
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout item_category_breakdown.xml cho mỗi mục trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_breakdown, parent, false);
        return new ViewHolder(view); // Trả về ViewHolder với view vừa inflate
    }

    // onBindViewHolder: Gắn dữ liệu từ CategoryBreakdown vào ViewHolder tại vị trí position
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Lấy đối tượng CategoryBreakdown tại vị trí hiện tại
        CategoryBreakdown breakdown = categoryBreakdownList.get(position);

        // Gán tên danh mục vào TextView
        holder.categoryTextView.setText(breakdown.getCategory());

        // Định dạng số tiền với currencyFormat và thêm ký hiệu "$" trước khi gán vào TextView
        holder.amountTextView.setText("$" + currencyFormat.format(breakdown.getAmount()));
    }

    // getItemCount: Trả về số lượng mục trong danh sách
    @Override
    public int getItemCount() {
        return categoryBreakdownList.size(); // Kích thước danh sách quyết định số item hiển thị
    }

    // Lớp ViewHolder: Lưu trữ các view của một item để tái sử dụng, tránh findViewById lặp lại
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, amountTextView; // TextView cho danh mục và số tiền

        // Constructor: Khởi tạo ViewHolder và ánh xạ các view từ layout
        ViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView); // Ánh xạ TextView cho danh mục
            amountTextView = itemView.findViewById(R.id.amountTextView); // Ánh xạ TextView cho số tiền
        }
    }
}