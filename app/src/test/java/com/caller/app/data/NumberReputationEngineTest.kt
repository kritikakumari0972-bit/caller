package com.caller.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberReputationEngineTest {

    @Test
    fun reportsAreMatchedAfterPhoneNumberNormalization() {
        val reports = listOf(
            NumberReport(
                id = "report-1",
                normalizedNumber = "+919876543210",
                category = SpamCategory.TELEMARKETING,
                note = "",
                createdAtMillis = 100L
            )
        )

        val summary = NumberReputationEngine.summarize("+91 98765-43210", reports)

        assertTrue(summary.hasLocalReports)
        assertEquals(1, summary.localReportCount)
        assertEquals(listOf(SpamCategory.TELEMARKETING), summary.categories)
    }

    @Test
    fun unmatchedNumberHasNoLocalReports() {
        val summary = NumberReputationEngine.summarize("+91 90000 11111", emptyList())

        assertFalse(summary.hasLocalReports)
        assertEquals(0, summary.localReportCount)
        assertEquals("No reports on this device", summary.label)
    }

    @Test
    fun aLocalReportIsNotPresentedAsVerifiedFraud() {
        val summary = NumberReputationEngine.summarize(
            "9876543210",
            listOf(
                NumberReport(
                    id = "report-2",
                    normalizedNumber = "9876543210",
                    category = SpamCategory.SUSPECTED_SCAM,
                    note = "Asked for an OTP",
                    createdAtMillis = 200L
                )
            )
        )

        assertEquals("Unverified local reports", summary.label)
    }

    @Test
    fun blankNumberNeverMatches() {
        val summary = NumberReputationEngine.summarize(
            "",
            listOf(
                NumberReport(
                    id = "report-3",
                    normalizedNumber = "9876543210",
                    category = SpamCategory.OTHER,
                    note = "",
                    createdAtMillis = 300L
                )
            )
        )

        assertEquals(0, summary.localReportCount)
    }
}
