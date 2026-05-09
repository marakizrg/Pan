package com.example.pan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pan.data.model.User
import kotlinx.coroutines.delay
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

class AuthViewModel : ViewModel() {

    // ── Login ────────────────────────────────────────────────────────────────
    var loginEmail    by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var rememberMe    by mutableStateOf(false)

    // ── Registration ─────────────────────────────────────────────────────────
    var regUsername    by mutableStateOf("")
    var regFirstName   by mutableStateOf("")
    var regLastName    by mutableStateOf("")
    var regEmail       by mutableStateOf("")
    var regPhone       by mutableStateOf("")
    var regCountryCode by mutableStateOf("+30")
    var regUniversity  by mutableStateOf("")
    var regYear        by mutableStateOf("")

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login() {
        if (loginEmail.isBlank() || loginPassword.isBlank()) {
            _authState.value = AuthState.Error("Παρακαλώ συμπληρώστε email και κωδικό.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)
            // Mock: any credentials are accepted
            _authState.value = AuthState.Success(
                User(id = "mock-1", username = loginEmail, email = loginEmail)
            )
        }
    }

    fun register() {
        when {
            regUsername.isBlank()   -> error("Παρακαλώ εισάγετε όνομα χρήστη.")
            regFirstName.isBlank()  -> error("Παρακαλώ εισάγετε το όνομά σας.")
            regLastName.isBlank()   -> error("Παρακαλώ εισάγετε το επώνυμό σας.")
            regEmail.isBlank()      -> error("Παρακαλώ εισάγετε ακαδημαϊκό email.")
            regPhone.isBlank()      -> error("Παρακαλώ εισάγετε αριθμό τηλεφώνου.")
            regUniversity.isBlank() -> error("Παρακαλώ επιλέξτε πανεπιστήμιο.")
            regYear.isBlank()       -> error("Παρακαλώ επιλέξτε έτος σπουδών.")
            else -> viewModelScope.launch {
                _authState.value = AuthState.Loading
                delay(1200)
                _authState.value = AuthState.Success(
                    User(
                        id          = "mock-1",
                        username    = regUsername,
                        firstName   = regFirstName,
                        lastName    = regLastName,
                        email       = regEmail,
                        phone       = "$regCountryCode$regPhone",
                        university  = regUniversity,
                        yearOfStudy = regYear
                    )
                )
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