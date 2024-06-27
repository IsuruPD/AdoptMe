package com.s92066379.adoptme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.s92066379.adoptme.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _login= MutableSharedFlow<Resource<FirebaseUser>>()
    val login= _login.asSharedFlow()

    private val _resetPassword= MutableSharedFlow<Resource<String>>()
    val resetPassword= _resetPassword.asSharedFlow()

    fun login(email: String, password: String){
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener{
                viewModelScope.launch {
                    it.user?.let{
                        _login.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}