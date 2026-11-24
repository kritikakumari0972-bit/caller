package com.caller.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup

data class MatchedCaller(
    val displayName: String,
    val phoneNumber: String
)

/**
 * Looks up a single incoming number in the device's Contacts Provider.
 * This is a local lookup: it does not use a network service or upload data.
 */
class ContactCallerLookup(private val context: Context) {

    fun findContact(rawNumber: String): MatchedCaller? {
        if (rawNumber.isBlank()) return null
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val lookupUri = Uri.withAppendedPath(
            PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(rawNumber)
        )
        val projection = arrayOf(
            PhoneLookup.DISPLAY_NAME,
            PhoneLookup.NUMBER
        )

        return context.contentResolver.query(
            lookupUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) {
                null
            } else {
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME)
                ).orEmpty().trim()
                val number = cursor.getString(
                    cursor.getColumnIndexOrThrow(PhoneLookup.NUMBER)
                ).orEmpty().trim()

                if (name.isBlank()) null else MatchedCaller(
                    displayName = name,
                    phoneNumber = number.ifBlank { rawNumber }
                )
            }
        }
    }
}
