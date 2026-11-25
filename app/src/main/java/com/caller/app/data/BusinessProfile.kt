package com.caller.app.data

/**
 * A business contact manually entered by the device owner.
 *
 * Every record is unverified. Recording a website or source note does not
 * mean Caller has checked the source or confirmed control of the number.
 */
data class LocalBusinessEntry(
    val id: String,
    val name: String,
    val normalizedNumber: String,
    val category: String,
    val website: String,
    val sourceReference: String,
    val createdAtMillis: Long
) {
    val trustLabel: String
        get() = "UNVERIFIED LOCAL ENTRY"

    val evidenceDescription: String
        get() = if (sourceReference.isBlank()) {
            "No source reference recorded. Caller has not verified this entry."
        } else {
            "Source reference saved by you; Caller has not checked it. This entry remains unverified."
        }
}

object BusinessDirectoryEngine {

    /**
     * Exact local match only. No country-code guessing, fuzzy matching, or
     * inference about who owns the number.
     */
    fun findByNumber(
        rawNumber: String,
        entries: List<LocalBusinessEntry>
    ): LocalBusinessEntry? {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        if (normalized.isBlank()) return null
        return entries.firstOrNull { it.normalizedNumber == normalized }
    }
}
