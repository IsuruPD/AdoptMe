package com.s92066379.adoptme.categories;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.s92066379.adoptme.R
import com.s92066379.adoptme.adapters.ListingsAdapter
import com.s92066379.adoptme.data.Listing
import com.s92066379.adoptme.databinding.ActivityCategoryBirdsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryBirds : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBirdsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListingsAdapter
    private val listings = mutableListOf<Listing>()
    private val listingIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBirdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerBirds)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListingsAdapter(listings, listingIds)
        recyclerView.adapter = adapter

        binding.btnBackBirds.setOnClickListener { onBackPressed() }

        fetchListingsFromFirestore()
    }

    private fun fetchListingsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("general_listings")
            .whereEqualTo("category", "Birds")
            .whereEqualTo("status", "Pending")
            .addSnapshotListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                listings.clear()
                listingIds.clear()
                if (queryDocumentSnapshots != null) {
                    for (doc in queryDocumentSnapshots) {
                        val listing = doc.toObject(Listing::class.java)
                        listing?.let {
                            listings.add(it)
                            listingIds.add(doc.id)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}