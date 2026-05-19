package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pan.data.local.UserPreferences
import com.example.pan.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Idle    : AuthState()
    data object Loading : AuthState()
    data class  Success(val user: User) : AuthState()
    data class  Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)

    // ── Login ────────────────────────────────────────────────────────────────
    var loginEmail    by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var rememberMe    by mutableStateOf(false)

    // ── Registration ─────────────────────────────────────────────────────────
    var regUsername        by mutableStateOf("")
    var regFirstName       by mutableStateOf("")
    var regLastName        by mutableStateOf("")
    var regEmail           by mutableStateOf("")
    var regPhone           by mutableStateOf("")
    var regCountryCode     by mutableStateOf("+30")
    var regUniversity      by mutableStateOf("")
    var regYear            by mutableStateOf("")
    var regPassword        by mutableStateOf("")
    var regPasswordConfirm by mutableStateOf("")

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login() {
        if (loginEmail.isBlank() || loginPassword.isBlank()) {
            _authState.value = AuthState.Error("Παρακαλώ συμπληρώστε email και κωδικό.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = userPrefs.loginUser(loginEmail.trim(), loginPassword)
            _authState.value = if (user != null) {
                userPrefs.setCurrentUserId(user.id)
                AuthState.Success(user)
            } else {
                AuthState.Error("Λανθασμένο email/όνομα χρήστη ή κωδικός.")
            }
        }
    }

    fun register() {
        when {
            regUsername.isBlank()             -> error("Παρακαλώ εισάγετε όνομα χρήστη.")
            regFirstName.isBlank()            -> error("Παρακαλώ εισάγετε το όνομά σας.")
            regLastName.isBlank()             -> error("Παρακαλώ εισάγετε το επώνυμό σας.")
            regEmail.isBlank()                -> error("Παρακαλώ εισάγετε ακαδημαϊκό email.")
            !regEmail.contains("@")           -> error("Το email δεν είναι έγκυρο. Πρέπει να περιέχει @.")
            regPhone.isBlank()                -> error("Παρακαλώ εισάγετε αριθμό τηλεφώνου.")
            regUniversity.isBlank()           -> error("Παρακαλώ επιλέξτε πανεπιστήμιο.")
            regYear.isBlank()                 -> error("Παρακαλώ επιλέξτε έτος σπουδών.")
            regPassword.isBlank()             -> error("Παρακαλώ εισάγετε κωδικό πρόσβασης.")
            regPassword.length < 6            -> error("Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.")
            regPassword != regPasswordConfirm -> error("Οι κωδικοί δεν ταιριάζουν.")
            else -> viewModelScope.launch {
                _authState.value = AuthState.Loading
                val user = User(
                    username    = regUsername.trim(),
                    firstName   = regFirstName.trim(),
                    lastName    = regLastName.trim(),
                    email       = regEmail.trim(),
                    phone       = "$regCountryCode$regPhone",
                    university  = regUniversity,
                    yearOfStudy = regYear
                )
                val result = userPrefs.registerUser(user, regPassword)
                _authState.value = if (result.isSuccess) {
                    val savedUser = result.getOrThrow()
                    userPrefs.setCurrentUserId(savedUser.id)
                    AuthState.Success(savedUser)
                } else {
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Σφάλμα εγγραφής.")
                }
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) _authState.value = AuthState.Idle
    }

    private fun error(message: String) {
        _authState.value = AuthState.Error(message)
    }
}