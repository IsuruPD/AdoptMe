package com.s92066379.adoptme.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.s92066379.adoptme.data.Listing
import java.util.UUID

class FirebaseListingHelper {
    private val auth = FirebaseAuth.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    fun uploadImageAndSaveListing(
        imageUri: Uri,
        name: String,
        category: String,
        breed: String,
        age: Int,
        vaccinationStatus: String,
        description: String,
        contact: Int,
        latitude: Double,
        longitude: Double,
        callback: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveListingToFirestore(userId, imageUrl, name, category, breed, age, vaccinationStatus, description, contact, latitude, longitude, callback)
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

    private fun saveListingToFirestore(
        userId: String,
        imageUrl: String,
        name: String,
        category: String,
        breed: String,
        age: Int,
        vaccinationStatus: String,
        description: String,
        contact: Int,
        latitude: Double,
        longitude: Double,
        callback: (Boolean, String?) -> Unit
    ) {
        val listing = Listing(userId, imageUrl, name, category, breed, age, vaccinationStatus, description, contact, latitude, longitude)
        val generalListingRef = firestore.collection("general_listings").document()

        generalListingRef.set(listing)
            .addOnSuccessListener {
                saveUserListingReference(userId, generalListingRef.id, callback)
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

    private fun saveUserListingReference(
        userId: String,
        listingId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        firestore.collection("users_listings").document(userId)
            .collection("listings").document(listingId).set(mapOf("exists" to true))
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

    fun uploadImageAndUpdateListing(
        listingId: String,
        uri: Uri,
        name: String,
        category: String,
        breed: String,
        age: Int,
        vaccinationStatus: String,
        description: String,
        contact: Int,
        latitude: Double,
        longitude: Double,
        callback: (Boolean, String) -> Unit
    ) {
        val storageRef = storageReference.child("images/${uri.lastPathSegment}")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUrl ->
                    updateListing(
                        listingId,
                        downloadUrl.toString(),
                        name,
                        category,
                        breed,
                        age,
                        vaccinationStatus,
                        description,
                        contact,
                        latitude,
                        longitude,
                        callback
                    )
                }
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Unknown error")
            }
    }

    private fun updateListing(
        listingId: String,
        imageUrl: String,
        name: String,
        category: String,
        breed: String,
        age: Int,
        vaccinationStatus: String,
        description: String,
        contact: Int,
        latitude: Double,
        longitude: Double,
        callback: (Boolean, String) -> Unit
    ) {
        val listing = hashMapOf(
            "imageUrl" to imageUrl,
            "name" to name,
            "category" to category,
            "breed" to breed,
            "age" to age,
            "vaccinationStatus" to vaccinationStatus,
            "description" to description,
            "contact" to contact,
            "latitude" to latitude,
            "longitude" to longitude
        )

        firestore.collection("listings").document(listingId)
            .set(listing)
            .addOnSuccessListener {
                callback(true, "Listing updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Unknown error")
            }
    }

    fun updateListingWithoutImage(
        listingId: String,
        name: String,
        category: String,
        breed: String,
        age: Int,
        vaccinationStatus: String,
        description: String,
        contact: Int,
        latitude: Double,
        longitude: Double,
        callback: (Boolean, String) -> Unit
    ) {
        val listing = hashMapOf(
            "name" to name,
            "category" to category,
            "breed" to breed,
            "age" to age,
            "vaccinationStatus" to vaccinationStatus,
            "description" to description,
            "contact" to contact,
            "latitude" to latitude,
            "longitude" to longitude
        )

        firestore.collection("general_listings").document(listingId)
            .set(listing)
            .addOnSuccessListener {
                callback(true, "Listing updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Unknown error")
            }
    }

    fun deleteListing(
        listingId: String,
        callback: (Boolean, String) -> Unit
    ) {
        firestore.collection("general_listings").document(listingId)
            .delete()
            .addOnSuccessListener {
                callback(true, "Listing deleted successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Unknown error")
            }
    }
}
