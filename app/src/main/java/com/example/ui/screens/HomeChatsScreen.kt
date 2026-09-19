package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar
import com.example.ui.theme.*

@Composable
fun HomeChatsScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onOpenChatRoom: (String) -> Unit,
    onOpenDirectChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenStore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val friendsList by repository.friends.collectAsState()
    var showSearchProfileDialog by remember { mutableStateOf(false) }
    var searchUsernameInput by remember { mutableStateOf("") }
    var refreshToast by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Friends (${friendsList.size})",
                    leftAvatarRes = null,
                    centerTitle = "Home",
                    rightAvatarRes = R.drawable.avatar_f1yingkit,
                    onLeftClick = onOpenFriends,
                    onRightClick = { onOpenDirectChat("f1yingkit3") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .testTag("home_chats_screen")
        ) {
            // Stats Bar matching Screenshot 7 with real click refresh
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .border(0.5.dp, Color(0xFFBDBDBD))
                    .clickable {
                        repository.refreshRooms()
                        refreshToast = true
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📊", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (refreshToast) "✅ Live network synced! 89.4k online." else "89.2k users. 12.8k rooms. 383.4k groups.",
                    color = if (refreshToast) Color(0xFF00796B) else Color(0xFF424242),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick Action Row: mig33 Store & Profile Search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD))
                    .border(0.5.dp, Color(0xFF90CAF9))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // mig33 Store Shortcut
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0D47A1))
                        .clickable { onOpenStore() }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏬", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "mig33 Store (المتجر)",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Quick Search Profile
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF0288D1), RoundedCornerShape(6.dp))
                        .clickable { showSearchProfileDialog = true }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "بحث عن مستخدم",
                        color = Color(0xFF0288D1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFB0BEC5), thickness = 0.5.dp)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Item 1: f1yingkit3 (Private Chat)
                item {
                    ChatItemRow(
                        stripColor = Color(0xFFFFD54F),
                        avatarRes = R.drawable.avatar_f1yingkit,
                        showOnlineDot = true,
                        title = "f1yingkit3",
                        subtitle = "Hi there! Want to play some games together?",
                        timestamp = "11:32",
                        onClick = { onOpenDirectChat("f1yingkit3") },
                        onAvatarClick = { onOpenProfile("f1yingkit3") },
                        testTag = "chat_item_f1yingkit3"
                    )
                }

                // Item 2: Fashion Show Room
                item {
                    RoomItemRow(
                        stripColor = Color(0xFFFF7043),
                        iconEmoji = "💃",
                        iconBg = Color(0xFFD32F2F),
                        title = "Fashion Show",
                        subtitle = "Show your best mig33 avatars & win!",
                        timestamp = "",
                        onClick = { onOpenChatRoom("fashion_show") },
                        testTag = "chat_item_fashion_show"
                    )
                }

                // Item 3: Game Lobby Room
                item {
                    RoomItemRow(
                        stripColor = Color(0xFFFFD54F),
                        iconEmoji = "💬",
                        iconBg = Color(0xFF455A64),
                        title = "Game Lobby",
                        subtitle = "[PVT] Play now: !start to enter. ...",
                        timestamp = "11:30",
                        onClick = { onOpenChatRoom("game_lobby") },
                        testTag = "chat_item_game_lobby"
                    )
                }

                // Item 4: t00fi3 feed activity
                item {
                    ChatItemRow(
                        stripColor = Color(0xFF81D4FA),
                        avatarRes = R.drawable.avatar_hipst4r,
                        showOnlineDot = false,
                        title = "t00fi3",
                        subtitle = "@f1yingkit3 Haha! Yup!",
                        timestamp = "",
                        onClick = { onOpenDirectChat("t00fi3") },
                        onAvatarClick = { onOpenProfile("t00fi3") },
                        testTag = "chat_item_t00fi3"
                    )
                }

                // Item 5: f1yingkit3 status
                item {
                    ChatItemRow(
                        stripColor = Color(0xFF81D4FA),
                        avatarRes = R.drawable.avatar_f1yingkit,
                        showOnlineDot = false,
                        title = "f1yingkit3",
                        subtitle = "“Flight of the Valkyries” is always used as ...",
                        timestamp = "",
                        onClick = { onOpenDirectChat("f1yingkit3") },
                        onAvatarClick = { onOpenProfile("f1yingkit3") },
                        testTag = "chat_item_f1yingkit3_status"
                    )
                }

                // Item 6: b4sejump reply with badges
                item {
                    ChatItemRow(
                        stripColor = Color(0xFF81D4FA),
                        avatarRes = R.drawable.avatar_b4sejump,
                        showOnlineDot = false,
                        title = "b4sejump",
                        realName = "Axton Hale",
                        badges = listOf("A", "M"),
                        subtitle = "Reply: LOL! Silly kitty doesn’t know that it ...",
                        timestamp = "",
                        onClick = { onOpenDirectChat("b4sejump") },
                        onAvatarClick = { onOpenProfile("b4sejump") },
                        testTag = "chat_item_b4sejump"
                    )
                }

                // Item 7: Danger 1 Room
                item {
                    RoomItemRow(
                        stripColor = Color(0xFFFF5722),
                        iconEmoji = "⚡",
                        iconBg = Color(0xFFE65100),
                        title = "Danger 1 (Official Trivia)",
                        subtitle = "CONGRATS! f1yingkit3 wins this round...",
                        timestamp = "11:33",
                        onClick = { onOpenChatRoom("danger_1") },
                        testTag = "chat_item_danger_1"
                    )
                }
            }
        }
    }

    // Search Any User's Profile Dialog
    if (showSearchProfileDialog) {
        AlertDialog(
            onDismissRequest = { showSearchProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔍 ", fontSize = 18.sp)
                    Text(
                        text = "عرض بروفايل أي شخص / View Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1),
                        fontSize = 15.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اكتب اسم المستخدم لفتح بروفايله مباشرة أو اختر من المقترحين:",
                        fontSize = 12.sp,
                        color = Color(0xFF546E7A)
                    )
                    OutlinedTextField(
                        value = searchUsernameInput,
                        onValueChange = { searchUsernameInput = it },
                        label = { Text("Username / اسم المستخدم") },
                        placeholder = { Text("e.g. hipst4r, b4sejump, f1yingkit3...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "أعضاء مقترحون / Popular Users:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78909C)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("f1yingkit3", "b4sejump", "t00fi3", "kalidawirx").forEach { name ->
                            Button(
                                onClick = {
                                    showSearchProfileDialog = false
                                    onOpenProfile(name)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(name, fontSize = 11.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = searchUsernameInput.trim()
                        if (name.isNotEmpty()) {
                            showSearchProfileDialog = false
                            onOpenProfile(name)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Open Profile / فتح البروفايل", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSearchProfileDialog = false }) {
                    Text("Cancel / إلغاء")
                }
            }
        )
    }
}

@Composable
fun ChatItemRow(
    stripColor: Color,
    avatarRes: Int,
    showOnlineDot: Boolean,
    title: String,
    realName: String? = null,
    badges: List<String> = emptyList(),
    subtitle: String,
    timestamp: String,
    onClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null,
    testTag: String = ""
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() }
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left color indicator strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(stripColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Avatar with optional green online indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onAvatarClick?.invoke() ?: onClick() },
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(4.dp))
                        .background(Color(0xFF000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                if (showOnlineDot) {
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF76FF03))
                            .border(1.dp, Color(0xFF33691E), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Info Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = Color(0xFF0288D1),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (realName != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = realName,
                                color = Color(0xFF757575),
                                fontSize = 13.sp
                            )
                        }
                        badges.forEach { b ->
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(if (b == "A") Color(0xFFFFB300) else Color(0xFF7E57C2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = b, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    if (timestamp.isNotEmpty()) {
                        Text(text = timestamp, color = Color(0xFF757575), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF37474F),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}

@Composable
fun RoomItemRow(
    stripColor: Color,
    iconEmoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    timestamp: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() }
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(stripColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Room Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(iconBg)
                    .border(1.dp, Color(0xFFB0BEC5), RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color(0xFF0288D1),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (timestamp.isNotEmpty()) {
                        Text(text = timestamp, color = Color(0xFF757575), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF37474F),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}
