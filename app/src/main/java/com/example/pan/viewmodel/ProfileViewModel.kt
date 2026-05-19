package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.data.local.UserPreferences
import com.example.pan.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)
    private var userId = ""

    var firstName   by mutableStateOf("")
    var lastName    by mutableStateOf("")
    var username    by mutableStateOf("")
    var email       by mutableStateOf("")
    var university  by mutableStateOf("")
    var yearOfStudy by mutableStateOf("")

    var newPassword        by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        val id = userPrefs.getCurrentUserId()
        if (id != null) {
            userId = id
            val user = userPrefs.getUserById(id)
            if (user != null) {
                firstName   = user.firstName
                lastName    = user.lastName
                username    = user.username
                email       = user.email
                university  = user.university
                yearOfStudy = user.yearOfStudy
            }
        }
    }

    fun saveProfile() {
        when {
            firstName.isBlank()   -> { _message.value = "Το όνομα δεν μπορεί να είναι κενό."; return }
            lastName.isBlank()    -> { _message.value = "Το επώνυμο δεν μπορεί να είναι κενό."; return }
            username.isBlank()    -> { _message.value = "Το όνομα χρήστη δεν μπορεί να είναι κενό."; return }
            email.isBlank()       -> { _message.value = "Το email δεν μπορεί να είναι κενό."; return }
            !email.contains("@") -> { _message.value = "Το email δεν είναι έγκυρο."; return }
        }
        val currentPhone = userPrefs.getUserById(userId)?.phone ?: ""
        val user = User(
            id          = userId,
            firstName   = firstName.trim(),
            lastName    = lastName.trim(),
            username    = username.trim(),
            email       = email.trim(),
            phone       = currentPhone,
            university  = university,
            yearOfStudy = yearOfStudy
        )
        val result = userPrefs.updateUser(user)
        _message.value = if (result.isSuccess) "Το προφίλ αποθηκεύτηκε!"
                         else result.exceptionOrNull()?.message
    }

    fun changePassword() {
        when {
            newPassword.isBlank()             -> { _message.value = "Ο νέος κωδικός δεν μπορεί να είναι κενός."; return }
            newPassword.length < 6            -> { _message.value = "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες."; return }
            newPassword != confirmNewPassword -> { _message.value = "Οι κωδικοί δεν ταιριάζουν."; return }
        }
        userPrefs.updatePassword(userId, newPassword)
        newPassword = ""
        confirmNewPassword = ""
        _message.value = "Ο κωδικός άλλαξε επιτυχώς!"
    }

    fun clearMessage() {
        _message.value = null
    }
}