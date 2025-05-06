package com.dardev.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dardev.R;
import com.dardev.databinding.OrderItemBinding;
import com.dardev.model.Order;
import com.dardev.view.OrderDetailsActivity;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private RecyclerView recyclerView;

    public OrderAdapter(RecyclerView recyclerView, Context context, List<Order> orders) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.orders = orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.order_item,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        // Set order ID
        holder.binding.orderId.setText("Order #" + order.getId());

        // Set order date
        holder.binding.orderDate.setText(order.getCreatedAt());

        // Set order status
        holder.binding.orderStatus.setText(order.getStatus());

        // Set order total
        holder.binding.orderTotal.setText("$" + order.getTotalPrice());

        // You can add additional details if needed
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OrderItemBinding binding;

        public ViewHolder(OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = recyclerView.getChildLayoutPosition(view);

            // Navigate to order details
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("ORDER_ID", orders.get(position).getId());
            context.startActivity(intent);
        }
    }
}