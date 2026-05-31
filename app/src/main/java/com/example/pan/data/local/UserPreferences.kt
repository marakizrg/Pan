package com.example.pan.data.local

import android.content.Context
import com.example.pan.data.local.db.CheckedCourseEntity
import com.example.pan.data.local.db.PanDatabase
import com.example.pan.data.local.db.UserEntity
import com.example.pan.data.model.User
import java.security.MessageDigest
import java.util.UUID

/**
 * Facade over local persistence.
 *
 *  - User accounts, password hashes, checked courses and the schedule-imported
 *    flag live in a Room/SQLite database ([PanDatabase]).
 *  - The lightweight session pointers (current user id, remember-me) stay in
 *    SharedPreferences — they are read synchronously during composition and are
 *    session state rather than data.
 *
 * Public method signatures are unchanged so existing callers need no edits.
 */
class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("pan_session", Context.MODE_PRIVATE)
    private val dao = PanDatabase.get(context).panDao()

    // ── Auth ─────────────────────────────────────────────────────────────────

    fun registerUser(user: User, password: String): Result<User> {
        if (dao.emailTaken(user.email, excludeId = ""))
            return Result.failure(Exception("Αυτό το email χρησιμοποιείται ήδη."))
        if (dao.usernameTaken(user.username, excludeId = ""))
            return Result.failure(Exception("Αυτό το όνομα χρήστη χρησιμοποιείται ήδη."))
        val userWithId = user.copy(id = UUID.randomUUID().toString())
        dao.insertUser(userWithId.toEntity(sha256(password)))
        return Result.success(userWithId)
    }

    fun loginUser(emailOrUsername: String, password: String): User? {
        val entity = dao.getUserByEmailOrUsername(emailOrUsername) ?: return null
        return if (entity.passwordHash == sha256(password)) entity.toUser() else null
    }

    // ── Session ───────────────────────────────────────────────────────────────

    fun setCurrentUserId(userId: String) {
        prefs.edit().putString("current_user_id", userId).apply()
    }

    fun getCurrentUserId(): String? = prefs.getString("current_user_id", null)

    fun clearCurrentUserId() {
        prefs.edit().remove("current_user_id").apply()
    }

    fun setRememberMe(remember: Boolean) {
        prefs.edit().putBoolean("remember_me", remember).apply()
    }

    fun isRemembered(): Boolean = prefs.getBoolean("remember_me", false)

    fun hasRememberedSession(): Boolean =
        isRemembered() && getCurrentUserId() != null

    // ── Schedule ──────────────────────────────────────────────────────────────

    fun saveScheduleImported(userId: String) {
        dao.setScheduleImported(userId, true)
    }

    fun isScheduleImported(userId: String): Boolean =
        dao.getUserById(userId)?.scheduleImported == true

    // ── Profile ───────────────────────────────────────────────────────────────

    fun getUserById(userId: String): User? = dao.getUserById(userId)?.toUser()

    fun updateUser(updatedUser: User): Result<Unit> {
        dao.getUserById(updatedUser.id)
            ?: return Result.failure(Exception("Χρήστης δεν βρέθηκε."))
        if (dao.emailTaken(updatedUser.email, excludeId = updatedUser.id))
            return Result.failure(Exception("Αυτό το email χρησιμοποιείται ήδη."))
        if (dao.usernameTaken(updatedUser.username, excludeId = updatedUser.id))
            return Result.failure(Exception("Αυτό το όνομα χρήστη χρησιμοποιείται ήδη."))
        dao.updateProfile(
            id        = updatedUser.id,
            username  = updatedUser.username,
            firstName = updatedUser.firstName,
            lastName  = updatedUser.lastName,
            email     = updatedUser.email,
            phone     = updatedUser.phone
        )
        return Result.success(Unit)
    }

    fun updatePassword(userId: String, newPassword: String) {
        dao.updatePassword(userId, sha256(newPassword))
    }

    // ── DiplomaPal ────────────────────────────────────────────────────────────

    fun saveCheckedCourses(userId: String, courseIds: Set<String>) {
        dao.clearCheckedCourses(userId)
        dao.insertCheckedCourses(courseIds.map { CheckedCourseEntity(userId, it) })
    }

    fun loadCheckedCourses(userId: String): Set<String> =
        dao.getCheckedCourseIds(userId).toSet()

    // ── Mapping ───────────────────────────────────────────────────────────────

    private fun User.toEntity(passwordHash: String) = UserEntity(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        passwordHash = passwordHash
    )

    private fun UserEntity.toUser() = User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone
    )

    private fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
