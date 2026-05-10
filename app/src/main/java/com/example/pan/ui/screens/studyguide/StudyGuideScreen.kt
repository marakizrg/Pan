package com.example.pan.ui.screens.studyguide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pan.data.model.Course
import com.example.pan.viewmodel.StudyGuideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGuideScreen(
    onBack: () -> Unit,
    viewModel: StudyGuideViewModel = viewModel()
) {
    var expandedIds by remember { mutableStateOf(emptySet<String>()) }
    val filteredCourses = viewModel.filteredCourses

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Οδηγός Σπουδών", fontWeight = FontWeight.SemiBold) },
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
        ) {
            OutlinedTextField(
                value         = viewModel.searchQuery,
                onValueChange = viewModel::onQueryChange,
                placeholder   = { Text("Αναζήτηση μαθήματος...") },
                leadingIcon   = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon  = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearQuery) {
                            Icon(Icons.Default.Clear, contentDescription = "Καθαρισμός")
                        }
                    }
                },
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier            = Modifier.fillMaxSize()
            ) {
                if (filteredCourses.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text      = "Δεν βρέθηκαν μαθήματα για\n\"${viewModel.searchQuery}\"",
                                style     = MaterialTheme.typography.bodyMedium,
                                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(filteredCourses, key = { it.id }) { course ->
                    CourseCard(
                        course     = course,
                        isExpanded = course.id in expandedIds,
                        onToggle   = {
                            expandedIds = if (course.id in expandedIds)
                                expandedIds - course.id
                            else
                                expandedIds + course.id
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseCard(course: Course, isExpanded: Boolean, onToggle: () -> Unit) {
    val arrowAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label       = "arrowAngle"
    )

    ElevatedCard(
        onClick   = onToggle,
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(16.dp)
        ) {
            // ── Header (always visible) ──────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = course.title,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = "${course.semester} · ${course.ects} ECTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector        = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Σύμπτυξη" else "Επέκταση",
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.rotate(arrowAngle)
                )
            }

            // ── Expandable details ───────────────────────────────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    DetailRow(label = "Καθηγητής", value = course.professor)
                    Spacer(Modifier.height(8.dp))
                    DetailRow(label = "Εξάμηνο",   value = course.semester)
                    Spacer(Modifier.height(8.dp))
                    DetailRow(label = "ECTS",       value = course.ects.toString())
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text       = "Περιγραφή",
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = course.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text       = "$label:",
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier   = Modifier.width(84.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
