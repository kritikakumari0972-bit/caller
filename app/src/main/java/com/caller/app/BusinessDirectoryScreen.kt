package com.caller.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caller.app.data.BusinessDirectoryStore
import com.caller.app.data.LocalBusinessEntry
import com.caller.app.data.PhoneNumberNormalizer
import com.caller.app.ui.theme.CallerBackground
import com.caller.app.ui.theme.CallerGreen
import com.caller.app.ui.theme.CallerMuted
import com.caller.app.ui.theme.CallerRed
import com.caller.app.ui.theme.CallerSurface
import com.caller.app.ui.theme.CallerText

@Composable
fun BusinessDirectoryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { BusinessDirectoryStore(context) }
    var entries by remember { mutableStateOf(store.getEntries()) }
    var name by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var website by rememberSaveable { mutableStateOf("") }
    var source by rememberSaveable { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CallerBackground)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column {
                Text(
                    "Business directory",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = CallerText
                )
                Text(
                    "Create your own on-device business number list.",
                    fontSize = 13.sp,
                    color = CallerMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CallerSurface)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            "Every entry is UNVERIFIED",
                            fontWeight = FontWeight.Bold,
                            color = CallerRed
                        )
                        Text(
                            "A source note is saved for your reference only. Caller does not check it or confirm who controls a number. Entries stay on this phone and are matched by exact number.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = CallerMuted,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add a business", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CallerText)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Business name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Business phone number") },
                    placeholder = { Text("+91 98765 43210") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Category (optional)") },
                    placeholder = { Text("Restaurant, delivery, bank...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Website (optional, reference only)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Where did you get this number? (optional)") },
                    placeholder = { Text("Official website page, printed bill, business card...") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp)
                )
                Button(
                    onClick = {
                        when {
                            name.trim().length !in 2..80 -> {
                                message = "Enter a business name (2–80 characters)."
                                messageIsError = true
                            }
                            PhoneNumberNormalizer.digitCount(number) !in 6..15 -> {
                                message = "Enter a phone number containing 6–15 digits."
                                messageIsError = true
                            }
                            store.addEntry(name, number, category, website, source) -> {
                                entries = store.getEntries()
                                name = ""
                                number = ""
                                category = ""
                                website = ""
                                source = ""
                                message = "Saved on this device as an unverified entry."
                                messageIsError = false
                            }
                            else -> {
                                message = "That number may already be in your directory, or the entry is invalid."
                                messageIsError = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save unverified entry")
                }
                message?.let {
                    Text(
                        it,
                        fontSize = 12.sp,
                        color = if (messageIsError) CallerRed else CallerGreen
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Your local entries", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CallerText)
                Text(entries.size.toString(), fontSize = 13.sp, color = CallerMuted)
            }
        }

        if (entries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CallerSurface)
                ) {
                    Text(
                        "No business entries yet. Add a number above. Caller does not preload sample or fabricated business listings.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = CallerMuted,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(entries, key = { it.id }) { entry ->
                BusinessEntryCard(
                    entry = entry,
                    onDelete = {
                        store.removeEntry(entry.id)
                        entries = store.getEntries()
                    }
                )
            }
        }
    }
}

@Composable
private fun BusinessEntryCard(
    entry: LocalBusinessEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CallerSurface)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row {
                Column(Modifier.weight(1f)) {
                    Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CallerText)
                    Text(entry.normalizedNumber, fontSize = 13.sp, color = CallerText)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete business entry", tint = CallerRed)
                }
            }
            if (entry.category.isNotBlank()) {
                Text(entry.category, fontSize = 12.sp, color = CallerMuted)
            }
            if (entry.website.isNotBlank()) {
                Text("Website reference: ${entry.website}", fontSize = 11.sp, color = CallerMuted)
            }
            Surface(
                color = Color(0xFFFFF0E8),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    entry.trustLabel,
                    color = CallerRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
            Text(
                entry.evidenceDescription,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = CallerMuted,
                modifier = Modifier.padding(top = 7.dp)
            )
            if (entry.sourceReference.isNotBlank()) {
                Text(
                    "Your source note: ${entry.sourceReference}",
                    fontSize = 11.sp,
                    color = CallerMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
