package com.s92066379.adoptme.activities

import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.s92066379.adoptme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gmap: GoogleMap
    private lateinit var searchView: SearchView
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView = findViewById(R.id.search_view)

        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Make the entire search bar clickable
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(location, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                addMarker(latLng, location)
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String) {
        currentMarker?.remove()
        currentMarker = gmap.addMarker(MarkerOptions().position(latLng).title(title))
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        val location = LatLng(6.9271, 79.8612)
        addMarker(location, "Colombo")
        gmap.uiSettings.isZoomControlsEnabled = true

        gmap.setOnMapClickListener { latLng ->
            addMarker(latLng,"")
        }
    }
}
