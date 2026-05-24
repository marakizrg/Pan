package com.example.pan.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.pan.ui.screens.dashboard.ScheduleEntry
import com.example.pan.util.ClassroomMatcher
import com.example.pan.util.ScheduleChecker
import org.json.JSONArray

class ClassroomScannerViewModel(application: Application) : AndroidViewModel(application) {

    val schedule: List<ScheduleEntry> by lazy { loadSchedule() }

    var detectedRoom by mutableStateOf<String?>(null)
        private set
    var scheduleMessage by mutableStateOf<String?>(null)
        private set
    var isScanning by mutableStateOf(true)
        private set

    private var consecutiveCount = 0
    private var lastCandidate: String? = null
    private var lastAnalysisMs = 0L

    /**
     * Called from the ML Kit success listener (main thread) for each analysed frame.
     * Implements 500 ms throttle and 3-consecutive-frame stability before locking.
     */
    fun onTextDetected(candidates: List<String>) {
        if (!isScanning) return

        val now = System.currentTimeMillis()
        if (now - lastAnalysisMs < 500L) return
        lastAnalysisMs = now

        val match = candidates.firstNotNullOfOrNull { ClassroomMatcher.normalize(it) }

        if (match == lastCandidate) {
            consecutiveCount++
        } else {
            consecutiveCount = 1
            lastCandidate = match
        }

        if (consecutiveCount >= 2 && match != null) {
            detectedRoom    = match
            scheduleMessage = ScheduleChecker.checkRoom(match, schedule)
            isScanning      = false
        }
    }

    fun reset() {
        detectedRoom    = null
        scheduleMessage = null
        isScanning      = true
        consecutiveCount = 0
        lastCandidate   = null
        lastAnalysisMs  = 0L
    }

    private fun loadSchedule(): List<ScheduleEntry> = try {
        val json = getApplication<Application>()
            .assets.open("classes.json").bufferedReader().use { it.readText() }
        val arr  = JSONArray(json)
        List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            ScheduleEntry(
                day    = obj.getString("day"),
                time   = obj.getString("time"),
                course = obj.getString("course"),
                room   = obj.getString("room")
            )
        }
    } catch (_: Exception) { emptyList() }
}