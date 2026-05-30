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

    private val dayOrder = mapOf(
        "Monday" to 1, "Tuesday" to 2, "Wednesday" to 3, "Thursday" to 4,
        "Friday" to 5, "Saturday" to 6, "Sunday" to 7
    )

    private const val CLASS_DURATION_MINUTES = 90

    /**
     * Cross-references [room] against [schedule] across the whole week.
     * Lists every lesson scheduled in this room, sorted by day/time, and
     * highlights an ongoing lesson if one is currently in progress.
     */
    fun checkRoom(room: String, schedule: List<ScheduleEntry>): String {
        val roomID = ClassroomMatcher.getCanonicalId(room)

        val roomEntries = schedule
            .filter { ClassroomMatcher.getCanonicalId(it.room) == roomID }
            .sortedWith(compareBy(
                { dayOrder[it.day] ?: 99 },
                { parseMinutes(it.time) ?: Int.MAX_VALUE }
            ))

        if (roomEntries.isEmpty()) return NO_CLASS_MSG

        val now        = Calendar.getInstance()
        val todayKey   = dayFromCalendar[now.get(Calendar.DAY_OF_WEEK)]
        val currentMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val ongoing = roomEntries.firstOrNull { entry ->
            entry.day.equals(todayKey, ignoreCase = true) &&
                (parseMinutes(entry.time)?.let { currentMin in it until (it + CLASS_DURATION_MINUTES) } == true)
        }

        val list = roomEntries.joinToString("\n") { entry ->
            val greekDay = greekDayName[entry.day] ?: entry.day
            "• $greekDay ${entry.time} — ${entry.course}"
        }

        val header = if (ongoing != null) {
            "Έχεις μάθημα τώρα: ${ongoing.course}\n\nΌλα τα μαθήματα σε αυτή την αίθουσα:"
        } else {
            "Έχεις μαθήματα σε αυτή την αίθουσα:"
        }

        return "$header\n$list"
    }

    private fun parseMinutes(time: String): Int? {
        val parts = time.split(":")
        if (parts.size < 2) return null
        val h = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        return h * 60 + m
    }

    private const val NO_CLASS_MSG = "Δεν έχεις μαθήματα σε αυτή την αίθουσα"
}