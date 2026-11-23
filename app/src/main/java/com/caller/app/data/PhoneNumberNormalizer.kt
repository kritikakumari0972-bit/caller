package com.caller.app.data

/**
 * A conservative normalizer for local exact-match rules.
 * It does not infer country-specific equivalence or rewrite country codes.
 */
object PhoneNumberNormalizer {

    fun normalize(rawNumber: String): String {
        val trimmed = rawNumber.trim()
        val digits = trimmed.filter(Char::isDigit)
        if (digits.isEmpty()) return ""
        return if (trimmed.startsWith("+")) "+$digits" else digits
    }

    fun digitCount(rawNumber: String): Int = rawNumber.count(Char::isDigit)
}
