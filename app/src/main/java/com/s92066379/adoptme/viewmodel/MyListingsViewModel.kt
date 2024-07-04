package com.s92066379.adoptme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.s92066379.adoptme.data.Listing
import com.s92066379.adoptme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MyListingsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _listings = MutableStateFlow<Resource<List<Listing>>>(Resource.Unspecified())
    val listings = _listings.asStateFlow()
    val listingIds = mutableListOf<String>()

    init {
        fetchUserListings()
    }

    private fun fetchUserListings() {
        viewModelScope.launch {
            _listings.emit(Resource.Loading())
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val querySnapshot = firestore.collection("general_listings")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val listings = querySnapshot.documents.mapNotNull { document ->
                    val listing = document.toObject(Listing::class.java)
                    listingIds.add(document.id)
                    listing
                }
                _listings.emit(Resource.Success(listings))
            } catch (e: Exception) {
                _listings.emit(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
}
