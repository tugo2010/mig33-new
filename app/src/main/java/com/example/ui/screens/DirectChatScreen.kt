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
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.DirectMessage
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DirectChatScreen(
    recipientUsername: String,
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBackHome: () -> Unit,
    onOpenProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allDirectMessages by repository.directMessages.collectAsState()
    val userProfile by repository.userProfile.collectAsState()
    val messages = allDirectMessages[recipientUsername] ?: emptyList()

    var messageInput by remember { mutableStateOf("") }
    var showRetroKeyboard by remember { mutableStateOf(true) }
    var showEmoticonPicker by remember { mutableStateOf(false) }
    var showGiftDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Home",
                    leftAvatarRes = null,
                    centerTitle = recipientUsername,
                    rightAvatarRes = if (recipientUsername == "f1yingkit3") R.drawable.avatar_f1yingkit else R.drawable.avatar_b4sejump,
                    onLeftClick = onBackHome,
                    onRightClick = { onOpenProfile(recipientUsername) }
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Input Bar matching Screenshot 6
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF222222)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8ECEF))
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Yellow mig button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF222222))
                                .border(1.dp, Color(0xFF444444), RoundedCornerShape(6.dp))
                                .clickable { showGiftDialog = true }
                                .testTag("direct_chat_gift_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color(0xFFFFD54F), CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Text input field
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.Black,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("direct_chat_input")
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Send Button (Orange Gradient)
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                                    )
                                )
                                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(4.dp))
                                .clickable {
                                    if (messageInput.isNotBlank()) {
                                        repository.sendDirectMessage(recipientUsername, messageInput)
                                        messageInput = ""
                                    }
                                }
                                .padding(horizontal = 12.dp)
                                .testTag("direct_chat_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Send",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Retro mig33 Keyboard matching Screenshot 6
                if (showRetroKeyboard) {
                    MigRetroKeyboard(
                        onKeyPress = { key -> messageInput += key },
                        onBackspace = {
                            if (messageInput.isNotEmpty()) {
                                messageInput = messageInput.dropLast(1)
                            }
                        },
                        onEmoticonClick = { showEmoticonPicker = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(8.dp)
                .testTag("direct_messages_list")
        ) {
            items(messages) { msg ->
                if (msg.isFromMe) {
                    // Right-aligned Green Bubble
                    SelfMessageBubbleRow(
                        message = msg,
                        selfUsername = userProfile.username,
                        avatarConfig = userProfile.avatarConfig
                    )
                } else {
                    // Left-aligned Yellow Bubble
                    OtherUserMessageBubbleRow(
                        message = msg,
                        senderName = recipientUsername
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    if (showEmoticonPicker) {
        MigEmoticonPickerDialog(
            emoticons = repository.emoticonsList,
            onSelect = { emo -> messageInput += emo.code },
            onDismiss = { showEmoticonPicker = false }
        )
    }

    if (showGiftDialog) {
        MigSendGiftDialog(
            gifts = repository.availableGifts,
            userCredits = userProfile.credits,
            recipientName = recipientUsername,
            onSendGift = { gift ->
                repository.sendVirtualGift("direct", gift, recipientUsername)
                repository.sendDirectMessage(recipientUsername, "Sent you a gift: ${gift.name} ${gift.emoji}!")
            },
            onDismiss = { showGiftDialog = false }
        )
    }
}

@Composable
fun OtherUserMessageBubbleRow(
    message: DirectMessage,
    senderName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 50.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(4.dp))
                .background(Color(0xFF000000)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = message.avatarRes),
                contentDescription = senderName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Classic Cream/Light-Yellow Bubble with orange border
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFFFDE7))
                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val otherLevel = when (message.senderUsername.lowercase()) {
                        "f1yingkit3" -> 18
                        "b4sejump" -> 21
                        "t00fi3" -> 15
                        "kalidawirx" -> 12
                        "migcute" -> 8
                        else -> 14
                    }
                    MigLevelBadgeView(
                        level = otherLevel,
                        size = 18.dp,
                        isOwned = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${message.senderUsername} (${message.timestamp}):",
                        color = Color(0xFF263238),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    color = Color(0xFF212121),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SelfMessageBubbleRow(
    message: DirectMessage,
    selfUsername: String,
    avatarConfig: com.example.model.AvatarConfig? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        // Classic Light-Green Bubble with green border
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE8F8E8))
                .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MigLevelBadgeView(
                        level = 24,
                        size = 18.dp,
                        isOwned = true
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${message.senderUsername} (${message.timestamp}):",
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    color = Color(0xFF212121),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (avatarConfig != null) {
            AvatarView(
                config = avatarConfig,
                modifier = Modifier.size(40.dp, 50.dp),
                cornerRadius = 4.dp,
                borderColor = Color(0xFF81D4FA),
                borderWidth = 1.dp,
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp, 50.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFF81D4FA), RoundedCornerShape(4.dp))
                    .background(Color(0xFF000000)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_b4sejump),
                    contentDescription = selfUsername,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
