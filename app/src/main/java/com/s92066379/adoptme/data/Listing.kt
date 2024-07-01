package com.s92066379.adoptme.data

data class Listing (
    val userId: String,
    val imageUrl: String,
    val name: String,
    val category: String,
    val breed: String,
    val age: Int,
    val vaccinationStatus: String,
    val description: String,
    val contact: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending"
)



