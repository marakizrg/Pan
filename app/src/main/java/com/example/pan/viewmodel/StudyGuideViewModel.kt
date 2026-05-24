package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.data.model.Course
import com.example.pan.util.SearchUtils
import org.json.JSONArray

class StudyGuideViewModel(application: Application) : AndroidViewModel(application) {

    private var allCourses by mutableStateOf<List<Course>>(emptyList())

    var searchQuery by mutableStateOf("")
        private set

    val filteredCourses: List<Course>
        get() = if (searchQuery.isBlank()) allCourses
                else {
                    val normalizedQuery = SearchUtils.normalizeForSearch(searchQuery)
                    allCourses.filter {
                        SearchUtils.normalizeForSearch(it.title).contains(normalizedQuery) ||
                        SearchUtils.normalizeForSearch(it.description).contains(normalizedQuery)
                    }
                }

    init {
        loadCourses()
    }

    private fun loadCourses() {
        try {
            val json = getApplication<Application>()
                .assets.open("study_guide.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(json)
            allCourses = List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                Course(
                    id            = obj.getString("lesson_id"),
                    title         = obj.getString("lesson_title"),
                    semester      = obj.optString("semester", "Δεν ορίζεται"),
                    ects          = obj.optString("ects", "Δεν ορίζεται"),
                    type          = obj.optString("lesson_type", "Μάθημα"),
                    professor     = obj.optString("teacher", "Δεν αναφέρεται"),
                    description   = obj.optString("about", "Δεν υπάρχει περιγραφή"),
                    prerequisites = obj.optString("prerequisites", "")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onQueryChange(query: String) { searchQuery = query }
    fun clearQuery()                  { searchQuery = ""    }
}
