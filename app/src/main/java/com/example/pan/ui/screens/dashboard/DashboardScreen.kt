package com.example.pan.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pan.ui.components.PanDrawerContent
import kotlinx.coroutines.launch

data class ScheduleEntry(
    val day: String,
    val time: String,
    val course: String,
    val room: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigateTo: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    var schedule by remember { mutableStateOf(listOf<ScheduleEntry>()) }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        modifier      = Modifier.fillMaxSize(),
        drawerContent = {
            PanDrawerContent(onItemClick = { route ->
                scope.launch { drawerState.close() }
                onNavigateTo(route)
            })
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Αρχική", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Μενού")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor             = MaterialTheme.colorScheme.primary,
                        titleContentColor          = MaterialTheme.colorScheme.onPrimary,
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CreateScheduleCard(onImport = {
                    schedule = loadScheduleFromAssets(context)
                })
                CalendarCard(schedule = schedule)
                AcademicNotificationsCard()
            }
        }
    }
}

@Composable
private fun CreateScheduleCard(onImport: () -> Unit) {
    ElevatedCard(
        onClick   = { onImport() },
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier              = Modifier.padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint     = MaterialTheme.colorScheme.onPrimary
            )
            Column {
                Text(
                    "CREATE SCHEDULE",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "PRESS HERE TO IMPORT REGISTERED CLASSES",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CalendarCard(schedule: List<ScheduleEntry>) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "CALENDAR",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "SHOWS DAILY PLAN ON PRESS AND NOTIFIES WHEN ASSIGNMENTS ARE DUE",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            if (schedule.isEmpty()) {
                Text(
                    "No classes imported yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                schedule.forEachIndexed { index, entry ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.course, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(entry.room,   style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(entry.day,    style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            entry.time,
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private val mockAnnouncements = listOf(
    "Αναπλήρωση μαθήματος Αλγορίθμων την Παρασκευή 16/05 στην Αίθουσα Γ1." to "Γραμματεία",
    "Υπενθύμιση: Προθεσμία παράδοσης εργασίας Προγραμματισμού η 20/05."     to "Τμήμα ΟΠΕ",
)

@Composable
private fun AcademicNotificationsCard() {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "ACADEMIC NOTIFICATIONS",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))
            mockAnnouncements.forEachIndexed { index, (message, source) ->
                if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(message, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun loadScheduleFromAssets(context: android.content.Context): List<ScheduleEntry> {
    val json = context.assets.open("classes.json")
        .bufferedReader().use { it.readText() }
    val array = org.json.JSONArray(json)
    return List(array.length()) { i ->
        val obj = array.getJSONObject(i)
        ScheduleEntry(
            day    = obj.getString("day"),
            time   = obj.getString("time"),
            course = obj.getString("course"),
            room   = obj.getString("room")
        )
    }
}