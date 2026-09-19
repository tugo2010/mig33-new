package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar

@Composable
fun InviteScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var friendInput by remember { mutableStateOf("") }
    var inviteMessage by remember { mutableStateOf("Join me on mig33! My username is hipst4r. Download now and chat with 80M+ users!") }
    var showSuccessBanner by remember { mutableStateOf<String?>(null) }

    val invitedList = remember {
        mutableStateListOf(
            Triple("alex_rocker@gmail.com", "Joined (+50 Credits)", true),
            Triple("+65 9123 4567", "Joined (+50 Credits)", true),
            Triple("sarah.cute99@yahoo.com", "Invite Pending", false)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Back",
                    leftAvatarRes = null,
                    centerTitle = "Invite Friends",
                    rightAvatarRes = null,
                    onLeftClick = onBack
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFECEFF1))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reward Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFFFB74D))))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎁 ", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = "Earn 50 migCredits Per Friend!",
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Invite friends via SMS or Email. You get +50 Credits & +25 Points immediately when they join.",
                                    color = Color(0xFF5D4037),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (showSuccessBanner != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = showSuccessBanner ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Referral Link / Code Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "YOUR PERSONAL INVITE CODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF757575)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "http://mig33.com/invite/hipst4r",
                                color = Color(0xFF0288D1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("mig33 Invite Link", "http://mig33.com/invite/hipst4r")
                                    clipboard.setPrimaryClip(clip)
                                    showSuccessBanner = "Invite link copied to clipboard!"
                                },
                                modifier = Modifier.size(32.dp).testTag("copy_invite_link_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }

            // Send Custom Invite Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SEND INVITATION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = friendInput,
                            onValueChange = { friendInput = it },
                            label = { Text("Friend's Mobile No. or Email") },
                            placeholder = { Text("e.g. +6281234567 or friend@email.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("invite_friend_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (friendInput.isNotBlank()) {
                                    invitedList.add(0, Triple(friendInput, "Invite Sent (+50 Credits bonus)", true))
                                    repository.addCredits(50)
                                    showSuccessBanner = "Invitation sent to $friendInput! +50 Credits added to your balance."
                                    friendInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth().testTag("send_invite_btn")
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send mig33 Invite & Claim +50 Credits", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // History of Invited Friends
            item {
                Text(
                    text = "INVITED CONTACTS (${invitedList.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF546E7A)
                )
            }

            items(invitedList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = item.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF263238))
                            Text(
                                text = item.second,
                                fontSize = 11.sp,
                                color = if (item.third) Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (item.third) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (item.third) "✓ Active" else "⏳ Pending",
                                fontSize = 11.sp,
                                color = if (item.third) Color(0xFF2E7D32) else Color(0xFFE65100),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
