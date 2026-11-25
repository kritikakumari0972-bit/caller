package com.caller.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BusinessDirectoryEngineTest {

    private val entry = LocalBusinessEntry(
        id = "entry-1",
        name = "Example Service",
        normalizedNumber = "+911234567890",
        category = "Service",
        website = "https://example.invalid",
        sourceReference = "User supplied source note",
        createdAtMillis = 100L
    )

    @Test
    fun matchesOnlyTheExactNormalizedNumber() {
        val match = BusinessDirectoryEngine.findByNumber("+91 12345 67890", listOf(entry))
        assertEquals("Example Service", match?.name)
    }

    @Test
    fun doesNotGuessCountryCodeOrPartialMatches() {
        assertNull(BusinessDirectoryEngine.findByNumber("1234567890", listOf(entry)))
        assertNull(BusinessDirectoryEngine.findByNumber("67890", listOf(entry)))
    }

    @Test
    fun emptyNumberNeverMatches() {
        assertNull(BusinessDirectoryEngine.findByNumber("", listOf(entry)))
    }

    @Test
    fun aSourceNoteNeverMakesAnEntryVerified() {
        assertEquals("UNVERIFIED LOCAL ENTRY", entry.trustLabel)
        assertTrue(entry.evidenceDescription.contains("has not checked it"))
    }
}
