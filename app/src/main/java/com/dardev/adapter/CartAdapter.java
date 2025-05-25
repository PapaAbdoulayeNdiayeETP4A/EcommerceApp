package com.dardev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.CartViewModel;
import com.dardev.ViewModel.FromCartViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.databinding.CartItemBinding;
import com.dardev.model.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>
{
    Context context;
    RecyclerView recyclerView;
    ArrayList<String> imageUrls; // Changé de Integer à String pour les URLs
    ArrayList<String> title;
    ArrayList<String> price;

    private List<Product> productsInCart;
    private OnItemDeleteListener deleteListener;

    private CartAdapter.CartAdapterOnClickHandler clickHandler;

    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;
    private FromCartViewModel fromCartViewModel;

    /**
     * The interface that receives onClick messages.
     */
    public interface CartAdapterOnClickHandler
    {
        void onClick(Product product);
    }

    /**
     * Interface pour gérer la suppression d'éléments
     */
    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    // Constructeur modifié pour accepter les URLs d'images
    public CartAdapter(RecyclerView recyclerView, Context context, ArrayList<String> imageUrls, ArrayList<String> title, ArrayList<String> price)
    {
        this.recyclerView = recyclerView;
        this.context = context;
        this.imageUrls = imageUrls;
        this.title = title;
        this.price = price;

        addFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(RemoveFavoriteViewModel.class);
        fromCartViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FromCartViewModel.class);
    }

    // Constructeur avec listener de suppression
    public CartAdapter(RecyclerView recyclerView, Context context, ArrayList<String> imageUrls, ArrayList<String> title, ArrayList<String> price, OnItemDeleteListener deleteListener)
    {
        this.recyclerView = recyclerView;
        this.context = context;
        this.imageUrls = imageUrls;
        this.title = title;
        this.price = price;
        this.deleteListener = deleteListener;

        addFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(RemoveFavoriteViewModel.class);
        fromCartViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FromCartViewModel.class);
    }

    // Constructeur alternatif qui accepte encore les Integer pour la compatibilité
    public CartAdapter(RecyclerView recyclerView, Context context, ArrayList<Integer> images, ArrayList<String> title, ArrayList<String> price, boolean useDrawableResources)
    {
        this.recyclerView = recyclerView;
        this.context = context;
        this.title = title;
        this.price = price;

        // Convertir les Integer en String pour la compatibilité
        this.imageUrls = new ArrayList<>();
        if (images != null) {
            for (Integer imageRes : images) {
                this.imageUrls.add(String.valueOf(imageRes));
            }
        }

        addFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(RemoveFavoriteViewModel.class);
        fromCartViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FromCartViewModel.class);
    }

    public void update(String imageUrl, String titles, String prices)
    {
        imageUrls.add(imageUrl);
        title.add(titles);
        price.add(prices);

        notifyDataSetChanged();  // refreshes the recycler view automatically
    }

    // Méthode pour la compatibilité avec les anciennes ressources drawable
    public void update(Integer imageRes, String titles, String prices)
    {
        imageUrls.add(String.valueOf(imageRes));
        title.add(titles);
        price.add(prices);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        CartItemBinding cartListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_item, parent, false);
        return new ViewHolder(cartListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.binding.title.setText(title.get(position));
        holder.binding.price.setText(price.get(position));

        // Charger l'image selon le type (URL ou ressource drawable)
        String imageSource = imageUrls.get(position);

        try {
            // Vérifier si c'est une URL (commence par http) ou une ressource drawable
            if (imageSource.startsWith("http") || imageSource.startsWith("https")) {
                // C'est une URL, utiliser Glide pour charger l'image depuis Internet
                Glide.with(context)
                        .load(imageSource)
                        .placeholder(R.drawable.shoes1) // Image de chargement
                        .error(R.drawable.shoes1) // Image en cas d'erreur
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.binding.image);
            } else {
                // C'est probablement une ressource drawable (pour la compatibilité)
                try {
                    int drawableId = Integer.parseInt(imageSource);
                    holder.binding.image.setImageResource(drawableId);
                } catch (NumberFormatException e) {
                    // Si ce n'est pas un nombre, essayer de charger comme URL quand même
                    Glide.with(context)
                            .load(imageSource)
                            .placeholder(R.drawable.shoes1)
                            .error(R.drawable.shoes1)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.binding.image);
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser l'image par défaut
            holder.binding.image.setImageResource(R.drawable.shoes1);
        }

        // Configurer le bouton de suppression
        ImageView deleteIcon = holder.itemView.findViewById(R.id.delete_icon);

        if (deleteIcon != null) {
            deleteIcon.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onItemDelete(position);
                } else {
                    removeItem(position);
                }
            });
        }
    }

    /**
     * Supprimer un élément de la liste
     */
    public void removeItem(int position) {
        if (position >= 0 && position < imageUrls.size()) {
            imageUrls.remove(position);
            title.remove(position);
            price.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    @Override
    public int getItemCount()
    {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title,price;
        ImageView image;
        ImageView deleteIcon;
        private final CartItemBinding binding;

        public ViewHolder(CartItemBinding binding)
        {
            super(binding.getRoot());
            View itemView = binding.getRoot();

            this.binding = binding;

            title = binding.title;
            price = binding.price;
            image = binding.image;

            // Chercher l'icône de suppression dans le layout
            deleteIcon = findDeleteIcon(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = recyclerView.getChildLayoutPosition(view);

                    Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();

                    // Intent intent = new Intent(context,address.class);
                    //    context.startActivity(intent);
                }
            });
        }

        private ImageView findDeleteIcon(View itemView) {
            // Méthode pour trouver l'icône de suppression dans la hiérarchie des vues
            if (itemView instanceof ViewGroup) {
                return findDeleteIconRecursive((ViewGroup) itemView);
            }
            return null;
        }

        private ImageView findDeleteIconRecursive(ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    // Vérifier si c'est l'icône de suppression (par sa taille ou drawable)
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    if (params.width == 50 * (int) context.getResources().getDisplayMetrics().density ||
                            params.height == 20 * (int) context.getResources().getDisplayMetrics().density) {
                        return imageView;
                    }
                } else if (child instanceof ViewGroup) {
                    ImageView result = findDeleteIconRecursive((ViewGroup) child);
                    if (result != null) return result;
                }
            }
            return null;
        }
    }
}