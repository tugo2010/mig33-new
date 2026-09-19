package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar

data class WatchTarget(
    val id: String,
    val name: String,
    val type: String, // "User" or "Room"
    val status: String,
    val isOnline: Boolean,
    val avatarRes: Int = R.drawable.avatar_b4sejump
)

@Composable
fun WatchlistScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenDirectChat: (String) -> Unit,
    onOpenChatRoom: (String) -> Unit,
    onOpenProfile: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var watchlist by remember {
        mutableStateOf(
            listOf(
                WatchTarget("w1", "f1yingkit3", "User", "Playing in Danger 1", true, R.drawable.avatar_f1yingkit),
                WatchTarget("w2", "b4sejump", "User", "Online in Home Feeds", true, R.drawable.avatar_b4sejump),
                WatchTarget("w3", "Danger 1", "Room", "48 / 50 Active Users", true),
                WatchTarget("w4", "migCute", "User", "Mascot Bot", true, R.drawable.mig33_app_icon),
                WatchTarget("w5", "kalidawirx", "User", "Last seen 2 hours ago", false, R.drawable.avatar_b4sejump),
                WatchTarget("w6", "Game-warteg", "Room", "24 / 60 Active Users", true)
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Back",
                    leftAvatarRes = null,
                    centerTitle = "Watch List",
                    rightAvatarRes = null,
                    onLeftClick = onBack
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add to Watchlist")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFECEFF1))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WATCHED USERS & CHATROOMS (${watchlist.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                    Text(
                        text = "Alerts Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            items(watchlist) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.type == "User") {
                            Image(
                                painter = painterResource(id = item.avatarRes),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFF81D4FA), CircleShape)
                                    .clickable { onOpenProfile(item.name) },
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💬", fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (item.type == "User") onOpenProfile(item.name)
                                    else onOpenChatRoom(if (item.name.contains("Danger")) "danger_1" else "game_warteg")
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF263238)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${item.type})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                            Text(
                                text = if (item.type == "User") "${item.status} • Tap to view profile" else item.status,
                                fontSize = 11.sp,
                                color = if (item.type == "User") Color(0xFF0288D1) else Color(0xFF546E7A)
                            )
                        }

                        // Action button
                        if (item.type == "User") {
                            IconButton(
                                onClick = { onOpenProfile(item.name) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("👤", fontSize = 14.sp)
                            }
                            IconButton(
                                onClick = { onOpenDirectChat(item.name) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = Color(0xFF0288D1))
                            }
                        } else {
                            Button(
                                onClick = { onOpenChatRoom(if (item.name.contains("Danger")) "danger_1" else "game_warteg") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Enter", fontSize = 11.sp)
                            }
                        }

                        IconButton(
                            onClick = { watchlist = watchlist.filter { it.id != item.id } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFB0BEC5))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add to Watchlist", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1)) },
            text = {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Username or Room Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            watchlist = listOf(
                                WatchTarget(
                                    id = "w_${System.currentTimeMillis()}",
                                    name = newItemName,
                                    type = if (newItemName.lowercase().contains("room")) "Room" else "User",
                                    status = "Monitoring online status",
                                    isOnline = true
                                )
                            ) + watchlist
                            newItemName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
