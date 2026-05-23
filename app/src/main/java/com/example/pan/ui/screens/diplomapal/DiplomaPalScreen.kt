package com.example.pan.ui.screens.diplomapal

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pan.model.CourseEntry
import com.example.pan.viewmodel.DegreeProgress
import com.example.pan.viewmodel.DiplomaPalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiplomaPalScreen(
    onBack: () -> Unit,
    onNavigateToStudyGuide: () -> Unit = {},
    viewModel: DiplomaPalViewModel     = viewModel()
) {
    val progress = viewModel.progress

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diploma Pal", fontWeight = FontWeight.SemiBold) },
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
            ECTSProgressCard(progress)
            StudyGuideButton(onClick = onNavigateToStudyGuide)
            CoursesCard(
                courses         = viewModel.courses,
                checkedCourses  = viewModel.checkedCourses,
                expanded        = viewModel.coursesExpanded,
                onToggleExpand  = { viewModel.toggleExpanded() },
                onToggleCourse  = { viewModel.toggleCourse(it) }
            )
            CriteriaCard(progress)
        }
    }
}

@Composable
private fun ECTSProgressCard(progress: DegreeProgress) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress    = { progress.progressFraction },
                    modifier    = Modifier.fillMaxSize(),
                    strokeWidth = 14.dp,
                    trackColor  = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap   = StrokeCap.Round,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "${progress.acquiredECTS}",
                        style      = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text  = "/ ${progress.requiredECTS}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = "ECTS",
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "${progress.progressPercent}% Ολοκληρώθηκε",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Απομένουν ${progress.remainingECTS} ECTS ακόμη",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (progress.canGraduate) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text       = "Πληροίς τις προϋποθέσεις πτυχίου!",
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StudyGuideButton(onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text          = "ΟΔΗΓΟΣ ΣΠΟΥΔΩΝ",
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
@Composable
private fun CoursesCard(
    courses:        List<CourseEntry>,
    checkedCourses: Map<String, Boolean>,
    expanded:       Boolean,
    onToggleExpand: () -> Unit,
    onToggleCourse: (String) -> Unit
) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text       = "ΜΑΘΗΜΑΤΑ",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector        = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Σύμπτυξη" else "Ανάπτυξη",
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))

                    val mandatory = courses.filter { it.type == "mandatory" }
                    val elective  = courses.filter { it.type == "elective"  }
                    val electiveCore = courses.filter { it.type == "elective-core" }

                    if (mandatory.isNotEmpty()) {
                        CourseGroupHeader("ΥΠΟΧΡΕΩΤΙΚΑ")
                        mandatory.forEachIndexed { index, course ->
                            if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            CourseRow(
                                course  = course,
                                checked = checkedCourses[course.id] ?: false,
                                onToggle = { onToggleCourse(course.id) }
                            )
                        }
                    }

                    if (elective.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        CourseGroupHeader("ΕΠΙΛΟΓΗΣ")
                        elective.forEachIndexed { index, course ->
                            if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            CourseRow(
                                course   = course,
                                checked  = checkedCourses[course.id] ?: false,
                                onToggle = { onToggleCourse(course.id) }
                            )
                        }
                    }

                    if (electiveCore.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        CourseGroupHeader("ΕΠΙΛΟΓΗΣ ΠΥΡΗΝΑ")
                        electiveCore.forEachIndexed { index, course ->
                            if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            CourseRow(
                                course  = course,
                                checked = checkedCourses[course.id] ?: false,
                                onToggle = { onToggleCourse(course.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseGroupHeader(label: String) {
    Text(
        text       = label,
        style      = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier   = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun CourseRow(
    course:  CourseEntry,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked         = checked,
            onCheckedChange = { onToggle() }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = course.name,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text  = course.id,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text       = "${course.ECTS} ECTS",
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CriteriaCard(progress: DegreeProgress) {
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
                    Icons.Default.Calculate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text       = "ΥΠΟΛΟΓΙΣΜΟΣ ΜΕ ΒΑΣΗ ΤΙΣ ΠΡΟΫΠΟΘΕΣΕΙΣ ΑΠΟΚΤΗΣΗΣ ΠΤΥΧΙΟΥ",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                text       = "ΧΡΕΙΑΖΕΤΑΙ:",
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            // Επίπεδο 1: Επιτυχία στα 22 υποχρεωτικά μαθήματα πυρήνα του προγράμματος σπουδών.
            CriteriaItem(
                missing = progress.missingMandatory,
                label   = "ΥΠΟΧΡΕΩΤΙΚΑ ΜΑΘΗΜΑΤΑ (22)"
            )
            Spacer(Modifier.height(12.dp))

            // Επίπεδο 2: Επιτυχία σε τουλάχιστον 4 από τα μαθήματα επιλογής πυρήνα του προγράμματος σπουδών.
            CriteriaItem(
                missing = progress.missingElectiveCore,
                label   = "ΜΑΘΗΜΑΤΑ ΕΠΙΛΟΓΗΣ ΠΥΡΗΝΑ (≥4)"
            )
            Spacer(Modifier.height(12.dp))

            // Επίπεδο 3: Λήψη τουλάχιστον 64 μονάδων ECTS προερχόμενες από μαθήματα επιλογής πυρήνα και επιλογής
            CriteriaItem(
                missing      = progress.missingElectiveECTS.toInt(),
                label        = "ECTS ΑΠΟ ΜΑΘΗΜΑΤΑ ΕΠΙΛΟΓΗΣ ΚΑΙ ΕΠΙΛΟΓΗΣ ΠΥΡΗΝΑ (≥64)",
                customSuffix = if (progress.missingElectiveECTS > 0)
                    " (απομένουν ${progress.missingElectiveECTS} ECTS)"
                else null
            )
        }
    }
}

@Composable
private fun CriteriaItem(
    missing:      Int,
    label:        String,
    customSuffix: String? = null
) {
    val met = missing == 0
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = if (met) Icons.Default.CheckCircle
            else     Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint               = if (met) MaterialTheme.colorScheme.primary
            else     MaterialTheme.colorScheme.error,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text       = if (met) "✓" else "$missing",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color      = if (met) MaterialTheme.colorScheme.primary
            else     MaterialTheme.colorScheme.error
        )
        Text(
            text  = label + (customSuffix ?: ""),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}