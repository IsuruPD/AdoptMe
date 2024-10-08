package com.s92066379.adoptme.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.s92066379.adoptme.data.User
import com.s92066379.adoptme.util.Constants.USER_COLLECTION
import com.s92066379.adoptme.util.RegisterFieldsState
import com.s92066379.adoptme.util.RegisterValidation
import com.s92066379.adoptme.util.Resource
import com.s92066379.adoptme.util.validateEmail
import com.s92066379.adoptme.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountsWithEmailPass(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    val firebaseUser = authResult.user
                    firebaseUser?.let {
                        val username = "${user.firstname} ${user.lastname}"
                        val updatedUser = user.copy(username = username)
                        saveUserInfo(it.uid, updatedUser)
                    }
                }
                .addOnFailureListener { e ->
                    _register.value = Resource.Error(e.message.toString())
                }
        } else {
            val registerFieldsState =
                RegisterFieldsState(validateEmail(user.email), validatePassword(password))
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        val userData = hashMapOf(
            "firstname" to user.firstname,
            "lastname" to user.lastname,
            "email" to user.email,
            "imagePath" to user.imagePath,
            "id" to userUid,
            "nic" to user.nic,
            "phoneNumber" to user.phoneNumber,
            "username" to user.username,
            "gender" to user.gender,
            "dob" to user.dob
        )
        val updatedUser = user.copy(id = userUid)

        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(userData)
            .addOnSuccessListener {
                _register.value = Resource.Success(updatedUser)
            }
            .addOnFailureListener { e ->
                _register.value = Resource.Error(e.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val validationStatus =
            emailValidation is RegisterValidation.Success &&
                    passwordValidation is RegisterValidation.Success

        return validationStatus
    }
}
