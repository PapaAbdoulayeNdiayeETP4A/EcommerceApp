package com.dardev.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dardev.R;
import com.dardev.ViewModel.AddToCartViewModel;
import com.dardev.model.Cart;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;
import com.dardev.utils.RequestCallback;

import java.util.List;

import okhttp3.ResponseBody;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;
    private OnRemoveFromWishlistListener removeListener;
    private AddToCartViewModel addToCartViewModel;

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

        // Initialiser le ViewModel pour Add to Cart
        if (context instanceof ViewModelStoreOwner) {
            this.addToCartViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                    .get(AddToCartViewModel.class);
        }
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
        holder.productPriceTextView.setText(product.getProductPrice() + " FCFA");

        // Load product image with Glide
        Glide.with(context)
                .load(product.getProductImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.productImageView);

        // Add to cart button click - Implémentation complète
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart(product, holder.addToCartButton);
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

    private void addProductToCart(Product product, Button button) {
        // Vérifier si l'utilisateur est connecté
        if (!LoginUtils.getInstance(context).isLoggedIn()) {
            Toast.makeText(context, "Veuillez vous connecter pour ajouter au panier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le produit est disponible
        if (product == null) {
            Toast.makeText(context, "Produit non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = LoginUtils.getInstance(context).getUserId();
        if (userId == -1) {
            Toast.makeText(context, "Erreur de session utilisateur", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer l'objet Cart
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(product.getProductId());
        cart.setQuantity(1); // Quantité par défaut

        // Désactiver temporairement le bouton pour éviter les clics multiples
        button.setEnabled(false);
        button.setText("Adding...");

        // Appeler l'API via le ViewModel si disponible
        if (addToCartViewModel != null && context instanceof LifecycleOwner) {
            addToCartViewModel.addToCart(cart, new RequestCallback() {
                @Override
                public void onCallBack() {
                    // Réactiver le bouton
                    button.setEnabled(true);
                    button.setText("Add to Cart");

                    Toast.makeText(context, "Produit ajouté au panier!", Toast.LENGTH_SHORT).show();

                    // Animation de succès
                    animateAddToCartSuccess(button);
                }

                @Override
                public void onError(String error) {
                    // Réactiver le bouton
                    button.setEnabled(true);
                    button.setText("Add to Cart");

                    Toast.makeText(context, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                }
            }).observe((LifecycleOwner) context, new Observer<ResponseBody>() {
                @Override
                public void onChanged(ResponseBody responseBody) {
                    // Cette méthode sera appelée quand la réponse est reçue
                    if (responseBody == null) {
                        // Erreur - réactiver le bouton
                        button.setEnabled(true);
                        button.setText("Add to Cart");
                    }
                }
            });
        } else {
            // Fallback si le ViewModel n'est pas disponible
            button.setEnabled(true);
            button.setText("Add to Cart");
            Toast.makeText(context, "Service non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateAddToCartSuccess(Button button) {
        // Animation simple pour indiquer le succès
        button.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        button.setText("Added ✓");

        // Remettre l'apparence normale après 2 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setBackgroundResource(R.drawable.continue_background); // Utilisez votre background personnalisé
                button.setText("Add to Cart");
            }
        }, 2000);
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
            addToCartButton = itemView.findViewById(R.id.add_whislist_to_cart_button); // Correction de l'ID
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