package com.example.pan.data.local

import android.content.Context
import com.example.pan.data.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("pan_users", Context.MODE_PRIVATE)

    // ── Auth ─────────────────────────────────────────────────────────────────

    fun registerUser(user: User, password: String): Result<User> {
        val users = loadUsers()
        if (users.any { it.first.email.equals(user.email, ignoreCase = true) })
            return Result.failure(Exception("Αυτό το email χρησιμοποιείται ήδη."))
        if (users.any { it.first.username.equals(user.username, ignoreCase = true) })
            return Result.failure(Exception("Αυτό το όνομα χρήστη χρησιμοποιείται ήδη."))
        val userWithId = user.copy(id = UUID.randomUUID().toString())
        saveUsers(users + (userWithId to sha256(password)))
        return Result.success(userWithId)
    }

    fun loginUser(emailOrUsername: String, password: String): User? {
        val hashed = sha256(password)
        return loadUsers().firstOrNull { (user, hash) ->
            hash == hashed &&
            (user.email.equals(emailOrUsername, ignoreCase = true) ||
             user.username.equals(emailOrUsername, ignoreCase = true))
        }?.first
    }

    // ── Session ───────────────────────────────────────────────────────────────

    fun setCurrentUserId(userId: String) {
        prefs.edit().putString("current_user_id", userId).apply()
    }

    fun getCurrentUserId(): String? = prefs.getString("current_user_id", null)

    fun clearCurrentUserId() {
        prefs.edit().remove("current_user_id").apply()
    }

    // ── Schedule ──────────────────────────────────────────────────────────────

    fun saveScheduleImported(userId: String) {
        prefs.edit().putBoolean("schedule_$userId", true).apply()
    }

    fun isScheduleImported(userId: String): Boolean =
        prefs.getBoolean("schedule_$userId", false)

    // ── Profile ───────────────────────────────────────────────────────────────

    fun getUserById(userId: String): User? =
        loadUsers().firstOrNull { it.first.id == userId }?.first

    fun updateUser(updatedUser: User): Result<Unit> {
        val users = loadUsers()
        val index = users.indexOfFirst { it.first.id == updatedUser.id }
        if (index == -1) return Result.failure(Exception("Χρήστης δεν βρέθηκε."))
        val others = users.filterIndexed { i, _ -> i != index }
        if (others.any { it.first.email.equals(updatedUser.email, ignoreCase = true) })
            return Result.failure(Exception("Αυτό το email χρησιμοποιείται ήδη."))
        if (others.any { it.first.username.equals(updatedUser.username, ignoreCase = true) })
            return Result.failure(Exception("Αυτό το όνομα χρήστη χρησιμοποιείται ήδη."))
        val updated = users.mapIndexed { i, pair -> if (i == index) updatedUser to pair.second else pair }
        saveUsers(updated)
        return Result.success(Unit)
    }

    fun updatePassword(userId: String, newPassword: String) {
        val users = loadUsers()
        val index = users.indexOfFirst { it.first.id == userId }
        if (index == -1) return
        val updated = users.mapIndexed { i, pair ->
            if (i == index) pair.first to sha256(newPassword) else pair
        }
        saveUsers(updated)
    }

    // ── DiplomaPal ────────────────────────────────────────────────────────────

    fun saveCheckedCourses(userId: String, courseIds: Set<String>) {
        val arr = JSONArray().also { a -> courseIds.forEach { a.put(it) } }
        prefs.edit().putString("courses_$userId", arr.toString()).apply()
    }

    fun loadCheckedCourses(userId: String): Set<String> {
        val json = prefs.getString("courses_$userId", "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).mapTo(mutableSetOf()) { arr.getString(it) }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private fun loadUsers(): List<Pair<User, String>> {
        val json = prefs.getString("users", "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            User(
                id          = obj.getString("id"),
                username    = obj.getString("username"),
                firstName   = obj.getString("firstName"),
                lastName    = obj.getString("lastName"),
                email       = obj.getString("email"),
                phone       = obj.getString("phone"),
                university  = obj.getString("university"),
                yearOfStudy = obj.getString("yearOfStudy")
            ) to obj.getString("passwordHash")
        }
    }

    private fun saveUsers(users: List<Pair<User, String>>) {
        val arr = JSONArray()
        users.forEach { (user, hash) ->
            arr.put(JSONObject().apply {
                put("id",           user.id)
                put("username",     user.username)
                put("firstName",    user.firstName)
                put("lastName",     user.lastName)
                put("email",        user.email)
                put("phone",        user.phone)
                put("university",   user.university)
                put("yearOfStudy",  user.yearOfStudy)
                put("passwordHash", hash)
            })
        }
        prefs.edit().putString("users", arr.toString()).apply()
    }

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
}