package com.caller.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.caller.app.data.ContactsRepository
import com.caller.app.data.SavedContact
import com.caller.app.ui.theme.CallerBackground
import com.caller.app.ui.theme.CallerGreen
import com.caller.app.ui.theme.CallerLavender
import com.caller.app.ui.theme.CallerMuted
import com.caller.app.ui.theme.CallerPurple
import com.caller.app.ui.theme.CallerRed
import com.caller.app.ui.theme.CallerSurface
import com.caller.app.ui.theme.CallerText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ContactsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var contacts by remember { mutableStateOf<List<SavedContact>>(emptyList()) }
    var query by rememberSaveable { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        errorMessage = if (granted) null else
            "Contact access was not granted. Your contacts have not been read."
    }

    LaunchedEffect(hasPermission, refreshKey) {
        if (hasPermission) {
            loading = true
            errorMessage = null
            val result = withContext(Dispatchers.IO) {
                runCatching { ContactsRepository(context).loadPhoneContacts() }
            }
            result.onSuccess { contacts = it }
                .onFailure { error ->
                    errorMessage = when (error) {
                        is SecurityException -> "Contact access is unavailable. Check Android permissions."
                        else -> "Contacts could not be loaded. Please try again."
                    }
                    contacts = emptyList()
                }
            loading = false
        } else {
            contacts = emptyList()
        }
    }

    val trimmedQuery = query.trim()
    val queryDigits = trimmedQuery.filter(Char::isDigit)
    val visibleContacts = remember(contacts, trimmedQuery) {
        contacts.filter { contact ->
            trimmedQuery.isBlank() ||
                contact.displayName.contains(trimmedQuery, ignoreCase = true) ||
                (queryDigits.isNotEmpty() && contact.searchableDigits.contains(queryDigits))
        }
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
                        Icons.Outlined.People,
                        contentDescription = null,
                        tint = CallerPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Contacts", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = CallerText)
                Text("Search your saved phone contacts", fontSize = 12.sp, color = CallerMuted)
            }
            if (hasPermission) {
                TextButton(onClick = { refreshKey++ }, enabled = !loading) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh contacts")
                }
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
                    Icons.Outlined.Person,
                    contentDescription = null,
                    tint = CallerGreen,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Your contacts stay on this phone. Caller reads them only after you approve Android's contact permission; this feature does not upload them.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = CallerMuted
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (!hasPermission) {
            Spacer(Modifier.height(22.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CallerSurface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.People,
                        contentDescription = null,
                        tint = CallerPurple,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Find people you already know",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CallerText
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "Allow contact access to search names and phone numbers saved on this device. You can keep using Caller without granting it.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = CallerMuted
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Allow contact access")
                    }
                }
            }
            errorMessage?.let {
                Text(it, color = CallerRed, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            }
        } else {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search name or number") },
                placeholder = { Text("Type a name or last digits") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(15.dp)
            )
            Spacer(Modifier.height(12.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CallerPurple)
                }
                errorMessage != null -> Text(
                    errorMessage.orEmpty(),
                    color = CallerRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(8.dp)
                )
                contacts.isEmpty() -> EmptyContactsState(
                    modifier = Modifier.weight(1f),
                    title = "No phone contacts found",
                    detail = "Contacts with saved phone numbers will appear here."
                )
                visibleContacts.isEmpty() -> EmptyContactsState(
                    modifier = Modifier.weight(1f),
                    title = "No matching contacts",
                    detail = "Try another name or a different part of the phone number."
                )
                else -> {
                    Text(
                        "${visibleContacts.size} contact number${if (visibleContacts.size == 1) "" else "s"}",
                        fontSize = 12.sp,
                        color = CallerMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = visibleContacts,
                            key = { "${it.id}|${it.searchableDigits}" }
                        ) { contact ->
                            ContactRow(contact)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(contact: SavedContact) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CallerSurface, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = CallerLavender
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    contact.displayName.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = CallerPurple
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                contact.displayName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = CallerText
            )
            Text(contact.phoneNumber, fontSize = 12.sp, color = CallerMuted)
        }
    }
}

@Composable
private fun EmptyContactsState(
    modifier: Modifier = Modifier,
    title: String,
    detail: String
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.People, contentDescription = null, tint = CallerMuted, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(10.dp))
        Text(title, fontWeight = FontWeight.SemiBold, color = CallerText)
        Text(
            detail,
            fontSize = 12.sp,
            color = CallerMuted,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
