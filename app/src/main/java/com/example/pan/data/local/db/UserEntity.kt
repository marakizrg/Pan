package com.example.pan.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A registered user. The password is stored only as a SHA-256 hash.
 * [scheduleImported] tracks whether the user has imported their class schedule.
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val scheduleImported: Boolean = false
)
