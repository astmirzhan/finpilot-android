package com.astmirzhan.finpilot.data

import android.content.Context

// Local demo authentication only. Not secure storage.
object AuthRepository {

    private const val PREFS_NAME = "finpilot_auth"
    private const val KEY_CURRENT_USER = "current_user"
    private const val USER_PREFIX = "user_"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isRegistered(context: Context, username: String): Boolean {
        return prefs(context).contains(USER_PREFIX + username)
    }

    fun register(context: Context, username: String, password: String): Boolean {
        if (isRegistered(context, username)) return false
        prefs(context).edit()
            .putString(USER_PREFIX + username, password)
            .putString(KEY_CURRENT_USER, username)
            .apply()
        return true
    }

    fun login(context: Context, username: String, password: String): Boolean {
        val stored = prefs(context).getString(USER_PREFIX + username, null) ?: return false
        if (stored != password) return false
        prefs(context).edit().putString(KEY_CURRENT_USER, username).apply()
        return true
    }

    fun currentUser(context: Context): String? {
        return prefs(context).getString(KEY_CURRENT_USER, null)
    }

    fun logout(context: Context) {
        prefs(context).edit().remove(KEY_CURRENT_USER).apply()
    }
}
