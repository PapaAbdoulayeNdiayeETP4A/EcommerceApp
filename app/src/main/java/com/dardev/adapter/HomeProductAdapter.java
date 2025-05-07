package com.dardev.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.dardev.R;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.model.Favorite;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;
import com.dardev.utils.RequestCallback;
import com.dardev.view.ShowProduct;

import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;

    public HomeProductAdapter(Context context, List<Product> productList,
                              AddFavoriteViewModel addFavoriteViewModel,
                              RemoveFavoriteViewModel removeFavoriteViewModel) {
        this.context = context;
        this.productList = productList;
        this.addFavoriteViewModel = addFavoriteViewModel;
        this.removeFavoriteViewModel = removeFavoriteViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText("₹" + product.getProductPrice());

        // Configuration du bouton favori
        holder.likeButton.setLiked(product.isFavourite() == 1);

        // Gestionnaire de clic sur le bouton favori
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (LoginUtils.getInstance(context).isLoggedIn()) {
                    int userId = LoginUtils.getInstance(context).getUserId();
                    Favorite favorite = new Favorite(userId, product.getProductId());

                    addFavoriteViewModel.addFavorite(favorite, new RequestCallback() {
                        @Override
                        public void onCallBack() {
                            product.setIsFavourite(true);
                        }
                    });
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (LoginUtils.getInstance(context).isLoggedIn()) {
                    int userId = LoginUtils.getInstance(context).getUserId();

                    removeFavoriteViewModel.removeFavorite(userId, product.getProductId(), new RequestCallback() {
                        @Override
                        public void onCallBack() {
                            product.setIsFavourite(false);
                        }
                    });
                }
            }
        });

        // Gestionnaire de clic sur la carte du produit
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProduct.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView productImage;
        TextView productName, productPrice;
        LikeButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.product_card);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            likeButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}