package com.caller.app.telephony

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import com.caller.app.data.BlockedNumberStore
import com.caller.app.data.ContactCallerLookup
import com.caller.app.data.LatestCallerMatch
import com.caller.app.data.LatestCallerMatchStore

/**
 * The Android system binds this service when Caller holds the call-screening role.
 * Calls are rejected only when the incoming number exactly matches the user's
 * explicit local blocklist. Saved-contact matches are informational only.
 */
class CallerScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val isIncoming = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        val incomingNumber = callDetails.handle?.schemeSpecificPart.orEmpty()
        val shouldBlock = isIncoming &&
            incomingNumber.isNotBlank() &&
            BlockedNumberStore(this).isBlocked(incomingNumber)

        // A contact match never changes the allow/block decision.
        if (isIncoming && !shouldBlock && incomingNumber.isNotBlank()) {
            runCatching {
                ContactCallerLookup(this).findContact(incomingNumber)
            }.getOrNull()?.let { match ->
                runCatching {
                    LatestCallerMatchStore(this).save(
                        LatestCallerMatch(
                            displayName = match.displayName,
                            phoneNumber = match.phoneNumber,
                            matchedAtMillis = System.currentTimeMillis()
                        )
                    )
                    CallerMatchNotifier.showMatch(this, match)
                }
            }
        }

        val response = CallResponse.Builder()
            .setDisallowCall(shouldBlock)
            .setRejectCall(shouldBlock)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(callDetails, response)
    }
}
