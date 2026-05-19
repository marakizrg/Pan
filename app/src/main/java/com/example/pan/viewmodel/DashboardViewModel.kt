package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.data.local.UserPreferences
import com.example.pan.ui.screens.dashboard.ScheduleEntry
import org.json.JSONArray

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)

    var schedule by mutableStateOf<List<ScheduleEntry>>(emptyList())
        private set

    init {
        val userId = userPrefs.getCurrentUserId()
        if (userId != null && userPrefs.isScheduleImported(userId)) {
            schedule = loadSchedule()
        }
    }

    fun importSchedule() {
        schedule = loadSchedule()
        userPrefs.getCurrentUserId()?.let { userPrefs.saveScheduleImported(it) }
    }

    private fun loadSchedule(): List<ScheduleEntry> {
        val json = getApplication<Application>()
            .assets.open("classes.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        return List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            ScheduleEntry(
                day    = obj.getString("day"),
                time   = obj.getString("time"),
                course = obj.getString("course"),
                room   = obj.getString("room")
            )
        }
    }
}