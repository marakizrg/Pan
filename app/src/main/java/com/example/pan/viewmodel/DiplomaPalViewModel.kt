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

    val expandedCategories = mutableStateMapOf<String, Boolean>()

    var progress by mutableStateOf(DegreeProgress())
        private set

    init {
        courses = loadCoursesFromAssets(application)
        val userId = userPrefs.getCurrentUserId()
        if (userId != null) {
            val saved = userPrefs.loadCheckedCourses(userId)
            saved.forEach { checkedCourses[it] = true }
        }
        
        // Initialize expanded categories
        expandedCategories["mandatory"] = false
        expandedCategories["elective-core"] = false
        expandedCategories["elective"] = false
        expandedCategories["free"] = false
        
        recalculate()
    }

    fun toggleCategory(category: String) {
        expandedCategories[category] = !(expandedCategories[category] ?: false)
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
        val electiveCoreCompleted  = completed.filter { it.type == "elective-core" }.sortedBy { it.ECTS }
        val missingElectiveCore    = maxOf(0, requiredElectiveCore - electiveCoreCompleted.size)

        val requiredElectiveECTS  = 64.0
        // Only elective-core courses beyond the first 4 contribute to the 64 ECTS requirement
        val electiveCoreSurplusECTS = if (electiveCoreCompleted.size > 4) {
            electiveCoreCompleted.drop(4).sumOf { it.ECTS }
        } else {
            0.0
        }
        
        val acquiredElectiveECTS  = completed
            .filter { it.type == "elective" || it.type == "free" }
            .sumOf { it.ECTS } + electiveCoreSurplusECTS

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