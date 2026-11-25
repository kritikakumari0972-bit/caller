package com.caller.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.caller.app.data.LatestCallerMatch
import com.caller.app.data.LatestCallerMatchStore
import com.caller.app.ui.theme.CallerBackground
import com.caller.app.ui.theme.CallerGreen
import com.caller.app.ui.theme.CallerLavender
import com.caller.app.ui.theme.CallerMuted
import com.caller.app.ui.theme.CallerPurple
import com.caller.app.ui.theme.CallerRed
import com.caller.app.ui.theme.CallerSurface
import com.caller.app.ui.theme.CallerText
import java.text.DateFormat
import java.util.Date

@Composable
fun CallerIdScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { LatestCallerMatchStore(context) }
    var latestMatch by remember { mutableStateOf<LatestCallerMatch?>(null) }

    fun refresh() {
        latestMatch = store.getLatest()
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CallerBackground)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(15.dp),
                color = CallerLavender
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        tint = CallerPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Caller ID", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = CallerText)
                Text("Contacts + local business entries", fontSize = 12.sp, color = CallerMuted)
            }
            IconButton(onClick = ::refresh) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Refresh latest match")
            }
        }

        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CallerSurface)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = CallerGreen
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Caller checks exact incoming numbers against contacts and your own local business directory. Records stay on this phone; there is no public reverse-number lookup.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = CallerMuted
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Latest matched caller",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CallerText
        )
        Spacer(Modifier.height(10.dp))

        if (latestMatch == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CallerSurface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Call,
                        contentDescription = null,
                        tint = CallerMuted,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No matched calls yet",
                        fontWeight = FontWeight.SemiBold,
                        color = CallerText
                    )
                    Text(
                        "When an incoming number matches a saved contact or a local business entry, its details will appear here.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = CallerMuted,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        } else {
            val match = latestMatch!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CallerSurface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = CallerLavender
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    match.displayName.firstOrNull()?.uppercase() ?: "?",
                                    color = CallerPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                match.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CallerText
                            )
                            Text(match.sourceLabel, fontSize = 12.sp, color = if (match.sourceLabel.contains("Unverified")) CallerMuted else CallerGreen)
                            match.sourceNote?.let { note ->
                                Text(note, fontSize = 11.sp, lineHeight = 15.sp, color = CallerMuted, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("Phone number", fontSize = 11.sp, color = CallerMuted)
                    Text(
                        match.phoneNumber,
                        fontSize = 16.sp,
                        color = CallerText,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Matched at", fontSize = 11.sp, color = CallerMuted)
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Date(match.matchedAtMillis)),
                        fontSize = 13.sp,
                        color = CallerText,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    store.clear()
                    refresh()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = CallerRed)
                Spacer(Modifier.width(8.dp))
                Text("Clear latest match")
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEBFF))
        ) {
            Text(
                "Contact matches come from your address book. Business matches come only from entries you added and are always unverified; an exact number match does not prove who controls that number.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = CallerText,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}
