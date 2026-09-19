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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSubHeaderBar

@Composable
fun RecommendationsScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenDirectChat: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val friendsList by repository.friends.collectAsState()
    var addedUsers by remember { mutableStateOf(setOf<String>()) }

    val recommendedUsers = listOf(
        Triple("migCute", "Official Mascot & Community Star", R.drawable.mig33_app_icon),
        Triple("f1yingkit3", "Trivia Showdown Champion (VIP)", R.drawable.avatar_f1yingkit),
        Triple("b4sejump", "Level 24 Fashion Icon", R.drawable.avatar_b4sejump),
        Triple("kalidawirx", "Game-warteg Community Lead", R.drawable.avatar_b4sejump),
        Triple("dhivehi_fan", "Maldives Lounge Mod", R.drawable.avatar_f1yingkit),
        Triple("retro_gamer99", "Play Games & Dice Enthusiast", R.drawable.avatar_hipst4r)
    )

    val recommendedRooms = listOf(
        Triple("danger_1", "Danger 1", "48 / 50 online • Trivia Showdown"),
        Triple("game_warteg", "Game-warteg", "24 / 60 online • Indonesian Community"),
        Triple("fashion_show", "Fashion Show", "34 / 50 online • Avatar Showcases"),
        Triple("trivia_world", "Trivia Lounge", "19 / 40 online • Fast Quiz")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                MigSubHeaderBar(
                    leftText = "Back",
                    leftAvatarRes = null,
                    centerTitle = "Recommendations",
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
            // Header: Recommended People
            item {
                Text(
                    text = "🌟 RECOMMENDED PEOPLE TO FOLLOW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
            }

            items(recommendedUsers) { user ->
                val isFriend = friendsList.contains(user.first) || addedUsers.contains(user.first)
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
                        Image(
                            painter = painterResource(id = user.third),
                            contentDescription = user.first,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFF81D4FA), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.first,
                                color = Color(0xFF0288D1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = user.second,
                                color = Color(0xFF757575),
                                fontSize = 11.sp
                            )
                        }
                        if (isFriend) {
                            Button(
                                onClick = { onOpenDirectChat(user.first) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    repository.addFriend(user.first)
                                    addedUsers = addedUsers + user.first
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Header: Suggested Chat Rooms
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🔥 HOT CHAT ROOMS FOR YOU",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }

            items(recommendedRooms) { room ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onJoinRoom(room.first) },
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = room.second,
                                color = Color(0xFF263238),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = room.third,
                                color = Color(0xFF546E7A),
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = { onJoinRoom(room.first) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Join", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
