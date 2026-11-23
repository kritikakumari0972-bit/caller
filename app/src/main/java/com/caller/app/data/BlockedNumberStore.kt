package com.caller.app.data

import android.content.Context

/**
 * Stores only numbers the user explicitly adds to their local blocklist.
 * Entries remain in this app's private SharedPreferences file.
 */
class BlockedNumberStore(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun getBlockedNumbers(): List<String> =
        preferences.getStringSet(KEY_BLOCKED_NUMBERS, emptySet())
            .orEmpty()
            .toList()
            .sorted()

    fun isBlocked(rawNumber: String): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (normalized.isBlank()) return false
        return preferences.getStringSet(KEY_BLOCKED_NUMBERS, emptySet())
            .orEmpty()
            .contains(normalized)
    }

    fun add(rawNumber: String): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (normalized.isBlank()) return false

        val updated = preferences.getStringSet(KEY_BLOCKED_NUMBERS, emptySet())
            .orEmpty()
            .toMutableSet()
        updated.add(normalized)
        preferences.edit().putStringSet(KEY_BLOCKED_NUMBERS, updated).apply()
        return true
    }

    fun remove(rawNumber: String) {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (normalized.isBlank()) return

        val updated = preferences.getStringSet(KEY_BLOCKED_NUMBERS, emptySet())
            .orEmpty()
            .toMutableSet()
        updated.remove(normalized)
        preferences.edit().putStringSet(KEY_BLOCKED_NUMBERS, updated).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "caller_local_settings"
        const val KEY_BLOCKED_NUMBERS = "blocked_numbers"
    }
}
