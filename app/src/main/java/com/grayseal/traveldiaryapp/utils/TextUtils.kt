package com.grayseal.traveldiaryapp.utils

fun isEmailValid(email: String): Boolean {
    val regexPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return email.matches(Regex(regexPattern))
}

fun isPasswordValid(password: String): Boolean {
    return password.length >= 6
}