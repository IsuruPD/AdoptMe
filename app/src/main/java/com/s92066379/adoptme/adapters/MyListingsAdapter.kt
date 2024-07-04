package com.s92066379.adoptme.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.s92066379.adoptme.R
import com.s92066379.adoptme.activities.EditMyListings
import com.s92066379.adoptme.activities.ViewDetails
import com.s92066379.adoptme.data.Listing
import java.text.SimpleDateFormat
import java.util.Locale

class MyListingsAdapter(private val listings: List<Listing>, private val listingIds: List<String>) : RecyclerView.Adapter<MyListingsAdapter.MyListingsViewHolder>() {

    inner class MyListingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val dateTextView: TextView = itemView.findViewById(R.id.timestamp)
        //val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val breedTextView: TextView = itemView.findViewById(R.id.breed)
        //val ageTextView: TextView = itemView.findViewById(R.id.ageTextView)
        //val vaccinationStatusTextView: TextView = itemView.findViewById(R.id.vaccinationStatusTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)

        fun bind(listing: Listing, listingId: String) {
            nameTextView.text = listing.name
            breedTextView.text = listing.breed
            val date = listing.timestamp.toDate()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(date)
            dateTextView.text = formattedDate
            descriptionTextView.text = listing.description

            Glide.with(itemView.context)
                .load(listing.imageUrl)
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, EditMyListings::class.java)
                intent.putExtra("listing", listing)
                intent.putExtra("Listing_ID", listingId)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_row, parent, false)
        return MyListingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyListingsViewHolder, position: Int) {
        holder.bind(listings[position], listingIds[position])
    }

    override fun getItemCount(): Int = listings.size
}
