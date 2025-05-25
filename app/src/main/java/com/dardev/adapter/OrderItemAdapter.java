package com.dardev.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dardev.R;
import com.dardev.model.OrderItem; // Importez OrderItem

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;

    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_detail_item, parent, false); // Créez ce layout XML
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.productNameTextView.setText("Produit ID: " + item.getProductId()); // Idéalement, affichez le nom du produit
        holder.quantityTextView.setText("Quantité: " + item.getQuantity());
        holder.priceTextView.setText(String.format("Prix: $%.2f", item.getPrice()));
        holder.totalItemPriceTextView.setText(String.format("Sous-total: $%.2f", item.getPrice() * item.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return orderItems == null ? 0 : orderItems.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView totalItemPriceTextView; // Pour le sous-total de l'article

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.order_item_product_name); // Assurez-vous que ces IDs existent dans order_item_detail_item.xml
            quantityTextView = itemView.findViewById(R.id.order_item_quantity);
            priceTextView = itemView.findViewById(R.id.order_item_price);
            totalItemPriceTextView = itemView.findViewById(R.id.order_item_total_price);
        }
    }
}