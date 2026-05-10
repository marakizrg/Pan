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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
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

private val drawerItems = listOf(
    DrawerItem("Ειδοποιήσεις",         Icons.Default.Notifications, Screen.Notifications.route),
    DrawerItem("Diploma Pal",           Icons.Default.School,        Screen.DiplomaHelper.route),
    DrawerItem("Οδηγός Σπουδών",       Icons.AutoMirrored.Filled.MenuBook, Screen.StudyGuide.route),
    DrawerItem("Πλοήγηση σε Κτήρια",  Icons.Default.LocationOn,    Screen.ClassLocator.route),
)

@Composable
fun PanDrawerContent(onItemClick: (String) -> Unit) {
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
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon     = { Icon(item.icon, contentDescription = null) },
                label    = { Text(item.label) },
                selected = false,
                onClick  = { onItemClick(item.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
