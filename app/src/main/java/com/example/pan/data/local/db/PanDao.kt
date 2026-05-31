package com.example.pan.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PanDao {

    // ── Users ─────────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): UserEntity?

    @Query(
        "SELECT * FROM users WHERE email = :emailOrUsername COLLATE NOCASE " +
            "OR username = :emailOrUsername COLLATE NOCASE LIMIT 1"
    )
    fun getUserByEmailOrUsername(emailOrUsername: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email COLLATE NOCASE AND id != :excludeId)")
    fun emailTaken(email: String, excludeId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username COLLATE NOCASE AND id != :excludeId)")
    fun usernameTaken(username: String, excludeId: String): Boolean

    @Query(
        "UPDATE users SET username = :username, firstName = :firstName, " +
            "lastName = :lastName, email = :email, phone = :phone WHERE id = :id"
    )
    fun updateProfile(
        id: String,
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    )

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :id")
    fun updatePassword(id: String, passwordHash: String)

    @Query("UPDATE users SET scheduleImported = :imported WHERE id = :id")
    fun setScheduleImported(id: String, imported: Boolean)

    // ── Checked courses ───────────────────────────────────────────────────────

    @Query("SELECT courseId FROM checked_courses WHERE userId = :userId")
    fun getCheckedCourseIds(userId: String): List<String>

    @Query("DELETE FROM checked_courses WHERE userId = :userId")
    fun clearCheckedCourses(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCheckedCourses(rows: List<CheckedCourseEntity>)
}
