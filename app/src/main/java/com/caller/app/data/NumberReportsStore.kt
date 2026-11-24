package com.caller.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Private on-device storage for reports that the current user enters.
 * Nothing is synchronized or uploaded by this store.
 */
class NumberReportsStore(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    @Synchronized
    fun getReports(): List<NumberReport> {
        val raw = preferences.getString(KEY_REPORTS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val category = runCatching {
                        SpamCategory.valueOf(item.optString(KEY_CATEGORY))
                    }.getOrDefault(SpamCategory.OTHER)
                    val number = PhoneNumberNormalizer.normalize(item.optString(KEY_NUMBER))
                    if (number.isBlank()) continue
                    add(
                        NumberReport(
                            id = item.optString(KEY_ID).ifBlank { UUID.randomUUID().toString() },
                            normalizedNumber = number,
                            category = category,
                            note = item.optString(KEY_NOTE),
                            createdAtMillis = item.optLong(KEY_CREATED_AT, 0L)
                        )
                    )
                }
            }.sortedByDescending { it.createdAtMillis }
        }.getOrDefault(emptyList())
    }

    @Synchronized
    fun addReport(
        rawNumber: String,
        category: SpamCategory,
        note: String = ""
    ): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (PhoneNumberNormalizer.digitCount(normalized) !in 6..15) return false

        val existing = getReports().toMutableList()
        existing.add(
            NumberReport(
                id = UUID.randomUUID().toString(),
                normalizedNumber = normalized,
                category = category,
                note = note.trim().take(MAX_NOTE_LENGTH),
                createdAtMillis = System.currentTimeMillis()
            )
        )

        val array = JSONArray()
        existing.sortedByDescending { it.createdAtMillis }
            .take(MAX_STORED_REPORTS)
            .forEach { report ->
                array.put(
                    JSONObject()
                        .put(KEY_ID, report.id)
                        .put(KEY_NUMBER, report.normalizedNumber)
                        .put(KEY_CATEGORY, report.category.name)
                        .put(KEY_NOTE, report.note)
                        .put(KEY_CREATED_AT, report.createdAtMillis)
                )
            }

        preferences.edit().putString(KEY_REPORTS, array.toString()).apply()
        return true
    }

    @Synchronized
    fun removeReport(reportId: String) {
        val remaining = getReports().filterNot { it.id == reportId }
        val array = JSONArray()
        remaining.forEach { report ->
            array.put(
                JSONObject()
                    .put(KEY_ID, report.id)
                    .put(KEY_NUMBER, report.normalizedNumber)
                    .put(KEY_CATEGORY, report.category.name)
                    .put(KEY_NOTE, report.note)
                    .put(KEY_CREATED_AT, report.createdAtMillis)
            )
        }
        preferences.edit().putString(KEY_REPORTS, array.toString()).apply()
    }

    @Synchronized
    fun clearAll() {
        preferences.edit().remove(KEY_REPORTS).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "caller_number_reports"
        const val KEY_REPORTS = "reports_json"
        const val KEY_ID = "id"
        const val KEY_NUMBER = "number"
        const val KEY_CATEGORY = "category"
        const val KEY_NOTE = "note"
        const val KEY_CREATED_AT = "created_at"
        const val MAX_STORED_REPORTS = 500
        const val MAX_NOTE_LENGTH = 180
    }
}
