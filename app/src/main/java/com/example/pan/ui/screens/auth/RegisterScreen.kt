package com.example.pan.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pan.viewmodel.AuthState
import com.example.pan.viewmodel.AuthViewModel

private val UNIVERSITIES = listOf(
    "ΟΠΑ - Οικονομικό Πανεπιστήμιο Αθηνών",
    "ΕΚΠΑ - Εθνικό & Καποδιστριακό Πανεπιστήμιο",
    "ΕΜΠ - Εθνικό Μετσόβιο Πολυτεχνείο",
    "ΑΠΘ - Αριστοτέλειο Πανεπιστήμιο Θεσσαλονίκης",
    "Άλλο"
)

private val COUNTRY_CODES = listOf("+30", "+1", "+44", "+49", "+33", "+39", "+34", "+7")

private val YEARS = listOf("1ο Έτος", "2ο Έτος", "3ο Έτος", "4ο Έτος", "5ο Έτος+")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var universityExpanded  by remember { mutableStateOf(false) }
    var countryCodeExpanded by remember { mutableStateOf(false) }
    var yearExpanded        by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (val s = authState) {
            is AuthState.Success -> onRegisterSuccess()
            is AuthState.Error   -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Εγγραφή",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel("Στοιχεία Λογαριασμού")

            // Username
            PanTextField(
                value         = viewModel.regUsername,
                onValueChange = { viewModel.regUsername = it },
                label         = "Όνομα Χρήστη",
                leadingIcon   = Icons.Default.Person,
                keyboardType  = KeyboardType.Text,
                imeAction     = ImeAction.Next,
                onNext        = { focusManager.moveFocus(FocusDirection.Down) }
            )

            // First + Last name row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PanTextField(
                    value         = viewModel.regFirstName,
                    onValueChange = { viewModel.regFirstName = it },
                    label         = "Όνομα",
                    keyboardType  = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction     = ImeAction.Next,
                    onNext        = { focusManager.moveFocus(FocusDirection.Right) },
                    modifier      = Modifier.weight(1f)
                )
                PanTextField(
                    value         = viewModel.regLastName,
                    onValueChange = { viewModel.regLastName = it },
                    label         = "Επώνυμο",
                    keyboardType  = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction     = ImeAction.Next,
                    onNext        = { focusManager.moveFocus(FocusDirection.Down) },
                    modifier      = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionLabel("Στοιχεία Επικοινωνίας")

            // Academic email
            PanTextField(
                value         = viewModel.regEmail,
                onValueChange = { viewModel.regEmail = it },
                label         = "Ακαδημαϊκό Email",
                leadingIcon   = Icons.Default.Email,
                keyboardType  = KeyboardType.Email,
                imeAction     = ImeAction.Next,
                onNext        = { focusManager.moveFocus(FocusDirection.Down) }
            )

            // Phone row: country code + number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Country code dropdown
                ExposedDropdownMenuBox(
                    expanded        = countryCodeExpanded,
                    onExpandedChange = { countryCodeExpanded = it },
                    modifier         = Modifier.width(110.dp)
                ) {
                    OutlinedTextField(
                        value         = viewModel.regCountryCode,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Κωδ.") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryCodeExpanded) },
                        modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape         = RoundedCornerShape(14.dp),
                        singleLine    = true
                    )
                    ExposedDropdownMenu(
                        expanded        = countryCodeExpanded,
                        onDismissRequest = { countryCodeExpanded = false }
                    ) {
                        COUNTRY_CODES.forEach { code ->
                            DropdownMenuItem(
                                text    = { Text(code) },
                                onClick = {
                                    viewModel.regCountryCode = code
                                    countryCodeExpanded = false
                                }
                            )
                        }
                    }
                }

                PanTextField(
                    value         = viewModel.regPhone,
                    onValueChange = { viewModel.regPhone = it },
                    label         = "Αριθμός Τηλεφώνου",
                    leadingIcon   = Icons.Default.Phone,
                    keyboardType  = KeyboardType.Phone,
                    imeAction     = ImeAction.Next,
                    onNext        = { focusManager.moveFocus(FocusDirection.Down) },
                    modifier      = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            SectionLabel("Ακαδημαϊκά Στοιχεία")

            // University dropdown
            ExposedDropdownMenuBox(
                expanded         = universityExpanded,
                onExpandedChange = { universityExpanded = it },
                modifier         = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value         = viewModel.regUniversity.ifBlank { "" },
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Πανεπιστήμιο") },
                    leadingIcon   = { Icon(Icons.Default.School, contentDescription = null) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = universityExpanded) },
                    placeholder   = { Text("Επιλέξτε πανεπιστήμιο") },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape      = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded         = universityExpanded,
                    onDismissRequest = { universityExpanded = false }
                ) {
                    UNIVERSITIES.forEach { uni ->
                        DropdownMenuItem(
                            text    = { Text(uni) },
                            onClick = {
                                viewModel.regUniversity = uni
                                universityExpanded = false
                            }
                        )
                    }
                }
            }

            // Year of study dropdown
            ExposedDropdownMenuBox(
                expanded         = yearExpanded,
                onExpandedChange = { yearExpanded = it },
                modifier         = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value         = viewModel.regYear.ifBlank { "" },
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Έτος Σπουδών") },
                    leadingIcon   = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                    placeholder   = { Text("Επιλέξτε έτος") },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape      = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded         = yearExpanded,
                    onDismissRequest = { yearExpanded = false }
                ) {
                    YEARS.forEach { year ->
                        DropdownMenuItem(
                            text    = { Text(year) },
                            onClick = {
                                viewModel.regYear = year
                                yearExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Register button
            Button(
                onClick  = { focusManager.clearFocus(); viewModel.register() },
                enabled  = authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color       = MaterialTheme.colorScheme.onPrimary,
                        modifier    = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        "ΕΓΓΡΑΦΗ",
                        fontSize      = 16.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Already have account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Έχετε ήδη λογαριασμό;",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateBack) {
                    Text(
                        "Σύνδεση →",
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.labelLarge,
        color      = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun PanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
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
            onNext = onNext?.let { { it() } },
            onDone = onDone?.let { { it() } }
        )
    )
}