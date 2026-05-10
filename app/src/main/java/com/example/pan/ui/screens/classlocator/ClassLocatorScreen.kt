package com.example.pan.ui.screens.classlocator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val classrooms = listOf(
    "Αίθουσα Α1",
    "Αίθουσα Β2",
    "Αμφιθέατρο Α",
    "Αμφιθέατρο Β",
    "Αίθουσα Γ1",
)

// Fractional (x, y) center of each classroom's dot in the canvas
private val dotPositions = mapOf(
    "Αίθουσα Α1"   to Pair(0.175f, 0.195f),
    "Αίθουσα Β2"   to Pair(0.175f, 0.490f),
    "Αμφιθέατρο Α" to Pair(0.480f, 0.305f),
    "Αμφιθέατρο Β" to Pair(0.480f, 0.765f),
    "Αίθουσα Γ1"   to Pair(0.805f, 0.495f),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLocatorScreen(onBack: () -> Unit) {
    var expanded          by remember { mutableStateOf(false) }
    var selectedClassroom by remember { mutableStateOf(classrooms[2]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Πλοήγηση σε Κτήρια", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Πίσω")
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
            ClassroomSelector(
                expanded         = expanded,
                selected         = selectedClassroom,
                onExpandedChange = { expanded = it },
                onSelect         = { selectedClassroom = it; expanded = false }
            )
            LocationInstructions(selectedClassroom)
            FloorPlanCard(selectedClassroom)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassroomSelector(
    expanded: Boolean,
    selected: String,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text  = "Επιλέξτε αίθουσα ή δείτε την προεπιλεγμένη βάσει του προγράμματός σας",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value         = selected,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Αίθουσα") },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors        = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier      = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                classrooms.forEach { room ->
                    DropdownMenuItem(
                        text    = { Text(room) },
                        onClick = { onSelect(room) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInstructions(selectedClassroom: String) {
    val borderColor = MaterialTheme.colorScheme.outline
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(borderColor, 8.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text       = "Τοποθεσία: $selectedClassroom",
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.primary
        )
        Text(
            text  = "Οδηγίες τοποθεσίας επιλεγμένης αίθουσας...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FloorPlanCard(selectedClassroom: String) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val dotPos       = dotPositions[selectedClassroom] ?: Pair(0.175f, 0.490f)

    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = "Κάτοψη Κτηρίου",
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                val w     = size.width
                val h     = size.height
                val thick = Stroke(2.5.dp.toPx())
                val thin  = Stroke(1.5.dp.toPx())
                val rc    = CornerRadius(3.dp.toPx())

                fun room(x1f: Float, y1f: Float, x2f: Float, y2f: Float) {
                    drawRoundRect(
                        color        = outlineColor,
                        topLeft      = Offset(w * x1f, h * y1f),
                        size         = Size(w * (x2f - x1f), h * (y2f - y1f)),
                        cornerRadius = rc,
                        style        = thin
                    )
                }

                // Building outer wall
                drawRoundRect(
                    color        = outlineColor,
                    topLeft      = Offset(w * 0.02f, h * 0.03f),
                    size         = Size(w * 0.96f, h * 0.94f),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                    style        = thick
                )

                // Left column — 3 rooms (Α1, Β2, unused)
                room(0.05f, 0.07f, 0.30f, 0.33f)
                room(0.05f, 0.37f, 0.30f, 0.63f)
                room(0.05f, 0.67f, 0.30f, 0.94f)

                // Centre column — 2 rooms (Αμφιθέατρο Α, Αμφιθέατρο Β)
                room(0.34f, 0.07f, 0.62f, 0.55f)
                room(0.34f, 0.59f, 0.62f, 0.94f)

                // Right column — 1 large room (Γ1)
                room(0.66f, 0.07f, 0.95f, 0.94f)

                // Halo + filled dot at target classroom
                val dotX = w * dotPos.first
                val dotY = h * dotPos.second
                drawCircle(
                    color  = primaryColor.copy(alpha = 0.20f),
                    radius = 16.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
                drawCircle(
                    color  = primaryColor,
                    radius = 7.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }
    }
}

private fun Modifier.dashedBorder(color: Color, radius: Dp): Modifier = this.drawBehind {
    val dash = 8.dp.toPx()
    val gap  = 4.dp.toPx()
    drawRoundRect(
        color        = color,
        cornerRadius = CornerRadius(radius.toPx()),
        style        = Stroke(
            width      = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, gap), 0f)
        )
    )
}
