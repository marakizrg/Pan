package com.example.pan.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pan.ui.theme.AuebBlue
import com.example.pan.ui.theme.AuebBluePale
import com.example.pan.viewmodel.AuthState
import com.example.pan.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

    LaunchedEffect(authState) {
        when (val s = authState) {
            is AuthState.Success -> onLoginSuccess()
            is AuthState.Error   -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            // ── Logo ────────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                LogoSection()
            }

            Spacer(Modifier.height(48.dp))

            // ── Form ────────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 60 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = viewModel.loginEmail,
                        onValueChange = { viewModel.loginEmail = it },
                        label = { Text("Email / Όνομα Χρήστη") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.loginPassword,
                        onValueChange = { viewModel.loginPassword = it },
                        label = { Text("Κωδικός Πρόσβασης") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Απόκρυψη" else "Εμφάνιση"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    // Remember me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked   = viewModel.rememberMe,
                            onCheckedChange = { viewModel.rememberMe = it }
                        )
                        Text("Να με θυμάσαι", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(24.dp))

                    // Login button
                    Button(
                        onClick  = { focusManager.clearFocus(); viewModel.login() },
                        enabled  = authState !is AuthState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color    = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                "ΣΥΝΔΕΣΗ",
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Register link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Νέος χρήστης;",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = onNavigateToRegister) {
                            Text(
                                "Εγγραφή →",
                                fontWeight = FontWeight.SemiBold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Rounded square logo
        Surface(
            modifier = Modifier.size(108.dp),
            shape    = RoundedCornerShape(28.dp),
            color    = AuebBlue,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text       = "Π",
                    fontSize   = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text       = "ΠΑΝ",
            fontSize   = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = MaterialTheme.colorScheme.primary,
            letterSpacing = 4.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text      = "Ο Συνοδός του Φοιτητή",
            fontSize  = 14.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AuebBluePale
        ) {
            Text(
                text     = "ΟΠΑ · AUEB",
                fontSize = 12.sp,
                color    = AuebBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}