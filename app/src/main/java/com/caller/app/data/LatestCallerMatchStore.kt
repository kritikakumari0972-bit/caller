package com.caller.app.data

import android.content.Context

data class LatestCallerMatch(
    val displayName: String,
    val phoneNumber: String,
    val matchedAtMillis: Long
)

/**
 * Stores only the most recent contact match, privately on this device.
 * It is intentionally not a general call log and can be cleared by the user.
 */
class LatestCallerMatchStore(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun save(match: LatestCallerMatch) {
        preferences.edit()
            .putString(KEY_NAME, match.displayName)
            .putString(KEY_NUMBER, match.phoneNumber)
            .putLong(KEY_MATCHED_AT, match.matchedAtMillis)
            .apply()
    }

    fun getLatest(): LatestCallerMatch? {
        val name = preferences.getString(KEY_NAME, null)?.takeIf { it.isNotBlank() }
            ?: return null
        val number = preferences.getString(KEY_NUMBER, "").orEmpty()
        val time = preferences.getLong(KEY_MATCHED_AT, 0L)
        if (time <= 0L) return null

        return LatestCallerMatch(
            displayName = name,
            phoneNumber = number,
            matchedAtMillis = time
        )
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "caller_latest_match"
        const val KEY_NAME = "display_name"
        const val KEY_NUMBER = "phone_number"
        const val KEY_MATCHED_AT = "matched_at_millis"
    }
}
