package com.aiextech.captaapp.extensions

var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

fun String.isEmail(): Boolean {
    return emailPattern.toRegex().matches(this)
}