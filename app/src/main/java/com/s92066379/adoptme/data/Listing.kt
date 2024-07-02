package com.s92066379.adoptme.data

import com.google.firebase.Timestamp

data class Listing(
    val userId: String = "",
    val imageUrl: String = "",
    val name: String = "",
    val category: String = "",
    val breed: String = "",
    val age: Int = 0,
    val vaccinationStatus: String = "",
    val description: String = "",
    val contact: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    val status: String = "Pending"
)
