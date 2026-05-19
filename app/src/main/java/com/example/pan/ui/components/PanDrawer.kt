package com.example.pan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pan.navigation.Screen

private data class DrawerItem(val label: String, val icon: ImageVector, val route: String)

private val mainItems = listOf(
    DrawerItem("Ειδοποιήσεις",        Icons.Default.Notifications,          Screen.Notifications.route),
    DrawerItem("Diploma Pal",          Icons.Default.School,                 Screen.DiplomaHelper.route),
    DrawerItem("Οδηγός Σπουδών",      Icons.AutoMirrored.Filled.MenuBook,   Screen.StudyGuide.route),
    DrawerItem("Πλοήγηση σε Κτήρια", Icons.Default.LocationOn,             Screen.ClassLocator.route),
)

@Composable
fun PanDrawerContent(onItemClick: (String) -> Unit, onLogout: () -> Unit) {
    ModalDrawerSheet {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    "ΠΑΝ",
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    "AUEB Companion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        mainItems.forEach { item ->
            NavigationDrawerItem(
                icon     = { Icon(item.icon, contentDescription = null) },
                label    = { Text(item.label) },
                selected = false,
                onClick  = { onItemClick(item.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        Text(
            "ΠΡΟΦΙΛ",
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
        )

        NavigationDrawerItem(
            icon     = { Icon(Icons.Default.Person, contentDescription = null) },
            label    = { Text("Το Προφίλ μου") },
            selected = false,
            onClick  = { onItemClick(Screen.Profile.route) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        NavigationDrawerItem(
            icon     = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            label    = { Text("Αποσύνδεση", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick  = { onLogout() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}