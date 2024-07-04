package com.s92066379.adoptme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.s92066379.adoptme.data.User
import com.s92066379.adoptme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserOptionsVM @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {
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

    fun logout() {
        auth.signOut()
    }
}
