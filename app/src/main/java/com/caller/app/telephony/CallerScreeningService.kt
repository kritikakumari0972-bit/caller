package com.caller.app.telephony

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import com.caller.app.data.BlockedNumberStore

/**
 * The Android system binds this service when Caller holds the call-screening role.
 * Calls are allowed unless an incoming number exactly matches the user's blocklist.
 */
class CallerScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val isIncoming = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        val number = callDetails.handle?.schemeSpecificPart.orEmpty()
        val shouldBlock = isIncoming &&
            number.isNotBlank() &&
            BlockedNumberStore(this).isBlocked(number)

        val response = CallResponse.Builder()
            .setDisallowCall(shouldBlock)
            .setRejectCall(shouldBlock)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(callDetails, response)
    }
}
