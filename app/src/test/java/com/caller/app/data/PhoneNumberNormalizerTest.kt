package com.caller.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberNormalizerTest {

    @Test
    fun removesFormattingCharacters() {
        assertEquals("+919876543210", PhoneNumberNormalizer.normalize("+91 98765-43210"))
    }

    @Test
    fun keepsLeadingPlusForInternationalNumbers() {
        assertEquals("+442071838750", PhoneNumberNormalizer.normalize("+44 (20) 7183 8750"))
    }

    @Test
    fun returnsEmptyForTextWithoutDigits() {
        assertEquals("", PhoneNumberNormalizer.normalize("not a phone number"))
    }

    @Test
    fun countsOnlyDigitsForBasicValidation() {
        assertEquals(10, PhoneNumberNormalizer.digitCount("+91 98765-43210"))
    }
}
