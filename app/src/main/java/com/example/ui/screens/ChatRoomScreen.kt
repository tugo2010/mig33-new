package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.ChatRoom
import com.example.model.RoomMessage
import com.example.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatRoomScreen(
    roomId: String,
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBackHome: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenDirectChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms by repository.chatRooms.collectAsState()
    val allMessages by repository.roomMessages.collectAsState()
    val userProfile by repository.userProfile.collectAsState()

    val currentRoom = rooms.find { it.id == roomId } ?: rooms.firstOrNull() ?: ChatRoom("danger_1", "Danger 1", "What's Hot", 48, 50)
    val roomMessagesList = allMessages[currentRoom.id] ?: emptyList()

    var messageInput by remember { mutableStateOf("") }
    var showContextMenu by remember { mutableStateOf(false) }
    var showGamesDialog by remember { mutableStateOf(false) }
    var showEmoticonPicker by remember { mutableStateOf(false) }
    var showGiftDialog by remember { mutableStateOf(false) }
    var showParticipantsDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showKickDialog by remember { mutableStateOf(false) }
    var alertBannerText by remember { mutableStateOf(currentRoom.gameRoundMessage) }

    val activeTriviaRounds by repository.activeTriviaRounds.collectAsState()
    val activeTrivia = activeTriviaRounds[currentRoom.id]
    val isTriviaActive = activeTrivia != null && !activeTrivia.isAnswered && System.currentTimeMillis() < activeTrivia.endMillis

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(roomMessagesList.size) {
        if (roomMessagesList.isNotEmpty()) {
            listState.animateScrollToItem(roomMessagesList.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Home",
                    leftAvatarRes = null,
                    centerTitle = currentRoom.name,
                    rightAvatarRes = R.drawable.avatar_f1yingkit,
                    onLeftClick = onBackHome,
                    onRightClick = { onOpenDirectChat("f1yingkit3") }
                )
                // Games Quick Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(Color(0xFF263238))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showGamesDialog = true }
                    ) {
                        Text(text = "🎮", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Room Games (ألعاب الروم)",
                            color = Color(0xFF81D4FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[!dice • !trivia • !flip]",
                            color = Color(0xFFFFD54F),
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0288D1),
                        modifier = Modifier.clickable { showGamesDialog = true }
                    ) {
                        Text(
                            text = "🎲 Play",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            MigRoomBottomBar(
                messageText = messageInput,
                onMessageChange = { messageInput = it },
                onSendMessage = {
                    if (messageInput.isNotBlank()) {
                        repository.sendRoomMessage(currentRoom.id, messageInput)
                        messageInput = ""
                    }
                },
                onQuickYellowClick = { showContextMenu = !showContextMenu },
                onHomeClick = onBackHome,
                onProfileClick = { onOpenProfile(userProfile.username) },
                onSettingsClick = { showInfoDialog = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .testTag("chat_room_screen")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Interactive Live Trivia Card if active in this room
                if (isTriviaActive && activeTrivia != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🧠 ", fontSize = 14.sp)
                                    Text(
                                        text = "LIVE TRIVIA ROUND (مسابقة لايف)",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF00B4D8)
                                ) {
                                    Text(
                                        text = "🏆 ${activeTrivia.question.prizeCredits} cr",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = activeTrivia.question.question,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )

                            // Quick Clickable 4 Options
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                activeTrivia.question.options.forEachIndexed { optIndex, optText ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                repository.submitTriviaAnswer(currentRoom.id, optIndex)
                                            },
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E293B),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                    ) {
                                        Text(
                                            text = "👉 [${optIndex + 1}] $optText",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (alertBannerText.isNotEmpty()) {
                    // Yellow Alert/Game Banner matching Screenshot 5
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFDE7))
                            .border(1.dp, Color(0xFFFFE082))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("room_announcement_banner")
                    ) {
                        Column {
                            Text(
                                text = alertBannerText,
                                color = Color(0xFF00ACC1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(roomMessagesList) { msg ->
                        RoomMessageRow(
                            message = msg,
                            currentUser = userProfile,
                            onUserClick = { onOpenProfile(msg.senderUsername) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Context Action Menu overlay (matching Screenshot 5)
            if (showContextMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x44000000))
                        .clickable { showContextMenu = false }
                        .padding(bottom = 54.dp, start = 6.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    MigRoomContextMenu(
                        onDismiss = { showContextMenu = false },
                        onPlayGames = { showGamesDialog = true },
                        onInsertEmoticon = { showEmoticonPicker = true },
                        onSendGift = { showGiftDialog = true },
                        onKick = { showKickDialog = true },
                        onParticipants = { showParticipantsDialog = true },
                        onRoomInfo = { showInfoDialog = true },
                        onAddToFavorites = {
                            repository.toggleFavoriteRoom(currentRoom.id)
                            alertBannerText = if (currentRoom.isFavorite) "Room removed from your favorites." else "Room added to your favorites!"
                        },
                        onGroups = { onOpenDrawer() },
                        onReportAbuse = {
                            alertBannerText = "Report submitted to mig33 Safety Team."
                        }
                    )
                }
            }
        }
    }

    // Room Games Hub Dialog
    if (showGamesDialog) {
        MigRoomGamesDialog(
            roomId = currentRoom.id,
            userProfile = userProfile,
            repository = repository,
            onDismiss = { showGamesDialog = false }
        )
    }

    // Emoticon Picker Dialog
    if (showEmoticonPicker) {
        MigEmoticonPickerDialog(
            emoticons = repository.emoticonsList,
            onSelect = { emo ->
                messageInput += " " + emo.code
            },
            onDismiss = { showEmoticonPicker = false }
        )
    }

    // Send Gift Dialog
    if (showGiftDialog) {
        MigSendGiftDialog(
            gifts = repository.availableGifts,
            userCredits = userProfile.credits,
            recipientName = "All in ${currentRoom.name}",
            onSendGift = { gift ->
                repository.sendVirtualGift(currentRoom.id, gift, "Room")
            },
            onDismiss = { showGiftDialog = false }
        )
    }

    // Kick User Dialog
    if (showKickDialog) {
        AlertDialog(
            onDismissRequest = { showKickDialog = false },
            title = { Text(text = "⚡ Moderator Kick", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select a user to kick from ${currentRoom.name}:", fontSize = 13.sp)
                    listOf("kalidawirx", "t00fi3", "dhivehin_queens").forEach { target ->
                        Button(
                            onClick = {
                                repository.kickUserFromRoom(currentRoom.id, target)
                                showKickDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Kick @$target", fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showKickDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Participants Dialog with interactive Direct Chat links
    if (showParticipantsDialog) {
        AlertDialog(
            onDismissRequest = { showParticipantsDialog = false },
            title = { Text(text = "👥 Participants (${currentRoom.usersCount})", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "f1yingkit3" to 18,
                        "b4sejump" to 21,
                        "t00fi3" to 15,
                        "kalidawirx" to 12,
                        "migCute" to 8
                    ).forEach { (name, lvl) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    showParticipantsDialog = false
                                    onOpenProfile(name)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).clickable {
                                    showParticipantsDialog = false
                                    onOpenProfile(name)
                                }
                            ) {
                                Text(text = "🟢 ", fontSize = 10.sp)
                                MigLevelBadgeView(
                                    level = lvl,
                                    size = 18.dp,
                                    isOwned = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            }
                            Row {
                                TextButton(onClick = {
                                    showParticipantsDialog = false
                                    onOpenProfile(name)
                                }) {
                                    Text("Profile", fontSize = 12.sp, color = Color(0xFFFF8C00), fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = {
                                    showParticipantsDialog = false
                                    onOpenDirectChat(name)
                                }) {
                                    Text("Chat", fontSize = 12.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showParticipantsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Room Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(text = currentRoom.name, color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Topic: ${currentRoom.topic}", fontSize = 13.sp)
                    Text(text = "Category: ${currentRoom.category}", fontSize = 13.sp)
                    Text(text = "Capacity: ${currentRoom.usersCount} / ${currentRoom.maxUsers} users", fontSize = 13.sp)
                    Text(text = "Game Mode: ${if (currentRoom.isGameActive) "Active (Trivia Round)" else "Normal"}", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun RoomMessageRow(
    message: RoomMessage,
    currentUser: UserProfile? = null,
    onUserClick: () -> Unit
) {
    val badgeColors by com.example.model.AdminSettings.badgeNameColors.collectAsState()

    if (message.isGameAlert) {
        // System Game Winner Alert
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE0F7FA))
                .border(1.dp, Color(0xFF80DEEA), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "⚡ [SYSTEM GAME] ${message.text}",
                color = Color(0xFF00838F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else if (message.isGiftAlert) {
        // Virtual Gift Alert Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFFF3E0))
                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = message.giftIcon ?: "🎁", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "🎁 GIFT SHOWER!",
                        color = Color(0xFFE65100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${message.senderUsername} ${message.text}",
                        color = Color(0xFF5D4037),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        // Standard User Chat Message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            val isBot = message.senderUsername in listOf("migBot", "Trivia Master", "Dice Master", "Lucky7 Bot", "CoinFlip Bot")
            val isMe = currentUser != null && message.senderUsername.equals(currentUser.username, ignoreCase = true)
            if (isBot) {
                Box(
                    modifier = Modifier
                        .size(36.dp, 46.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF0288D1), Color(0xFF01579B))
                            )
                        )
                        .border(1.dp, Color(0xFF81D4FA), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (message.senderUsername) {
                            "Trivia Master" -> "🧠"
                            "Dice Master" -> "🎲"
                            "Lucky7 Bot" -> "🎰"
                            "CoinFlip Bot" -> "🪙"
                            else -> "🤖"
                        },
                        fontSize = 20.sp
                    )
                }
            } else if (isMe) {
                com.example.ui.components.AvatarView(
                    config = currentUser.avatarConfig,
                    modifier = Modifier
                        .size(36.dp, 46.dp)
                        .clickable { onUserClick() },
                    cornerRadius = 4.dp,
                    borderColor = Color(0xFFFFB74D),
                    borderWidth = 1.dp,
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp, 46.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(4.dp))
                        .background(Color(0xFF000000))
                        .clickable { onUserClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = message.avatarRes),
                        contentDescription = message.senderUsername,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isBot) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = Color(0xFF0288D1)
                            ) {
                                Text(
                                    text = "BOT",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = message.senderUsername,
                                color = Color(0xFF0288D1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else {
                        val senderLevel = when (message.senderUsername.lowercase()) {
                            "hipst4r" -> 24
                            "f1yingkit3" -> 18
                            "b4sejump" -> 21
                            "kalidawirx" -> 12
                            "t00fi3" -> 15
                            "migcute" -> 8
                            else -> 10
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MigLevelBadgeView(
                                level = senderLevel,
                                size = 18.dp,
                                isOwned = true
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            if (message.adminBadgeId != null) {
                                val b = com.example.model.MigLevelCalculator.ALL_MIG33_BADGES.find { it.badgeNumber == message.adminBadgeId }
                                com.example.ui.components.MigSpecialBadgeView(
                                    badgeNumber = message.adminBadgeId,
                                    icon = b?.icon ?: "",
                                    size = 18.dp,
                                    isOwned = true
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            
                            val nameColor = if (message.adminBadgeId != null && badgeColors[message.adminBadgeId] != null) {
                                Color(badgeColors[message.adminBadgeId]!!)
                            } else if (message.nameColorHex != null) {
                                Color(message.nameColorHex)
                            } else {
                                Color(0xFF0288D1)
                            }
                            Text(
                                text = message.senderUsername,
                                color = nameColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onUserClick() }
                            )
                        }
                    }
                    Text(text = message.timestamp, color = Color(0xFF9E9E9E), fontSize = 11.sp)
                }
                Text(
                    text = message.text,
                    color = if (isBot) Color(0xFF0D47A1) else Color(0xFF263238),
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = if (isBot) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
