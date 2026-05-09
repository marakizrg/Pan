package com.example.pan.data.model

data class User(
    val id: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val university: String = "",
    val yearOfStudy: String = ""
) {
    val fullName: String get() = "$firstName $lastName".trim()
}