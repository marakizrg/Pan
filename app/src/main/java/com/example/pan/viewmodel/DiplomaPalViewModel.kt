package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.data.local.UserPreferences
import com.example.pan.model.CourseEntry
import com.example.pan.model.loadCoursesFromAssets

data class DegreeProgress(
    val acquiredECTS:        Double  = 0.0,
    val requiredECTS:        Double  = 240.0,
    val missingMandatory:    Int     = 0,
    val missingElectiveCore: Int     = 0,
    val missingElectiveECTS: Double  = 0.0,
    val progressFraction:    Float   = 0f,
    val progressPercent:     Int     = 0,
    val remainingECTS:       Double  = 240.0,
    val canGraduate:         Boolean = false
)

class DiplomaPalViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)

    var courses by mutableStateOf<List<CourseEntry>>(emptyList())
        private set

    val checkedCourses = mutableStateMapOf<String, Boolean>()

    var coursesExpanded by mutableStateOf(false)
        private set

    var progress by mutableStateOf(DegreeProgress())
        private set

    init {
        courses = loadCoursesFromAssets(application)
        val userId = userPrefs.getCurrentUserId()
        if (userId != null) {
            val saved = userPrefs.loadCheckedCourses(userId)
            saved.forEach { checkedCourses[it] = true }
        }
        recalculate()
    }

    fun toggleExpanded() {
        coursesExpanded = !coursesExpanded
    }

    fun toggleCourse(id: String) {
        checkedCourses[id] = !(checkedCourses[id] ?: false)
        recalculate()
        userPrefs.getCurrentUserId()?.let { userId ->
            val checked = checkedCourses.filterValues { it }.keys
            userPrefs.saveCheckedCourses(userId, checked)
        }
    }

    private fun recalculate() {
        val completed = courses.filter { checkedCourses[it.id] == true }

        val acquiredECTS  = completed.sumOf { it.ECTS }
        val requiredECTS  = 240.0

        val totalMandatory     = courses.count   { it.type == "mandatory" }
        val completedMandatory = completed.count { it.type == "mandatory" }
        val missingMandatory   = maxOf(0, totalMandatory - completedMandatory)

        val requiredElectiveCore   = 4
        val completedElectiveCore  = completed.count { it.type == "elective-core" }
        val missingElectiveCore    = maxOf(0, requiredElectiveCore - completedElectiveCore)

        val requiredElectiveECTS  = 64.0
        val acquiredElectiveECTS  = completed
            .filter { it.type == "elective" || it.type == "elective-core" }
            .sumOf { it.ECTS }
        val missingElectiveECTS   = maxOf(0.0, requiredElectiveECTS - acquiredElectiveECTS)

        val remainingECTS = maxOf(0.0, requiredECTS - acquiredECTS)
        val fraction      = (acquiredECTS / requiredECTS).toFloat().coerceIn(0f, 1f)
        val canGraduate   = missingMandatory   == 0 &&
                missingElectiveCore == 0 &&
                missingElectiveECTS == 0.0 &&
                acquiredECTS >= requiredECTS

        progress = DegreeProgress(
            acquiredECTS        = acquiredECTS,
            requiredECTS        = requiredECTS,
            missingMandatory    = missingMandatory,
            missingElectiveCore = missingElectiveCore,
            missingElectiveECTS = missingElectiveECTS,
            progressFraction    = fraction,
            progressPercent     = (fraction * 100).toInt(),
            remainingECTS       = remainingECTS,
            canGraduate         = canGraduate
        )
    }
}