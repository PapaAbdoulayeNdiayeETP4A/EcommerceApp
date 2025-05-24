package com.dardev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dardev.R;
import com.dardev.model.Product;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;
    private OnRemoveFromWishlistListener removeListener;

    // Interface for click listener
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Interface for remove from wishlist listener
    public interface OnRemoveFromWishlistListener {
        void onRemoveFromWishlist(Product product, int position);
    }

    public WishlistAdapter(Context context, List<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    // Setter pour le listener de suppression
    public void setOnRemoveFromWishlistListener(OnRemoveFromWishlistListener removeListener) {
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText("$" + product.getProductPrice());

        // Load product image with Glide
        Glide.with(context)
                .load(product.getProductImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.productImageView);

        // Add to cart button click
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Added to cart: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                // Implement actual add to cart functionality here
            }
        });

        // Remove from wishlist button click
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && removeListener != null) {
                    // Appeler le listener pour supprimer de la base de données
                    removeListener.onRemoveFromWishlist(product, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // Méthode pour supprimer un élément après confirmation de suppression de la DB
    public void removeItem(int position) {
        if (position >= 0 && position < products.size()) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView productImageView;
        public TextView productNameTextView;
        public TextView productPriceTextView;
        public Button addToCartButton;
        public ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
            removeButton = itemView.findViewById(R.id.remove_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onProductClick(products.get(position));
            }
        }
    }
}