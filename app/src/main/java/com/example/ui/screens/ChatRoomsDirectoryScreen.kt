package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.model.ChatRoom
import com.example.ui.components.MigHeaderBar
import com.example.ui.theme.*

@Composable
fun ChatRoomsDirectoryScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms by repository.chatRooms.collectAsState()
    var expandedCategory by remember { mutableStateOf("What's Hot") }
    var searchFilter by remember { mutableStateOf("") }
    var showSearchInput by remember { mutableStateOf(false) }

    val userProfile by repository.userProfile.collectAsState()
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    val maxRoomCapacity = (10 + userProfile.migLevel).coerceIn(10, 60)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                // Cyan subheader bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF00ACC1), Color(0xFF00838F))
                            )
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👥", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chat Rooms",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateRoomDialog = true },
                containerColor = Color(0xFFFF8C00),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Room")
            }
        },
        bottomBar = {
            // Classic J2ME Bottom Menu / Refresh Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("rooms_bottom_bar"),
                color = Color(0xFF1E1E1E)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Menu",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onOpenDrawer() }
                            .testTag("rooms_menu_btn")
                    )
                    Text(
                        text = "Refresh",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { repository.refreshRooms() }
                            .testTag("rooms_refresh_btn")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE0F7FA))
                .testTag("chat_rooms_directory")
        ) {
            // Orange Header Ribbon matching screenshot 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                        )
                    )
                    .border(0.5.dp, Color(0xFFFFB74D))
                    .clickable { repository.refreshRooms() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💡", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                val totalOnline = rooms.sumOf { it.usersCount }
                Text(
                    text = "${totalOnline + 89000} users. ${rooms.size} rooms active",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showSearchInput) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchFilter,
                        onValueChange = { searchFilter = it },
                        placeholder = { Text("Filter rooms by name...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        searchFilter = ""
                        showSearchInput = false
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Category 1: Your Favorites
                item {
                    CategoryHeaderRow(
                        title = "Your Favorites (${rooms.count { it.isFavorite }})",
                        isExpanded = expandedCategory == "Your Favorites",
                        onClick = {
                            expandedCategory = if (expandedCategory == "Your Favorites") "" else "Your Favorites"
                        }
                    )
                }
                if (expandedCategory == "Your Favorites") {
                    val favRooms = rooms.filter { it.isFavorite }
                    items(favRooms) { room ->
                        RoomTreeItem(room = room, onJoin = { onJoinRoom(room.id) })
                    }
                }

                // Category 2: Recent Rooms
                item {
                    CategoryHeaderRow(
                        title = "Recent Rooms",
                        isExpanded = expandedCategory == "Recent Rooms",
                        onClick = {
                            expandedCategory = if (expandedCategory == "Recent Rooms") "" else "Recent Rooms"
                        }
                    )
                }
                if (expandedCategory == "Recent Rooms") {
                    val recRooms = rooms.take(3)
                    items(recRooms) { room ->
                        RoomTreeItem(room = room, onJoin = { onJoinRoom(room.id) })
                    }
                }

                // Category 3: Play Games
                item {
                    CategoryHeaderRow(
                        title = "Play Games (${rooms.count { it.category == "Play Games" }})",
                        isExpanded = expandedCategory == "Play Games",
                        onClick = {
                            expandedCategory = if (expandedCategory == "Play Games") "" else "Play Games"
                        }
                    )
                }
                if (expandedCategory == "Play Games") {
                    val gameRooms = rooms.filter { it.category == "Play Games" }
                    items(gameRooms) { room ->
                        RoomTreeItem(room = room, onJoin = { onJoinRoom(room.id) })
                    }
                }

                // Category 4: What's Hot (Default Expanded matching Screenshot 1)
                item {
                    CategoryHeaderRow(
                        title = "What's Hot",
                        isExpanded = expandedCategory == "What's Hot",
                        onClick = {
                            expandedCategory = if (expandedCategory == "What's Hot") "" else "What's Hot"
                        }
                    )
                }
                if (expandedCategory == "What's Hot") {
                    val hotRooms = rooms.filter {
                        if (searchFilter.isBlank()) it.category == "What's Hot" || it.isHot
                        else it.name.contains(searchFilter, ignoreCase = true)
                    }
                    items(hotRooms) { room ->
                        RoomTreeItem(room = room, onJoin = { onJoinRoom(room.id) })
                    }

                    // Special actions in What's Hot
                    item {
                        ActionTreeItem(
                            iconSymbol = "🔄",
                            title = "Refresh What's Hot",
                            onClick = { repository.refreshRooms() }
                        )
                    }
                    item {
                        ActionTreeItem(
                            iconSymbol = "🔍",
                            title = if (showSearchInput) "Hide Search" else "Search Rooms",
                            onClick = { showSearchInput = !showSearchInput }
                        )
                    }
                }
            }
        }

    if (showCreateRoomDialog) {
        AlertDialog(
            onDismissRequest = { showCreateRoomDialog = false },
            title = { Text("Create Chat Room") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newRoomName,
                        onValueChange = { newRoomName = it },
                        label = { Text("Room Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your Account Level: ${userProfile.migLevel}", fontSize = 12.sp, color = Color.Gray)
                    Text("Room Max Capacity: $maxRoomCapacity Users", fontSize = 12.sp, color = Color.Gray)
                    Text("(Calculated based on your level)", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRoomName.isNotBlank()) {
                            repository.createRoom(newRoomName, "What's Hot", maxRoomCapacity, userProfile.username)
                            showCreateRoomDialog = false
                            newRoomName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateRoomDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    }
}

@Composable
fun CategoryHeaderRow(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("category_$title"),
        color = Color(0xFFE0F7FA)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "- " else "+ ",
                    color = Color(0xFF00838F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = title,
                    color = Color(0xFF00838F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = Color(0xFFB2EBF2), thickness = 1.dp)
        }
    }
}

@Composable
fun RoomTreeItem(
    room: ChatRoom,
    onJoin: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin() }
            .testTag("room_tree_${room.id}"),
        color = Color(0xFFE0F7FA)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "💬", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = room.name,
                        color = Color(0xFF004D40),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "(${room.usersCount}/${room.maxUsers})",
                    color = Color(0xFF00695C),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            HorizontalDivider(color = Color(0xFFE0F2F1), thickness = 0.5.dp)
        }
    }
}

@Composable
fun ActionTreeItem(
    iconSymbol: String,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color(0xFFE0F7FA)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = iconSymbol, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color(0xFF00838F),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(color = Color(0xFFB2EBF2), thickness = 0.5.dp)
        }
    }
}
