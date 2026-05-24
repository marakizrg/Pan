package com.example.pan.util

import com.example.pan.ui.screens.dashboard.ScheduleEntry
import java.util.Calendar

object ScheduleChecker {

    private val dayFromCalendar = mapOf(
        Calendar.MONDAY    to "Monday",
        Calendar.TUESDAY   to "Tuesday",
        Calendar.WEDNESDAY to "Wednesday",
        Calendar.THURSDAY  to "Thursday",
        Calendar.FRIDAY    to "Friday",
        Calendar.SATURDAY  to "Saturday",
        Calendar.SUNDAY    to "Sunday"
    )

    private val greekDayName = mapOf(
        "Monday"    to "Δευτέρα",
        "Tuesday"   to "Τρίτη",
        "Wednesday" to "Τετάρτη",
        "Thursday"  to "Πέμπτη",
        "Friday"    to "Παρασκευή",
        "Saturday"  to "Σάββατο",
        "Sunday"    to "Κυριακή"
    )

    private const val CLASS_DURATION_MINUTES = 90

    /**
     * Cross-references [room] against [schedule] for today.
     * Returns a localised Greek message suitable for the UI overlay.
     */
    fun checkRoom(room: String, schedule: List<ScheduleEntry>): String {
        val now      = Calendar.getInstance()
        val todayKey = dayFromCalendar[now.get(Calendar.DAY_OF_WEEK)] ?: return NO_CLASS_MSG

        // Use canonical IDs for robust comparison between OCR results and schedule data
        val roomID = ClassroomMatcher.getCanonicalId(room)

        val todayEntries = schedule.filter { entry ->
            entry.day.equals(todayKey, ignoreCase = true) &&
            ClassroomMatcher.getCanonicalId(entry.room) == roomID
        }

        if (todayEntries.isEmpty()) return NO_CLASS_MSG

        val currentMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        for (entry in todayEntries) {
            val start = parseMinutes(entry.time) ?: continue
            if (currentMin in start until (start + CLASS_DURATION_MINUTES)) {
                return "Έχεις μάθημα τώρα: ${entry.course}"
            }
        }

        val upcoming = todayEntries
            .filter  { (parseMinutes(it.time) ?: -1) > currentMin }
            .minByOrNull { parseMinutes(it.time) ?: Int.MAX_VALUE }

        return if (upcoming != null) {
            val greekDay = greekDayName[todayKey] ?: todayKey
            "Επόμενο μάθημα εδώ: ${upcoming.course} — $greekDay ${upcoming.time}"
        } else {
            NO_CLASS_MSG
        }
    }

    private fun parseMinutes(time: String): Int? {
        val parts = time.split(":")
        if (parts.size < 2) return null
        val h = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        return h * 60 + m
    }

    private const val NO_CLASS_MSG = "Δεν έχεις μάθημα σε αυτή την αίθουσα σήμερα"
}