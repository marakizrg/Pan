package com.example.pan.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pan.viewmodel.ProfileViewModel

private val UNIVERSITIES = listOf(
    "ΟΠΑ - Οικονομικό Πανεπιστήμιο Αθηνών",
    "ΕΚΠΑ - Εθνικό & Καποδιστριακό Πανεπιστήμιο",
    "ΕΜΠ - Εθνικό Μετσόβιο Πολυτεχνείο",
    "ΑΠΘ - Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης",
    "Άλλο"
)

private val YEARS = listOf("1ο Έτος", "2ο Έτος", "3ο Έτος", "4ο Έτος", "5ο Έτος+")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Προφίλ", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor            = MaterialTheme.colorScheme.primary,
                    titleContentColor         = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Avatar ────────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = buildString {
                        if (viewModel.firstName.isNotBlank()) append(viewModel.firstName.first().uppercaseChar())
                        if (viewModel.lastName.isNotBlank())  append(viewModel.lastName.first().uppercaseChar())
                    }.ifBlank { "?" }
                    Text(
                        text       = initials,
                        fontSize   = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // ── Στοιχεία Προφίλ ───────────────────────────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Στοιχεία Προφίλ",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )

                    // First + Last name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileTextField(
                            value         = viewModel.firstName,
                            onValueChange = { viewModel.firstName = it },
                            label         = "Όνομα",
                            capitalization = KeyboardCapitalization.Words,
                            imeAction     = ImeAction.Next,
                            onIme         = { focusManager.moveFocus(FocusDirection.Right) },
                            modifier      = Modifier.weight(1f)
                        )
                        ProfileTextField(
                            value         = viewModel.lastName,
                            onValueChange = { viewModel.lastName = it },
                            label         = "Επώνυμο",
                            capitalization = KeyboardCapitalization.Words,
                            imeAction     = ImeAction.Next,
                            onIme         = { focusManager.moveFocus(FocusDirection.Down) },
                            modifier      = Modifier.weight(1f)
                        )
                    }

                    ProfileTextField(
                        value         = viewModel.username,
                        onValueChange = { viewModel.username = it },
                        label         = "Όνομα Χρήστη",
                        leadingIcon   = Icons.Default.Person,
                        imeAction     = ImeAction.Next,
                        onIme         = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    ProfileTextField(
                        value         = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label         = "Ακαδημαϊκό Email",
                        leadingIcon   = Icons.Default.Email,
                        keyboardType  = KeyboardType.Email,
                        imeAction     = ImeAction.Done,
                        onIme         = { focusManager.clearFocus() }
                    )

                    // University dropdown
                    var uniExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded         = uniExpanded,
                        onExpandedChange = { uniExpanded = it },
                        modifier         = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value         = viewModel.university,
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Πανεπιστήμιο") },
                            leadingIcon   = { Icon(Icons.Default.School, contentDescription = null) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(uniExpanded) },
                            modifier      = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape         = RoundedCornerShape(14.dp),
                            singleLine    = true
                        )
                        ExposedDropdownMenu(
                            expanded         = uniExpanded,
                            onDismissRequest = { uniExpanded = false }
                        ) {
                            UNIVERSITIES.forEach { uni ->
                                DropdownMenuItem(
                                    text    = { Text(uni) },
                                    onClick = { viewModel.university = uni; uniExpanded = false }
                                )
                            }
                        }
                    }

                    // Year dropdown
                    var yearExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded         = yearExpanded,
                        onExpandedChange = { yearExpanded = it },
                        modifier         = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value         = viewModel.yearOfStudy,
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Έτος Σπουδών") },
                            leadingIcon   = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(yearExpanded) },
                            modifier      = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape         = RoundedCornerShape(14.dp),
                            singleLine    = true
                        )
                        ExposedDropdownMenu(
                            expanded         = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            YEARS.forEach { year ->
                                DropdownMenuItem(
                                    text    = { Text(year) },
                                    onClick = { viewModel.yearOfStudy = year; yearExpanded = false }
                                )
                            }
                        }
                    }

                    Button(
                        onClick   = { focusManager.clearFocus(); viewModel.saveProfile() },
                        modifier  = Modifier.fillMaxWidth().height(50.dp),
                        shape     = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Αποθήκευση", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }

            // ── Αλλαγή Κωδικού ────────────────────────────────────────────────
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Αλλαγή Κωδικού",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )

                    var newPassVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value         = viewModel.newPassword,
                        onValueChange = { viewModel.newPassword = it },
                        label         = { Text("Νέος Κωδικός") },
                        leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon  = {
                            IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                Icon(
                                    if (newPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (newPassVisible) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    var confirmPassVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value         = viewModel.confirmNewPassword,
                        onValueChange = { viewModel.confirmNewPassword = it },
                        label         = { Text("Επιβεβαίωση Νέου Κωδικού") },
                        leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon  = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(
                                    if (confirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (confirmPassVisible) VisualTransformation.None
                                               else PasswordVisualTransformation(),
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(14.dp),
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus(); viewModel.changePassword() }
                        )
                    )

                    OutlinedButton(
                        onClick   = { focusManager.clearFocus(); viewModel.changePassword() },
                        modifier  = Modifier.fillMaxWidth().height(50.dp),
                        shape     = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Αλλαγή Κωδικού", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    onIme: () -> Unit = {}
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        leadingIcon   = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        modifier      = modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(14.dp),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(
            keyboardType   = keyboardType,
            capitalization = capitalization,
            imeAction      = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onIme() },
            onDone = { onIme() }
        )
    )
}