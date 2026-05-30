package com.example.pan

import com.example.pan.ui.screens.dashboard.ScheduleEntry
import com.example.pan.util.ScheduleChecker
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ScheduleCheckerTest {

    private val schedule: List<ScheduleEntry> by lazy {
        val json = File("src/main/assets/classes.json").readText()
        val arr = JSONArray(json)
        List(arr.length()) { i ->
            val obj = arr.getJSONObject(i)
            ScheduleEntry(
                day = obj.getString("day"),
                time = obj.getString("time"),
                course = obj.getString("course"),
                room = obj.getString("room")
            )
        }
    }

    @Test
    fun a23_lists_all_weekly_lessons() {
        val result = ScheduleChecker.checkRoom("A23", schedule)
        println("--- A23 result ---\n$result\n------------------")

        assertTrue("header missing", result.contains("Έχεις μαθήματα σε αυτή την αίθουσα"))
        assertTrue("Monday entry missing", result.contains("Δευτέρα 13:00"))
        assertTrue("Thursday 9:00 entry missing", result.contains("Πέμπτη 9:00"))
        assertTrue("Thursday 11:00 entry missing", result.contains("Πέμπτη 11:00"))
        assertTrue("Friday entry missing", result.contains("Παρασκευή 15:00"))
    }

    @Test
    fun unknown_room_returns_no_lessons_message() {
        val result = ScheduleChecker.checkRoom("Z999", schedule)
        assertEquals("Δεν έχεις μαθήματα σε αυτή την αίθουσα", result)
    }
}
