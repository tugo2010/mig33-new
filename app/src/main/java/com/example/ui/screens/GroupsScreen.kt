package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar

data class MigGroupItem(
    val id: String,
    val name: String,
    val category: String,
    val membersCount: Int,
    val description: String,
    val icon: String,
    val isJoined: Boolean = false
)

@Composable
fun GroupsScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenGroupChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var groupsList by remember {
        mutableStateOf(
            listOf(
                MigGroupItem("g1", "Gamerz Guild Indonesia", "Gaming", 1420, "Komunitas gamer mig33 se-Indonesia, mabar & tips.", "🎮", true),
                MigGroupItem("g2", "Trivia Masters World", "Competitions", 980, "Global trivia champions & quiz showdown winners.", "🏆", true),
                MigGroupItem("g3", "Avatar Stylists & Fashion", "Lifestyle", 2150, "Share coolest mig33 avatar outfit combos & items.", "👗", false),
                MigGroupItem("g4", "Singapore & Malaysia Chillout", "Regional", 650, "Coffee meetups, weekend chats & music lovers.", "☕", false),
                MigGroupItem("g5", "Anime & Manga Otaku Club", "Entertainment", 1830, "Discuss latest anime episodes, memes & fanart.", "✨", false),
                MigGroupItem("g6", "mig33 Poetry & Quotes", "Literature", 410, "Daily inspirational quotes, status messages & poems.", "📜", false)
            )
        )
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var newGroupDesc by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Back",
                    leftAvatarRes = null,
                    centerTitle = "Groups",
                    rightAvatarRes = null,
                    onLeftClick = onBack
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Group")
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
                        text = "COMMUNITY GROUPS (${groupsList.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                    Text(
                        text = "Joined: ${groupsList.count { it.isJoined }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            items(groupsList) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE1F5FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(group.icon, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = group.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = "${group.category} • ${group.membersCount} members",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = group.description,
                            fontSize = 12.sp,
                            color = Color(0xFF455A64)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (group.isJoined) {
                                OutlinedButton(
                                    onClick = {
                                        groupsList = groupsList.map {
                                            if (it.id == group.id) it.copy(isJoined = false, membersCount = it.membersCount - 1)
                                            else it
                                        }
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Leave", fontSize = 12.sp, color = Color(0xFFD32F2F))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { onOpenGroupChat(group.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Open Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        groupsList = groupsList.map {
                                            if (it.id == group.id) it.copy(isJoined = true, membersCount = it.membersCount + 1)
                                            else it
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text("+ Join Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New mig33 Group", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newGroupDesc,
                        onValueChange = { newGroupDesc = it },
                        label = { Text("Group Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGroupName.isNotBlank()) {
                            groupsList = listOf(
                                MigGroupItem(
                                    id = "g_${System.currentTimeMillis()}",
                                    name = newGroupName,
                                    category = "Community",
                                    membersCount = 1,
                                    description = if (newGroupDesc.isBlank()) "Welcome to $newGroupName!" else newGroupDesc,
                                    icon = "👥",
                                    isJoined = true
                                )
                            ) + groupsList
                            showCreateDialog = false
                            newGroupName = ""
                            newGroupDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
