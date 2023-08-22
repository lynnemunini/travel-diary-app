package com.grayseal.traveldiaryapp.ui.onboarding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel : ViewModel() {

    fun signInUser(email: String, password: String): LiveData<SignInResult> {
        val resultLiveData = MutableLiveData<SignInResult>()

        // Sign-in
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resultLiveData.value = SignInResult.Success
                } else {
                    resultLiveData.value = SignInResult.Failure(task.exception?.message)
                }
            }

        return resultLiveData
    }
}

sealed class SignInResult {
    object Success : SignInResult()
    data class Failure(val error: String?) : SignInResult()
}