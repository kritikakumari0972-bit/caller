package com.caller.app.data

/**
 * A user-created note about an unwanted call. Reports are stored locally
 * and are not proof that a number is fraudulent.
 */
enum class SpamCategory(val title: String) {
    SUSPECTED_SCAM("Suspected scam"),
    TELEMARKETING("Telemarketing"),
    ROBOCALL("Automated / recorded call"),
    REPEATED_UNWANTED("Repeated unwanted calls"),
    OTHER("Other unwanted call")
}

data class NumberReport(
    val id: String,
    val normalizedNumber: String,
    val category: SpamCategory,
    val note: String,
    val createdAtMillis: Long
)

data class NumberReputationSummary(
    val number: String,
    val localReportCount: Int,
    val categories: List<SpamCategory>,
    val latestReportAtMillis: Long?
) {
    val hasLocalReports: Boolean get() = localReportCount > 0

    /**
     * Deliberately avoids an accusation or numeric fraud score:
     * this app only knows what this device's owner entered.
     */
    val label: String
        get() = if (hasLocalReports) "Unverified local reports" else "No reports on this device"
}

object NumberReputationEngine {

    fun summarize(
        rawNumber: String,
        reports: List<NumberReport>
    ): NumberReputationSummary {
        val normalized = PhoneNumberNormalizer.normalize(rawNumber)
        val matches = if (normalized.isBlank()) {
            emptyList()
        } else {
            reports.filter { it.normalizedNumber == normalized }
        }

        return NumberReputationSummary(
            number = normalized,
            localReportCount = matches.size,
            categories = matches.map { it.category }.distinct(),
            latestReportAtMillis = matches.maxOfOrNull { it.createdAtMillis }
        )
    }
}
