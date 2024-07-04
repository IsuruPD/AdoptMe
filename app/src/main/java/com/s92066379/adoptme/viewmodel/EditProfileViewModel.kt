package com.s92066379.adoptme.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.s92066379.adoptme.data.User
import com.s92066379.adoptme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {


    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    private val _updateStatus = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val updateStatus = _updateStatus.asStateFlow()

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
            try {
                val userDocument = firestore.collection("user").document(auth.uid!!).get().await()
                val user = userDocument.toObject(User::class.java)
                user?.let {
                    _user.emit(Resource.Success(it))
                } ?: run {
                    _user.emit(Resource.Error("User not found"))
                }
            } catch (e: Exception) {
                _user.emit(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }

    fun updateUserDetails(firstname: String, lastname: String, phoneNumber: String, imageUri: Uri?, originalImageUrl: String?) {
        viewModelScope.launch {
            _updateStatus.emit(Resource.Loading())
            try {
                val userUpdates = mutableMapOf(
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "phoneNumber" to phoneNumber
                )

                if (imageUri != null) {
                    val imageRef = storage.child("profile_images/${auth.uid!!}.jpg")
                    imageRef.putFile(imageUri).await()
                    val imageUrl = imageRef.downloadUrl.await().toString()
                    userUpdates["imagePath"] = imageUrl
                }else if (originalImageUrl != null) {
                    userUpdates["imagePath"] = originalImageUrl
                }

                firestore.collection("user").document(auth.uid!!).update(userUpdates as Map<String, Any>).await()
                _updateStatus.emit(Resource.Success(Unit))
            } catch (e: Exception) {
                _updateStatus.emit(Resource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
    fun resetUpdateStatus() {
        _updateStatus.value = Resource.Unspecified()
    }
}
