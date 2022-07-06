package com.aiextech.captaapp.extensions

var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

var passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{4,}\$"

fun String.isEmail(): Boolean {
    return emailPattern.toRegex().matches(this)
}

fun String.isPassword(): Boolean {
    return passwordPattern.toRegex().matches(this)
}

