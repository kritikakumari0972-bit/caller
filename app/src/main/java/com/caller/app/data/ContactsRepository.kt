package com.caller.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract.CommonDataKinds.Phone

data class SavedContact(
    val id: String,
    val displayName: String,
    val phoneNumber: String
) {
    val searchableDigits: String
        get() = PhoneNumberNormalizer.normalize(phoneNumber).filter(Char::isDigit)
}

/**
 * Reads the device's contacts only after the app has been granted READ_CONTACTS.
 * Nothing in this repository uploads or persists the returned contact rows.
 */
class ContactsRepository(private val context: Context) {

    fun loadPhoneContacts(): List<SavedContact> {
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("Contact permission has not been granted.")
        }

        val projection = arrayOf(
            Phone._ID,
            Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER
        )
        val sortOrder = "${Phone.DISPLAY_NAME} COLLATE LOCALIZED ASC, ${Phone.NUMBER} ASC"

        val cursor = context.contentResolver.query(
            Phone.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        ) ?: return emptyList()

        return cursor.use { result ->
            val rowIdColumn = result.getColumnIndexOrThrow(Phone._ID)
            val contactIdColumn = result.getColumnIndexOrThrow(Phone.CONTACT_ID)
            val nameColumn = result.getColumnIndexOrThrow(Phone.DISPLAY_NAME)
            val numberColumn = result.getColumnIndexOrThrow(Phone.NUMBER)

            val uniqueContacts = linkedMapOf<String, SavedContact>()
            while (result.moveToNext()) {
                val contactId = result.getString(contactIdColumn).orEmpty()
                val rowId = result.getString(rowIdColumn).orEmpty()
                val name = result.getString(nameColumn).orEmpty().ifBlank { "Unknown contact" }
                val number = result.getString(numberColumn).orEmpty().trim()

                if (number.isNotBlank()) {
                    val entry = SavedContact(
                        id = rowId.ifBlank { contactId },
                        displayName = name,
                        phoneNumber = number
                    )
                    uniqueContacts["$contactId|${PhoneNumberNormalizer.normalize(number)}"] = entry
                }
            }
            uniqueContacts.values.toList()
        }
    }
}
