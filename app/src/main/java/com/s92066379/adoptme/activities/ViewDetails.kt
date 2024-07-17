package com.s92066379.adoptme.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.s92066379.adoptme.R
import com.s92066379.adoptme.data.Listing
import com.s92066379.adoptme.databinding.ActivityViewDetailsBinding
import com.s92066379.adoptme.firebase.FirebaseListingHelper
import java.text.SimpleDateFormat
import java.util.Locale

class ViewDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityViewDetailsBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listing = intent.getParcelableExtra<Listing>("listing")
        val listingId = intent.getStringExtra("Listing_ID")

        listing?.let { displayListingDetails(it) }

        binding.btnBackView.setOnClickListener { onBackPressed() }

        binding.imgAdoptButton.setOnClickListener {
            if (listingId != null) {
                confirmAdoption(listingId)
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.locationView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun displayListingDetails(listing: Listing) {
        binding.nameView.text = listing.name
        binding.breedView.text = listing.breed
        binding.ageView.text = listing.age.toString()
        binding.vaccinationView.text = listing.vaccinationStatus
        binding.descriptionView.text = listing.description
        binding.contactView.text = listing.contact.toString()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(listing.timestamp.toDate())
        binding.timestampView.text = formattedDate

        Glide.with(this)
            .load(listing.imageUrl)
            .into(binding.imageView)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val listing = intent.getParcelableExtra<Listing>("listing")
        listing?.let {
            val location = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title(it.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun confirmAdoption(listingId: String) {
        AlertDialog.Builder(this)
            .setMessage("Do you want to adopt this animal?")
            .setPositiveButton("Yes") { _, _ ->
                updateListingStatus(listingId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateListingStatus(listingId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("general_listings").document(listingId)
            .update("status", "Adopted")
            .addOnSuccessListener {
                Toast.makeText(this, "Congrats on helping out a pookie!", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Adoption failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}