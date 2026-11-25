package com.caller.app.telephony

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import com.caller.app.data.BlockedNumberStore
import com.caller.app.data.BusinessDirectoryStore
import com.caller.app.data.ContactCallerLookup
import com.caller.app.data.LatestCallerMatch
import com.caller.app.data.LatestCallerMatchStore
import java.util.concurrent.Executors

/**
 * Android binds this service while Caller holds the call-screening role.
 *
 * The allow/block decision is returned immediately. A local contact lookup
 * runs afterwards on a worker thread so a slow Contacts Provider query cannot
 * delay Android's call-screening response.
 */
class CallerScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val isIncoming = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        val incomingNumber = callDetails.handle?.schemeSpecificPart.orEmpty()
        val shouldBlock = isIncoming &&
            incomingNumber.isNotBlank() &&
            BlockedNumberStore(this).isBlocked(incomingNumber)

        val response = CallResponse.Builder()
            .setDisallowCall(shouldBlock)
            .setRejectCall(shouldBlock)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        // Contact matches are informational only and never affect the call decision.
        respondToCall(callDetails, response)

        if (isIncoming && !shouldBlock && incomingNumber.isNotBlank()) {
            val appContext = applicationContext
            contactLookupExecutor.execute {
                val contactMatch = runCatching {
                    ContactCallerLookup(appContext).findContact(incomingNumber)
                }.getOrNull()

                if (contactMatch != null) {
                    runCatching {
                        LatestCallerMatchStore(appContext).save(
                            LatestCallerMatch(
                                displayName = contactMatch.displayName,
                                phoneNumber = contactMatch.phoneNumber,
                                matchedAtMillis = System.currentTimeMillis(),
                                sourceLabel = "Matched from saved contacts"
                            )
                        )
                        CallerMatchNotifier.showMatch(appContext, contactMatch)
                    }
                } else {
                    // Contact names take priority. Business entries are a separate,
                    // user-managed local source and are never treated as verified.
                    val business = runCatching {
                        BusinessDirectoryStore(appContext).findByNumber(incomingNumber)
                    }.getOrNull()
                    business?.let { entry ->
                        runCatching {
                            LatestCallerMatchStore(appContext).save(
                                LatestCallerMatch(
                                    displayName = entry.name,
                                    phoneNumber = entry.normalizedNumber,
                                    matchedAtMillis = System.currentTimeMillis(),
                                    sourceLabel = "Unverified local business entry",
                                    sourceNote = entry.evidenceDescription
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private companion object {
        val contactLookupExecutor = Executors.newSingleThreadExecutor()
    }
}
