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
    "Μαράσλειο Μέγαρο"
)

// FLOORS


private val floors = listOf(
    "0/Ισόγειο"
)

// GROUND FLOOR ROOMS
private val groundFloorRooms = listOf(
    "Αμφιθέατρο Α",
    "Αμφιθέατρο Β",
    "Αμφιθέατρο Γ"
)

// DOT POSITIONS

private val dotPositions = mapOf(

    "Αμφιθέατρο Β" to Pair(0.20f, 0.50f),

    "Αμφιθέατρο Α" to Pair(0.50f, 0.45f),

    "Αμφιθέατρο Γ" to Pair(0.80f, 0.50f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLocatorScreen(
    onBack: () -> Unit
) {

    // BUILDING
    var selectedBuilding by remember {
        mutableStateOf(buildings.first())
    }

    var buildingExpanded by remember {
        mutableStateOf(false)
    }

    // FLOOR
    var selectedFloor by remember {
        mutableStateOf(floors.first())
    }

    var floorExpanded by remember {
        mutableStateOf(false)
    }

    // CLASSROOM
    var selectedClassroom by remember {
        mutableStateOf(groundFloorRooms.first())
    }

    var classroomExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Πλοήγηση σε Κτήρια",
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

            // BUILDING SELECTOR
            BuildingSelector(
                expanded = buildingExpanded,
                selected = selectedBuilding,
                onExpandedChange = {
                    buildingExpanded = it
                },
                onSelect = {
                    selectedBuilding = it
                    buildingExpanded = false
                }
            )

            // FLOOR SELECTOR
            FloorSelector(
                expanded = floorExpanded,
                selected = selectedFloor,
                onExpandedChange = {
                    floorExpanded = it
                },
                onSelect = {
                    selectedFloor = it
                    floorExpanded = false
                }
            )

            // CLASSROOM SELECTOR
            ClassroomSelector(
                expanded = classroomExpanded,
                selected = selectedClassroom,
                onExpandedChange = {
                    classroomExpanded = it
                },
                onSelect = {
                    selectedClassroom = it
                    classroomExpanded = false
                }
            )

            // LOCATION INFO
            LocationInstructions(
                selectedBuilding = selectedBuilding,
                selectedFloor = selectedFloor,
                selectedClassroom = selectedClassroom
            )

            // FLOOR PLAN
            FloorPlanCard(
                selectedBuilding = selectedBuilding,
                selectedClassroom = selectedClassroom
            )
        }
    }
}

// --------------------------------------------------
// BUILDING SELECTOR
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildingSelector(
    expanded: Boolean,
    selected: String,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "Επιλογή Κτηρίου",
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
                    Text("Κτήριο")
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

                buildings.forEach { building ->

                    DropdownMenuItem(
                        text = {
                            Text(building)
                        },

                        onClick = {
                            onSelect(building)
                        }
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// FLOOR SELECTOR
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloorSelector(
    expanded: Boolean,
    selected: String,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "Επιλογή Ορόφου",
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
                    Text("Όροφος")
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

                floors.forEach { floor ->

                    DropdownMenuItem(
                        text = {
                            Text(floor)
                        },

                        onClick = {
                            onSelect(floor)
                        }
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// CLASSROOM SELECTOR
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassroomSelector(
    expanded: Boolean,
    selected: String,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "Επιλογή Αίθουσας",
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
                    Text("Αίθουσα")
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

                groundFloorRooms.forEach { room ->

                    DropdownMenuItem(
                        text = {
                            Text(room)
                        },

                        onClick = {
                            onSelect(room)
                        }
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// LOCATION INFO
// --------------------------------------------------

@Composable
private fun LocationInstructions(
    selectedBuilding: String,
    selectedFloor: String,
    selectedClassroom: String
) {

    val borderColor = MaterialTheme.colorScheme.outline

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
            text = "Η επιλεγμένη αίθουσα εμφανίζεται με μπλε δείκτη στην κάτοψη.",

            style = MaterialTheme.typography.bodySmall,

            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --------------------------------------------------
// FLOOR PLAN CARD
// --------------------------------------------------

@Composable
private fun FloorPlanCard(
    selectedBuilding: String,
    selectedClassroom: String
) {

    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary

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
                text = "Κάτοψη Ισογείου - $selectedBuilding",

                style = MaterialTheme.typography.titleSmall,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {

                val w = size.width
                val h = size.height

                val thick = Stroke(3.dp.toPx())
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

                // LEFT AMPHITHEATER
                room(0.05f, 0.18f, 0.30f, 0.78f)

                // CENTER AMPHITHEATER
                room(0.36f, 0.08f, 0.64f, 0.82f)

                // RIGHT AMPHITHEATER
                room(0.70f, 0.18f, 0.95f, 0.78f)

                // DOT
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

// DASHED BORDER

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