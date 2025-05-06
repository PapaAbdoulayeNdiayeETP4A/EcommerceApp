package com.dardev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dardev.R;
import com.dardev.databinding.ShippingAddressItemBinding;
import com.dardev.model.Shipping;

import java.util.List;

public class ShippingAdapter extends RecyclerView.Adapter<ShippingAdapter.ViewHolder> {

    private Context context;
    private List<Shipping> shippingAddresses;
    private int selectedPosition = -1;
    private ShippingAdapterOnClickHandler clickHandler;

    /**
     * Interface for handling click events
     */
    public interface ShippingAdapterOnClickHandler {
        void onClick(Shipping shippingAddress);
    }

    public ShippingAdapter(Context context, List<Shipping> shippingAddresses, ShippingAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.shippingAddresses = shippingAddresses;
        this.clickHandler = clickHandler;
    }

    public void setShippingAddresses(List<Shipping> shippingAddresses) {
        this.shippingAddresses = shippingAddresses;
        notifyDataSetChanged();
    }

    public Shipping getSelectedShippingAddress() {
        if (selectedPosition >= 0 && selectedPosition < shippingAddresses.size()) {
            return shippingAddresses.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShippingAddressItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.shipping_address_item,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shipping shippingAddress = shippingAddresses.get(position);

        // Set name
        holder.binding.name.setText(shippingAddress.getName());

        // Set address
        String fullAddress = shippingAddress.getAddress() + ", " +
                shippingAddress.getCity() + ", " +
                shippingAddress.getCountry() + ", " +
                shippingAddress.getPostalCode();
        holder.binding.address.setText(fullAddress);

        // Set phone
        holder.binding.phone.setText(shippingAddress.getPhone());

        // Set selected state
        holder.binding.radioButton.setChecked(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return shippingAddresses == null ? 0 : shippingAddresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ShippingAddressItemBinding binding;

        public ViewHolder(ShippingAddressItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
            binding.radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            // Update selected position
            selectedPosition = position;
            notifyDataSetChanged();

            // Call the click handler
            clickHandler.onClick(shippingAddresses.get(position));
        }
    }
}