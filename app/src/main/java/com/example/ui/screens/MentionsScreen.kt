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
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Chat
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

data class MentionItem(
    val id: String,
    val author: String,
    val text: String,
    val location: String,
    val timeAgo: String,
    val avatarRes: Int
)

@Composable
fun MentionsScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenDirectChat: (String) -> Unit,
    onOpenProfile: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()
    val mentionsList = remember {
        listOf(
            MentionItem("m1", "f1yingkit3", "@${userProfile.username} Hey check out the trivia game in Danger 1 room right now!", "Danger 1 Chat Room", "10 mins ago", R.drawable.avatar_f1yingkit),
            MentionItem("m2", "b4sejump", "@${userProfile.username} That denim jacket avatar look is awesome!", "Mini-blog Feed", "1 hour ago", R.drawable.avatar_b4sejump),
            MentionItem("m3", "t00fi3", "Replying to @${userProfile.username}: Haha yeah let's meet up this weekend at the coffee spot!", "Feed Comments", "3 hours ago", R.drawable.avatar_f1yingkit),
            MentionItem("m4", "migCute", "Welcome @${userProfile.username} to the mig33 community! Share your avatar creations.", "Official mig33 Notice", "1 day ago", R.drawable.mig33_app_icon)
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
                    centerTitle = "Mentions & @Tags",
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "@MENTIONS OF @${userProfile.username.uppercase()} (${mentionsList.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
            }

            items(mentionsList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = item.avatarRes),
                                contentDescription = item.author,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFF81D4FA), CircleShape)
                                    .clickable { onOpenProfile(item.author) },
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenProfile(item.author) }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.author, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0288D1))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• ${item.timeAgo}", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                                }
                                Text(item.location, fontSize = 11.sp, color = Color(0xFF757575))
                            }
                            IconButton(
                                onClick = { onOpenProfile(item.author) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AlternateEmail, contentDescription = "View Profile", tint = Color(0xFFFF8C00))
                            }
                            IconButton(
                                onClick = { onOpenDirectChat(item.author) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = "Reply", tint = Color(0xFF0288D1))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.text, fontSize = 13.sp, color = Color(0xFF263238), lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}
