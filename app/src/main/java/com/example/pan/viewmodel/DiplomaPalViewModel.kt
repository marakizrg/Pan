package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.model.CourseEntry
import com.example.pan.model.loadCoursesFromAssets

data class DegreeProgress(
    val acquiredEcts:     Double  = 0.0,
    val requiredEcts:     Double  = 240.0,
    val missingMandatory: Int     = 0,
    val missingElective:  Int     = 0,
    val progressFraction: Float   = 0f,
    val progressPercent:  Int     = 0,
    val remainingEcts:    Double  = 240.0,
    val canGraduate:      Boolean = false
)

class DiplomaPalViewModel(application: Application) : AndroidViewModel(application) {

    var courses by mutableStateOf<List<CourseEntry>>(emptyList())
        private set

    val checkedCourses = mutableStateMapOf<String, Boolean>()

    var coursesExpanded by mutableStateOf(false)
        private set

    var progress by mutableStateOf(DegreeProgress())
        private set

    init {
        courses = loadCoursesFromAssets(application)
        recalculate()
    }

    fun toggleExpanded() {
        coursesExpanded = !coursesExpanded
    }

    fun toggleCourse(id: String) {
        checkedCourses[id] = !(checkedCourses[id] ?: false)
        recalculate()
    }

    private fun recalculate() {
        val completed = courses.filter { checkedCourses[it.id] == true }

        val acquiredEcts       = completed.sumOf { it.ects }
        val requiredEcts       = 240.0
        val completedMandatory = completed.count { it.type == "mandatory" }
        val totalMandatory     = courses.count   { it.type == "mandatory" }
        val completedElective  = completed.count { it.type == "elective" }
        val requiredElective   = 5

        val missingMandatory = maxOf(0, totalMandatory  - completedMandatory)
        val missingElective  = maxOf(0, requiredElective - completedElective)
        val remainingEcts    = maxOf(0.0, requiredEcts - acquiredEcts)
        val fraction         = (acquiredEcts / requiredEcts).toFloat().coerceIn(0f, 1f)
        val canGraduate      = missingMandatory == 0 && missingElective == 0 && acquiredEcts >= requiredEcts

        progress = DegreeProgress(
            acquiredEcts     = acquiredEcts,
            requiredEcts     = requiredEcts,
            missingMandatory = missingMandatory,
            missingElective  = missingElective,
            progressFraction = fraction,
            progressPercent  = (fraction * 100).toInt(),
            remainingEcts    = remainingEcts,
            canGraduate      = canGraduate
        )
    }
}