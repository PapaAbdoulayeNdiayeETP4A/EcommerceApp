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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dardev.R;
import com.dardev.databinding.PosterItemBinding;
import com.dardev.model.Poster;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.ViewHolder> {

    private Context context;
    private List<Poster> posters;
    private RecyclerView recyclerView;
    private PosterAdapterOnClickHandler clickHandler;

    /**
     * Interface for handling click events
     */
    public interface PosterAdapterOnClickHandler {
        void onClick(Poster poster);
    }

    public PosterAdapter(Context context, List<Poster> posters, PosterAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.posters = posters;
        this.clickHandler = clickHandler;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PosterItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.poster_item,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Poster poster = posters.get(position);
        holder.binding.title.setText(poster.getTitle());

        // Load image using Glide
        Glide.with(context)
                .load(poster.getImage())
                .placeholder(R.drawable.placeholder) // You need a placeholder drawable
                .into(holder.binding.posterImage);
    }

    @Override
    public int getItemCount() {
        return posters == null ? 0 : posters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final PosterItemBinding binding;

        public ViewHolder(PosterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            clickHandler.onClick(posters.get(position));
        }
    }
}