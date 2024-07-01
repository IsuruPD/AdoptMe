package com.s92066379.adoptme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.s92066379.adoptme.R;
import com.s92066379.adoptme.models.Listing;

import java.util.List;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {

    private List<Listing> listings;

    public ListingsAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.listingNumber.setText(String.valueOf(position + 1));
        holder.petName.setText(listing.getPetName());
        holder.subTitle.setText(listing.getSubTitle());
        holder.timestamp.setText(listing.getTimestamp());
        holder.description.setText(listing.getDescription());
        holder.contact.setText(listing.getContact());
        Glide.with(holder.itemView.getContext()).load(listing.getImageUrl()).into(holder.listingImage);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView listingNumber, petName, subTitle, timestamp, description, contact;
        ImageView listingImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listingNumber = itemView.findViewById(R.id.listingNumber);
            petName = itemView.findViewById(R.id.petName);
            subTitle = itemView.findViewById(R.id.subTitle);
            timestamp = itemView.findViewById(R.id.timestamp);
            description = itemView.findViewById(R.id.description);
            contact = itemView.findViewById(R.id.contact);
            listingImage = itemView.findViewById(R.id.listingImage);
        }
    }
}
