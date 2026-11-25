package com.caller.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * User-managed business directory stored only in app-private preferences.
 * This store has no network client and never marks entries as verified.
 */
class BusinessDirectoryStore(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    @Synchronized
    fun getEntries(): List<LocalBusinessEntry> {
        val raw = preferences.getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val name = item.optString(KEY_NAME).trim()
                    val number = PhoneNumberNormalizer.normalize(item.optString(KEY_NUMBER))
                    if (name.isBlank() || PhoneNumberNormalizer.digitCount(number) !in 6..15) {
                        continue
                    }
                    add(
                        LocalBusinessEntry(
                            id = item.optString(KEY_ID).ifBlank { UUID.randomUUID().toString() },
                            name = name.take(MAX_NAME_LENGTH),
                            normalizedNumber = number,
                            category = item.optString(KEY_CATEGORY).take(MAX_CATEGORY_LENGTH),
                            website = item.optString(KEY_WEBSITE).take(MAX_WEBSITE_LENGTH),
                            sourceReference = item.optString(KEY_SOURCE).take(MAX_SOURCE_LENGTH),
                            createdAtMillis = item.optLong(KEY_CREATED_AT, 0L)
                        )
                    )
                }
            }.sortedByDescending { it.createdAtMillis }
        }.getOrDefault(emptyList())
    }

    @Synchronized
    fun addEntry(
        name: String,
        rawNumber: String,
        category: String = "",
        website: String = "",
        sourceReference: String = ""
    ): Boolean {
        val cleanName = name.trim()
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (cleanName.length !in 2..MAX_NAME_LENGTH) return false
        if (PhoneNumberNormalizer.digitCount(normalized) !in 6..15) return false

        val existing = getEntries()
        // An exact number maps to one directory entry to prevent ambiguous labels.
        if (existing.any { it.normalizedNumber == normalized }) return false

        val entry = LocalBusinessEntry(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            normalizedNumber = normalized,
            category = category.trim().take(MAX_CATEGORY_LENGTH),
            website = website.trim().take(MAX_WEBSITE_LENGTH),
            sourceReference = sourceReference.trim().take(MAX_SOURCE_LENGTH),
            createdAtMillis = System.currentTimeMillis()
        )
        val array = JSONArray()
        (listOf(entry) + existing).take(MAX_ENTRIES).forEach { item ->
            array.put(
                JSONObject()
                    .put(KEY_ID, item.id)
                    .put(KEY_NAME, item.name)
                    .put(KEY_NUMBER, item.normalizedNumber)
                    .put(KEY_CATEGORY, item.category)
                    .put(KEY_WEBSITE, item.website)
                    .put(KEY_SOURCE, item.sourceReference)
                    .put(KEY_CREATED_AT, item.createdAtMillis)
            )
        }
        preferences.edit().putString(KEY_ENTRIES, array.toString()).apply()
        return true
    }

    @Synchronized
    fun findByNumber(rawNumber: String): LocalBusinessEntry? =
        BusinessDirectoryEngine.findByNumber(rawNumber, getEntries())

    @Synchronized
    fun removeEntry(entryId: String) {
        val remaining = getEntries().filterNot { it.id == entryId }
        val array = JSONArray()
        remaining.forEach { item ->
            array.put(
                JSONObject()
                    .put(KEY_ID, item.id)
                    .put(KEY_NAME, item.name)
                    .put(KEY_NUMBER, item.normalizedNumber)
                    .put(KEY_CATEGORY, item.category)
                    .put(KEY_WEBSITE, item.website)
                    .put(KEY_SOURCE, item.sourceReference)
                    .put(KEY_CREATED_AT, item.createdAtMillis)
            )
        }
        preferences.edit().putString(KEY_ENTRIES, array.toString()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "caller_business_directory"
        const val KEY_ENTRIES = "entries_json"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_NUMBER = "number"
        const val KEY_CATEGORY = "category"
        const val KEY_WEBSITE = "website"
        const val KEY_SOURCE = "source_reference"
        const val KEY_CREATED_AT = "created_at"
        const val MAX_ENTRIES = 500
        const val MAX_NAME_LENGTH = 80
        const val MAX_CATEGORY_LENGTH = 60
        const val MAX_WEBSITE_LENGTH = 200
        const val MAX_SOURCE_LENGTH = 300
    }
}
