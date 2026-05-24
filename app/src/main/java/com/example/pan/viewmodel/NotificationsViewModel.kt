package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray

data class NotificationEntry(
    val title: String,
    val course: String,
    val date: String,
    val content: String
)

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    var notifications by mutableStateOf<List<NotificationEntry>>(emptyList())
        private set

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        try {
            val json = getApplication<Application>()
                .assets.open("notifications.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(json)
            notifications = List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                NotificationEntry(
                    title   = obj.getString("title"),
                    course  = obj.getString("course"),
                    date    = obj.getString("date"),
                    content = obj.optString("content", "")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
