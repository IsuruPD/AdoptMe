package com.s92066379.adoptme.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.s92066379.adoptme.adapters.MyListingsAdapter
import com.s92066379.adoptme.databinding.ActivityMyListingsBinding
import com.s92066379.adoptme.util.Resource
import com.s92066379.adoptme.viewmodel.MyListingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyListings : AppCompatActivity() {

    private lateinit var binding: ActivityMyListingsBinding
    private val viewModel by viewModels<MyListingsViewModel>()
    private lateinit var listingsAdapter: MyListingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listingsAdapter = MyListingsAdapter(emptyList(), emptyList())
        setupRecyclerView()

        binding.btnBackPreviousListings.setOnClickListener {
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.listings.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val listings = resource.data ?: emptyList()
                        val listingIds = viewModel.listingIds
                        listingsAdapter = MyListingsAdapter(listings, listingIds)
                        binding.recyclerviewPrevious.adapter = listingsAdapter
                    }

                    is Resource.Error -> {
                        Toast.makeText(this@MyListings, resource.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerviewPrevious.apply {
            layoutManager = LinearLayoutManager(this@MyListings)
            adapter = listingsAdapter
        }
    }
}
