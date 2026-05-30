package com.example.pan.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pan.viewmodel.DashboardViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pan.navigation.Screen
import com.example.pan.ui.components.PanDrawerContent
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class ScheduleEntry(
    val day: String,
    val time: String,
    val course: String,
    val room: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()
    val schedule    = viewModel.schedule

    ModalNavigationDrawer(
        drawerState   = drawerState,
        modifier      = Modifier.fillMaxSize(),
        drawerContent = {
            PanDrawerContent(
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    onNavigateTo(route)
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
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
                    viewModel.importSchedule()
                })
                ClassroomScannerCard(onScan = {
                    onNavigateTo(Screen.ClassroomScanner.route)
                })
                CalendarCard(schedule = schedule)
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
                    "ΔΗΜΙΟΥΡΓΙΑ ΠΡΟΓΡΑΜΜΑΤΟΣ",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "ΠΑΤΗΣΤΕ ΕΔΩ ΓΙΑ ΝΑ ΕΙΣΑΧΘΟΥΝ ΤΑ ΜΑΘΗΜΑΤΑ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ClassroomScannerCard(onScan: () -> Unit) {
    ElevatedCard(
        onClick   = onScan,
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier              = Modifier.padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint     = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Column {
                Text(
                    "ΣΑΡΩΣΗ ΑΙΘΟΥΣΑΣ",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "ΣΑΡΩΣΤΕ ΤΗΝ ΠΙΝΑΚΙΔΑ ΓΙΑ ΝΑ ΔΕΙΤΕ ΤΟ ΠΡΟΓΡΑΜΜΑ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun CalendarCard(schedule: List<ScheduleEntry>) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val dayMap = mapOf(
        "Monday" to "Δευ",
        "Tuesday" to "Τρι",
        "Wednesday" to "Τετ",
        "Thursday" to "Πεμ",
        "Friday" to "Παρ"
    )
    val currentDayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val initialDay = when(currentDayIndex) {
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        else -> "Monday"
    }
    var selectedDay by remember { mutableStateOf(initialDay) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "ΗΜΕΡΟΛΟΓΙΟ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))

            if (schedule.isEmpty()) {
                Text(
                    "Δεν υπάρχουν μαθήματα. Εισάγετε το πρόγραμμά σας για να εμφανιστεί εδώ.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEach { day ->
                        val isSelected = selectedDay == day
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .clickable { selectedDay = day }
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = dayMap[day] ?: day,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                val daySchedule = schedule.filter { it.day == selectedDay }

                if (daySchedule.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Δεν υπάρχουν μαθήματα για αυτή την ημέρα.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    daySchedule.forEachIndexed { index, entry ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 68.dp, top = 4.dp, bottom = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isLab = entry.course.contains("Φροντιστήριο", ignoreCase = true)
                            val accentColor = if (isLab) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary

                            Text(
                                text = entry.time,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.width(56.dp),
                                textAlign = TextAlign.End
                            )
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .width(2.dp)
                                    .height(32.dp)
                                    .background(accentColor.copy(alpha = 0.3f))
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.course,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isLab) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = entry.room,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

