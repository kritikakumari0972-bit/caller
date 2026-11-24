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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.caller.app.data.BlockedNumberStore
import com.caller.app.data.NumberReport
import com.caller.app.data.NumberReportsStore
import com.caller.app.data.NumberReputationEngine
import com.caller.app.data.PhoneNumberNormalizer
import com.caller.app.data.SpamCategory
import com.caller.app.ui.theme.CallerBackground
import com.caller.app.ui.theme.CallerGreen
import com.caller.app.ui.theme.CallerGreenSoft
import com.caller.app.ui.theme.CallerLavender
import com.caller.app.ui.theme.CallerMuted
import com.caller.app.ui.theme.CallerPurple
import com.caller.app.ui.theme.CallerRed
import com.caller.app.ui.theme.CallerSurface
import com.caller.app.ui.theme.CallerText
import java.text.DateFormat
import java.util.Date

@Composable
fun SpamReportsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { NumberReportsStore(context) }
    val blockedStore = remember { BlockedNumberStore(context) }
    var blockedNumbers by remember { mutableStateOf(blockedStore.getBlockedNumbers().toSet()) }
    var reports by remember { mutableStateOf(store.getReports()) }
    var number by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(SpamCategory.SUSPECTED_SCAM.name) }
    var categoryMenuOpen by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val category = SpamCategory.valueOf(selectedCategory)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CallerBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 18.dp,
            vertical = 18.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(15.dp),
                    color = CallerLavender
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = CallerPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Spam reports", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = CallerText)
                    Text("Your private number notes", fontSize = 12.sp, color = CallerMuted)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CallerGreenSoft)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Report, contentDescription = null, tint = CallerGreen)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Reports are saved only on this phone. They are personal notes, not verified fraud findings, and are not shared with other Caller users.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = CallerText
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CallerSurface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Add a number report",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CallerText
                    )

                    OutlinedTextField(
                        value = number,
                        onValueChange = {
                            number = it
                            message = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone number") },
                        placeholder = { Text("+91 98765 43210") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Box {
                        OutlinedButton(
                            onClick = { categoryMenuOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(category.title)
                        }
                        DropdownMenu(
                            expanded = categoryMenuOpen,
                            onDismissRequest = { categoryMenuOpen = false }
                        ) {
                            SpamCategory.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        selectedCategory = option.name
                                        categoryMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(180) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Optional note") },
                        placeholder = { Text("What happened? Avoid sharing sensitive details.") },
                        minLines = 1,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (PhoneNumberNormalizer.digitCount(number) in 6..15) {
                        val status = NumberReputationEngine.summarize(number, reports)
                        Surface(
                            color = if (status.hasLocalReports) CallerRed.copy(alpha = 0.08f) else CallerLavender,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(
                                    status.label,
                                    color = if (status.hasLocalReports) CallerRed else CallerPurple,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                if (status.hasLocalReports) {
                                    Text(
                                        "${status.localReportCount} report(s) on this device",
                                        color = CallerMuted,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Text(
                                        "No report for this number is saved on this device.",
                                        color = CallerMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (PhoneNumberNormalizer.digitCount(number) !in 6..15) {
                                message = "Enter a phone number containing 6 to 15 digits."
                            } else if (store.addReport(number, category, note)) {
                                reports = store.getReports()
                                number = ""
                                note = ""
                                message = "Report saved privately on this device."
                            } else {
                                message = "This number could not be saved."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Save report")
                    }

                    message?.let {
                        Text(
                            it,
                            color = if (it.startsWith("Enter") || it.startsWith("could")) CallerRed else CallerGreen,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Saved reports", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CallerText)
                    Text(
                        "${reports.size} stored on this device",
                        fontSize = 11.sp,
                        color = CallerMuted
                    )
                }
                if (reports.isNotEmpty()) {
                    Text(
                        "Delete individual reports below",
                        fontSize = 10.sp,
                        color = CallerMuted
                    )
                }
            }
        }

        if (reports.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = CallerMuted,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("No reports yet", fontWeight = FontWeight.SemiBold, color = CallerText)
                    Text(
                        "Reports you add will appear here.",
                        fontSize = 12.sp,
                        color = CallerMuted
                    )
                }
            }
        } else {
            items(items = reports, key = { it.id }) { report ->
                ReportRow(
                    report = report,
                    isBlocked = blockedNumbers.contains(report.normalizedNumber),
                    onBlock = {
                        blockedStore.add(report.normalizedNumber)
                        blockedNumbers = blockedStore.getBlockedNumbers().toSet()
                    },
                    onDelete = {
                        store.removeReport(report.id)
                        reports = store.getReports()
                    }
                )
            }
        }
    }
}

@Composable
private fun ReportRow(
    report: NumberReport,
    isBlocked: Boolean,
    onBlock: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CallerSurface)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = CallerLavender
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = CallerPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(report.normalizedNumber, fontWeight = FontWeight.SemiBold, color = CallerText)
                Text(report.category.title, fontSize = 12.sp, color = CallerRed)
                if (report.note.isNotBlank()) {
                    Text(report.note, fontSize = 11.sp, color = CallerMuted)
                }
                Text(
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(Date(report.createdAtMillis)),
                    fontSize = 10.sp,
                    color = CallerMuted
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    enabled = !isBlocked,
                    onClick = onBlock
                ) {
                    Icon(
                        Icons.Outlined.Block,
                        contentDescription = if (isBlocked) "Already blocked" else "Block this number",
                        tint = if (isBlocked) CallerMuted else CallerPurple
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete report",
                        tint = CallerRed
                    )
                }
            }
        }
    }
}
