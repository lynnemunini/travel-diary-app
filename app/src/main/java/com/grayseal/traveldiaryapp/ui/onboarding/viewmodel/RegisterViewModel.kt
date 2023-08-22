package com.grayseal.traveldiaryapp.ui.onboarding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.grayseal.traveldiaryapp.utils.isEmailValid
import com.grayseal.traveldiaryapp.utils.isPasswordValid

class RegisterViewModel : ViewModel() {

    fun registerUser(email: String, password: String): LiveData<RegistrationResult> {
        val resultLiveData = MutableLiveData<RegistrationResult>()

        if (!isEmailValid(email)) {
            resultLiveData.value = RegistrationResult.InvalidEmail
            return resultLiveData
        }

        if (!isPasswordValid(password)) {
            resultLiveData.value = RegistrationResult.InvalidPassword
            return resultLiveData
        }

        // Register the user using Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resultLiveData.value = RegistrationResult.Success
                } else {
                    resultLiveData.value = RegistrationResult.Failure(task.exception?.message)
                }
            }

        return resultLiveData
    }
}

sealed class RegistrationResult {
    object Success : RegistrationResult()
    object InvalidEmail : RegistrationResult()
    object InvalidPassword : RegistrationResult()
    data class Failure(val error: String?) : RegistrationResult()
}
