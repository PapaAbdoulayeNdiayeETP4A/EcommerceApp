package com.dardev.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dardev.R;
import com.dardev.databinding.HistoryItemBinding;
import com.dardev.model.Product;
//import com.dardev.view.ProductDetailsActivity;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<Product> historyProducts;
    private RecyclerView recyclerView;

    public HistoryAdapter(RecyclerView recyclerView, Context context, List<Product> historyProducts) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.historyProducts = historyProducts;
    }

    public void setHistoryProducts(List<Product> historyProducts) {
        this.historyProducts = historyProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HistoryItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.history_item,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = historyProducts.get(position);

        // Set product name
        holder.binding.productName.setText(product.getProductName());

        // Set product price
        holder.binding.productPrice.setText("$" + product.getProductPrice());

        // Load product image using Glide
        Glide.with(context)
                .load(product.getProductImage())
                .placeholder(R.drawable.placeholder) // You need a placeholder drawable
                .into(holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return historyProducts == null ? 0 : historyProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HistoryItemBinding binding;

        public ViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            int position = recyclerView.getChildLayoutPosition(view);
//
//            // Navigate to product details
//            Intent intent = new Intent(context, ProductDetailsActivity.class);
//            intent.putExtra("PRODUCT", historyProducts.get(position));
//            context.startActivity(intent);
        }
    }
}