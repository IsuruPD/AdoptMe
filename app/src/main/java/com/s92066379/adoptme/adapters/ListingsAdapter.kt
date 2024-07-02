package com.s92066379.adoptme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s92066379.adoptme.R
import com.s92066379.adoptme.data.Listing
import java.text.SimpleDateFormat
import java.util.Locale

class ListingsAdapter(private val listings: List<Listing>) : RecyclerView.Adapter<ListingsAdapter.ListingViewHolder>() {

    inner class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val dateTextView: TextView = itemView.findViewById(R.id.timestamp)
        //val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val breedTextView: TextView = itemView.findViewById(R.id.breed)
        //val ageTextView: TextView = itemView.findViewById(R.id.ageTextView)
        //val vaccinationStatusTextView: TextView = itemView.findViewById(R.id.vaccinationStatusTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)

        fun bind(listing: Listing) {
            nameTextView.text = listing.name
            //categoryTextView.text = listing.category
            breedTextView.text = listing.breed
            val date = listing.timestamp.toDate()
            // Format the date
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(date)
            dateTextView.text = formattedDate


            //ageTextView.text = listing.age.toString()
            //vaccinationStatusTextView.text = listing.vaccinationStatus
            descriptionTextView.text = listing.description

            Glide.with(itemView.context)
                .load(listing.imageUrl)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_row, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(listings[position])
    }

    override fun getItemCount(): Int = listings.size
}
