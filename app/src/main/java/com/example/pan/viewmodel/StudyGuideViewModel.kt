package com.example.pan.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pan.data.model.Course
import com.example.pan.data.model.mockCourses
import com.example.pan.util.SearchUtils

class StudyGuideViewModel : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    val filteredCourses: List<Course>
        get() = if (searchQuery.isBlank()) mockCourses
                else {
                    val normalizedQuery = SearchUtils.normalizeForSearch(searchQuery)
                    mockCourses.filter {
                        SearchUtils.normalizeForSearch(it.title).contains(normalizedQuery)
                    }
                }

    fun onQueryChange(query: String) { searchQuery = query }
    fun clearQuery()                  { searchQuery = ""    }
}
