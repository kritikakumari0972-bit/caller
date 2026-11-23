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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.caller.app.data.PhoneNumberNormalizer
import com.caller.app.ui.theme.CallerBackground
import com.caller.app.ui.theme.CallerGreen
import com.caller.app.ui.theme.CallerGreenSoft
import com.caller.app.ui.theme.CallerLavender
import com.caller.app.ui.theme.CallerMuted
import com.caller.app.ui.theme.CallerPurple
import com.caller.app.ui.theme.CallerRed
import com.caller.app.ui.theme.CallerSurface
import com.caller.app.ui.theme.CallerText

private enum class CallerTab(val title: String) {
    HOME("Home"),
    CONTACTS("Contacts"),
    BLOCKLIST("Blocklist"),
    SETTINGS("Settings")
}

@Composable
fun CallerApp(
    screeningRoleHeld: Boolean,
    onEnableScreening: () -> Unit
) {
    val context = LocalContext.current
    val store = remember { BlockedNumberStore(context) }
    var selectedTab by rememberSaveable { mutableStateOf(CallerTab.HOME.name) }

    Scaffold(
        containerColor = CallerBackground,
        bottomBar = {
            NavigationBar(containerColor = CallerSurface) {
                CallerTab.values().forEach { tab ->
                    val selected = selectedTab == tab.name
                    val icon = when (tab) {
                        CallerTab.HOME -> Icons.Outlined.Home
                        CallerTab.CONTACTS -> Icons.Outlined.People
                        CallerTab.BLOCKLIST -> Icons.Outlined.Block
                        CallerTab.SETTINGS -> Icons.Outlined.Settings
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab.name },
                        icon = { Icon(icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (CallerTab.valueOf(selectedTab)) {
            CallerTab.HOME -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                screeningRoleHeld = screeningRoleHeld,
                blockedCount = store.getBlockedNumbers().size,
                onEnableScreening = onEnableScreening,
                onOpenBlocklist = { selectedTab = CallerTab.BLOCKLIST.name }
            )
            CallerTab.CONTACTS -> ContactsScreen(
                modifier = Modifier.padding(innerPadding)
            )
            CallerTab.BLOCKLIST -> BlocklistScreen(
                modifier = Modifier.padding(innerPadding),
                store = store
            )
            CallerTab.SETTINGS -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                screeningRoleHeld = screeningRoleHeld,
                onEnableScreening = onEnableScreening
            )
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    screeningRoleHeld: Boolean,
    blockedCount: Int,
    onEnableScreening: () -> Unit,
    onOpenBlocklist: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(16.dp),
                color = CallerLavender
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Call,
                        contentDescription = null,
                        tint = CallerPurple,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Caller", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = CallerText)
                Text("Know who gets through.", fontSize = 13.sp, color = CallerMuted)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (screeningRoleHeld) CallerGreenSoft else CallerSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (screeningRoleHeld) Color(0xFFC9EBDD) else CallerLavender,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = if (screeningRoleHeld) CallerGreen else CallerPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            if (screeningRoleHeld) "Call protection is on" else "Set up call protection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = CallerText
                        )
                        Text(
                            if (screeningRoleHeld) "Caller can screen calls using your blocklist."
                            else "Choose Caller as your call-screening app.",
                            fontSize = 13.sp,
                            color = CallerMuted
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                if (!screeningRoleHeld) {
                    Button(
                        onClick = onEnableScreening,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Enable call protection")
                    }
                    Text(
                        "Android will show a system role prompt. You stay in control.",
                        fontSize = 12.sp,
                        color = CallerMuted,
                        modifier = Modifier.padding(top = 9.dp)
                    )
                } else {
                    Surface(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Only numbers you add to your blocklist are rejected.",
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            fontSize = 13.sp,
                            color = CallerGreen
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = blockedCount.toString(),
                label = "Blocked numbers",
                accent = CallerPurple
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "On device",
                label = "Blocklist storage",
                accent = CallerGreen
            )
        }

        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CallerSurface)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "Your protection, your rules",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = CallerText
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    "Add numbers you do not want to hear from. Caller keeps this list in the app's private storage and checks it on your phone.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = CallerMuted
                )
                TextButton(onClick = onOpenBlocklist, modifier = Modifier.align(Alignment.End)) {
                    Text("Manage blocklist")
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CallerSurface)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Coming next", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CallerText)
                RoadmapLine("Verified business caller lookup")
                RoadmapLine("Spam reports with confidence indicators")
                RoadmapLine("Smart dialer and recent-call tools")
                RoadmapLine("Optional, transparent community intelligence")
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "Caller is an early build. It does not identify every unknown number yet.",
            fontSize = 12.sp,
            color = CallerMuted,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CallerSurface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = accent)
            Spacer(Modifier.height(5.dp))
            Text(label, fontSize = 12.sp, color = CallerMuted)
        }
    }
}

@Composable
private fun RoadmapLine(text: String) {
    Row(
        modifier = Modifier.padding(top = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(Modifier.size(7.dp), shape = CircleShape, color = CallerPurple) {}
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = CallerMuted)
    }
}

@Composable
private fun BlocklistScreen(
    modifier: Modifier = Modifier,
    store: BlockedNumberStore
) {
    var numberInput by rememberSaveable { mutableStateOf("") }
    var blockedNumbers by remember { mutableStateOf(store.getBlockedNumbers()) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Text("Blocklist", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = CallerText)
        Text(
            "Numbers you choose to keep out.",
            fontSize = 14.sp,
            color = CallerMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )
        OutlinedTextField(
            value = numberInput,
            onValueChange = {
                numberInput = it
                message = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Phone number") },
            placeholder = { Text("+91 98765 43210") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                val digitCount = PhoneNumberNormalizer.digitCount(numberInput)
                if (digitCount !in 6..15) {
                    message = "Enter a number containing 6 to 15 digits."
                } else if (store.add(numberInput)) {
                    blockedNumbers = store.getBlockedNumbers()
                    numberInput = ""
                    message = "Number added to this device's blocklist."
                } else {
                    message = "That number could not be added."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Add to blocklist")
        }
        message?.let {
            Text(
                it,
                color = if (it.startsWith("Enter") || it.startsWith("That")) CallerRed else CallerGreen,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Blocked numbers", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CallerText)
            Text(blockedNumbers.size.toString(), fontSize = 13.sp, color = CallerMuted)
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFE9E9F1))

        if (blockedNumbers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.Block, contentDescription = null, tint = CallerMuted, modifier = Modifier.size(34.dp))
                Spacer(Modifier.height(10.dp))
                Text("Your blocklist is empty", fontWeight = FontWeight.SemiBold, color = CallerText)
                Text("Add a number above to create your first rule.", fontSize = 12.sp, color = CallerMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(blockedNumbers, key = { it }) { blockedNumber ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CallerSurface, RoundedCornerShape(14.dp))
                            .padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(blockedNumber, fontWeight = FontWeight.SemiBold, color = CallerText)
                            Text("Stored on this device", fontSize = 11.sp, color = CallerMuted)
                        }
                        IconButton(
                            onClick = {
                                store.remove(blockedNumber)
                                blockedNumbers = store.getBlockedNumbers()
                            }
                        ) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Remove blocked number",
                                tint = CallerRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    screeningRoleHeld: Boolean,
    onEnableScreening: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Text("Settings", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = CallerText)
        Text(
            "Control how Caller works on your phone.",
            fontSize = 14.sp,
            color = CallerMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )
        SettingsRow(
            title = "Call-screening role",
            detail = if (screeningRoleHeld) "Enabled in Android settings" else "Not enabled",
            enabled = screeningRoleHeld
        )
        if (!screeningRoleHeld) {
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onEnableScreening,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Set up call protection")
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CallerSurface)
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = CallerPurple)
                    Spacer(Modifier.width(9.dp))
                    Text("Privacy", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CallerText)
                }
                PrivacyPoint("Your blocklist is stored locally in this app.")
                PrivacyPoint("Contacts are read only after you grant permission and stay on this phone.")
                PrivacyPoint("This build does not upload contacts or call history.")
                PrivacyPoint("No remote caller lookup is active in this milestone.")
                PrivacyPoint("Call screening starts only after you grant Android's role.")
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CallerSurface)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("About Caller", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CallerText)
                Spacer(Modifier.height(5.dp))
                Text("Version 0.2.0 · Local contacts", fontSize = 13.sp, color = CallerMuted)
                Text(
                    "Caller does not automatically classify unknown numbers as spam. Only your explicit blocklist is used for rejection.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = CallerMuted,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    detail: String,
    enabled: Boolean
) {
    val statusColor = if (enabled) CallerGreen else CallerRed
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CallerSurface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Security, contentDescription = null, tint = CallerPurple)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = CallerText)
                Text(detail, fontSize = 12.sp, color = CallerMuted)
            }
            Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
                Text(
                    if (enabled) "ON" else "OFF",
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun PrivacyPoint(text: String) {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.padding(top = 6.dp).size(6.dp),
            shape = CircleShape,
            color = CallerGreen
        ) {}
        Spacer(Modifier.width(9.dp))
        Text(text, fontSize = 13.sp, lineHeight = 18.sp, color = CallerMuted)
    }
}
