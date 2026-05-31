package com.example.pan.data.local.db

import androidx.room.Entity

/**
 * One row per (user, course) the user has marked as completed in DiplomaPal.
 * Composite primary key keeps each pair unique.
 */
@Entity(tableName = "checked_courses", primaryKeys = ["userId", "courseId"])
data class CheckedCourseEntity(
    val userId: String,
    val courseId: String
)
