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
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

// BUILDINGS

private val buildings = listOf(
    "Πατησίων"
)

// FLOORS

private val floors = listOf(
    "Ισόγειο",
    "Ημιώροφος",
    "1ος Όροφος"
)

// START LOCATIONS

private val startLocations = listOf(
    "Κεντρική Είσοδος",
    "Οδός Δεριγνύ",
    "Οδός Αντωνιάδου",
    "Οδός Μαυροματαίων",
    "Οδός Πατησίων"
)

// CLASSROOMS

private val classrooms = listOf(

    "Δ11",

    "Αμφιθέατρο Α",
    "Αμφιθέατρο Β",
    "Αμφιθέατρο Γ",
    "Αμφιθέατρο Δεριγνύ",
    "Αμφιθέατρο Αντωνιάδου"
)

// DOT POSITIONS

private val dotPositions = mapOf(

    "Δ11" to Pair(0.63f, 0.53f),

    "Αμφιθέατρο Α" to Pair(0.50f, 0.16f),
    "Αμφιθέατρο Β" to Pair(0.34f, 0.22f),
    "Αμφιθέατρο Γ" to Pair(0.66f, 0.22f),

    "Αμφιθέατρο Δεριγνύ" to Pair(0.13f, 0.68f),

    "Αμφιθέατρο Αντωνιάδου" to Pair(0.86f, 0.73f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatisionLocatorScreen(
    onBack: () -> Unit
) {

    var selectedBuilding by remember {
        mutableStateOf(buildings.first())
    }

    var selectedFloor by remember {
        mutableStateOf(floors.first())
    }

    var selectedStartLocation by remember {
        mutableStateOf(startLocations.first())
    }

    var selectedClassroom by remember {
        mutableStateOf(classrooms.first())
    }

    var buildingExpanded by remember {
        mutableStateOf(false)
    }

    var floorExpanded by remember {
        mutableStateOf(false)
    }

    var startExpanded by remember {
        mutableStateOf(false)
    }

    var classroomExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Κτίριο Πατησίων",
                        fontWeight = FontWeight.SemiBold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Πίσω"
                        )
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
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SelectorBox(
                title = "Επιλογή Κτηρίου",
                label = "Κτήριο",
                expanded = buildingExpanded,
                selected = selectedBuilding,
                items = buildings,
                onExpandedChange = {
                    buildingExpanded = it
                },
                onSelect = {
                    selectedBuilding = it
                    buildingExpanded = false
                }
            )

            SelectorBox(
                title = "Επιλογή Ορόφου",
                label = "Όροφος",
                expanded = floorExpanded,
                selected = selectedFloor,
                items = floors,
                onExpandedChange = {
                    floorExpanded = it
                },
                onSelect = {
                    selectedFloor = it
                    floorExpanded = false
                }
            )

            SelectorBox(
                title = "Σημείο Εκκίνησης",
                label = "Αφετηρία",
                expanded = startExpanded,
                selected = selectedStartLocation,
                items = startLocations,
                onExpandedChange = {
                    startExpanded = it
                },
                onSelect = {
                    selectedStartLocation = it
                    startExpanded = false
                }
            )

            SelectorBox(
                title = "Επιλογή Αίθουσας",
                label = "Αίθουσα",
                expanded = classroomExpanded,
                selected = selectedClassroom,
                items = classrooms,
                onExpandedChange = {
                    classroomExpanded = it
                },
                onSelect = {
                    selectedClassroom = it
                    classroomExpanded = false
                }
            )

            LocationInstructions(
                selectedBuilding = selectedBuilding,
                selectedFloor = selectedFloor,
                selectedStartLocation = selectedStartLocation,
                selectedClassroom = selectedClassroom
            )

            FloorPlanCard(
                selectedClassroom = selectedClassroom
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorBox(
    title: String,
    label: String,
    expanded: Boolean,
    selected: String,
    items: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {

            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,

                label = {
                    Text(label)
                },

                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                }
            ) {

                items.forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },

                        onClick = {
                            onSelect(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInstructions(
    selectedBuilding: String,
    selectedFloor: String,
    selectedStartLocation: String,
    selectedClassroom: String
) {

    val borderColor = MaterialTheme.colorScheme.outline

    val floorInstruction = when {

        selectedClassroom == "Δ11" -> {
            "Μεταβείτε στον 1ο Όροφο."
        }

        else -> {
            "Η αίθουσα βρίσκεται στο Ισόγειο."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(borderColor, 8.dp)
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = "Τοποθεσία: $selectedClassroom",

            style = MaterialTheme.typography.labelMedium,

            fontWeight = FontWeight.SemiBold,

            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Κτήριο: $selectedBuilding"
        )

        Text(
            text = "Όροφος: $selectedFloor"
        )

        Text(
            text = "Αφετηρία: $selectedStartLocation"
        )

        Text(
            text = floorInstruction,

            style = MaterialTheme.typography.bodyMedium,

            fontWeight = FontWeight.SemiBold,

            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Η επιλεγμένη αίθουσα εμφανίζεται με μπλε δείκτη στην κάτοψη.",

            style = MaterialTheme.typography.bodySmall,

            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FloorPlanCard(
    selectedClassroom: String
) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline

    val dotPos = dotPositions[selectedClassroom]
        ?: Pair(0.50f, 0.50f)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),

        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Κάτοψη Κτιρίου Πατησίων",

                style = MaterialTheme.typography.titleSmall,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {

                val w = size.width
                val h = size.height

                val thick = Stroke(2.5.dp.toPx())
                val thin = Stroke(1.5.dp.toPx())

                val rc = CornerRadius(4.dp.toPx())

                fun room(
                    x1: Float,
                    y1: Float,
                    x2: Float,
                    y2: Float
                ) {

                    drawRoundRect(
                        color = outlineColor,

                        topLeft = Offset(
                            w * x1,
                            h * y1
                        ),

                        size = Size(
                            w * (x2 - x1),
                            h * (y2 - y1)
                        ),

                        cornerRadius = rc,

                        style = thin
                    )
                }

                // OUTER BUILDING
                drawRoundRect(
                    color = outlineColor,

                    topLeft = Offset(
                        w * 0.02f,
                        h * 0.03f
                    ),

                    size = Size(
                        w * 0.96f,
                        h * 0.94f
                    ),

                    cornerRadius = CornerRadius(8.dp.toPx()),

                    style = thick
                )

                // ΑΜΦΙΘΕΑΤΡΟ Β
                room(0.28f, 0.08f, 0.40f, 0.28f)

                // ΑΜΦΙΘΕΑΤΡΟ Α
                room(0.43f, 0.05f, 0.57f, 0.28f)

                // ΑΜΦΙΘΕΑΤΡΟ Γ
                room(0.60f, 0.08f, 0.72f, 0.28f)

                // Δ11
                room(0.57f, 0.46f, 0.69f, 0.62f)

                // ΑΜΦΙΘΕΑΤΡΟ ΔΕΡΙΓΝΥ
                room(0.03f, 0.52f, 0.22f, 0.84f)

                // ΑΜΦΙΘΕΑΤΡΟ ΑΝΤΩΝΙΑΔΟΥ
                room(0.78f, 0.55f, 0.95f, 0.90f)

                // LOCATION DOT
                val dotX = w * dotPos.first
                val dotY = h * dotPos.second

                drawCircle(
                    color = primaryColor.copy(alpha = 0.20f),

                    radius = 18.dp.toPx(),

                    center = Offset(dotX, dotY)
                )

                drawCircle(
                    color = primaryColor,

                    radius = 8.dp.toPx(),

                    center = Offset(dotX, dotY)
                )
            }
        }
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    radius: Dp
): Modifier = this.drawBehind {

    val dash = 8.dp.toPx()
    val gap = 4.dp.toPx()

    drawRoundRect(
        color = color,

        cornerRadius = CornerRadius(radius.toPx()),

        style = Stroke(
            width = 1.5.dp.toPx(),

            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dash, gap),
                0f
            )
        )
    )
}
