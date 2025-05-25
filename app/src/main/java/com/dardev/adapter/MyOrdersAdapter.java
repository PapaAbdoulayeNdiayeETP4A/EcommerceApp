package com.dardev.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.databinding.MyOrdersItemBinding;
import com.dardev.model.Order; // Importez la classe Order
import com.dardev.model.OrderItem; // Importez OrderItem si vous voulez afficher les articles
import com.dardev.view.OrderDetailsActivity;
import com.bumptech.glide.Glide; // Importez Glide pour charger les images

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    Context context;
    RecyclerView recyclerView;
    private List<Order> orders; // Changement ici: liste d'objets Order
    private OrderAdapterOnClickHandler clickHandler; // Interface pour le clic sur les éléments

    /**
     * The interface that receives onClick messages.
     */
    public interface OrderAdapterOnClickHandler {
        void onClick(Order order);
    }

    // Constructeur mis à jour
    public MyOrdersAdapter(RecyclerView recyclerView, Context context, List<Order> orders, OrderAdapterOnClickHandler clickHandler) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.orders = orders;
        this.clickHandler = clickHandler;
    }

    // Nouvelle méthode pour mettre à jour la liste des commandes
    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyOrdersItemBinding myOrdersItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.my_orders_item, // Assurez-vous que ce layout correspond à l'élément de liste de commande
                parent,
                false);
        return new ViewHolder(myOrdersItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        // Mettez à jour les vues avec les données de l'objet Order
        holder.myOrdersItemBinding.orderIdTextView.setText("Commande ID: " + order.getId());
        holder.myOrdersItemBinding.orderDateTextView.setText("Date: " + order.getCreatedAt());
        holder.myOrdersItemBinding.orderStatusTextView.setText("Statut: " + order.getStatus());
        holder.myOrdersItemBinding.orderTotalPriceTextView.setText(String.format("Total: $%.2f", order.getTotalPrice()));

        // Charger l'image du premier article de la commande si disponible
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            // Assurez-vous que OrderItem a une URL d'image de produit ou un moyen de la récupérer
            // Pour l'instant, je vais supposer que Product.java a une image.
            // Idéalement, votre OrderItem devrait contenir les détails complets du produit.
            // Si votre OrderItem ne contient que productId, vous devrez faire une autre requête API ou charger l'image différemment.
            // Pour l'exemple, nous allons charger une image générique ou la première image du produit si elle est disponible dans OrderItem
            // Si OrderItem contient 'imageUrl', utilisez-le directement :
            // Glide.with(context).load(firstItem.getImageUrl()).into(holder.myOrdersItemBinding.itemImageView);
            // Si OrderItem ne contient pas l'image, vous devrez adapter cette partie.
            // Pour cet exemple, je vais juste définir une image par défaut ou gérer son absence.
            Glide.with(context)
                    .load(R.drawable.placeholder_image) // Remplacez par une image de placeholder réelle
                    .into(holder.myOrdersItemBinding.itemImageView);
            // Ou si votre OrderItem inclut productName et vous avez des images locales basées sur le nom:
            // holder.myOrdersItemBinding.itemImageView.setImageResource(context.getResources().getIdentifier(firstItem.getProductName().toLowerCase(), "drawable", context.getPackageName()));

        } else {
            // Pas d'articles, définir une image de placeholder
            Glide.with(context)
                    .load(R.drawable.placeholder_image) // Image de placeholder
                    .into(holder.myOrdersItemBinding.itemImageView);
        }

    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MyOrdersItemBinding myOrdersItemBinding;

        public ViewHolder(MyOrdersItemBinding myOrdersItemBinding) {
            super(myOrdersItemBinding.getRoot());
            this.myOrdersItemBinding = myOrdersItemBinding;

            myOrdersItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickHandler != null) {
                        clickHandler.onClick(orders.get(position));
                    }
                }
            });
        }
    }
}