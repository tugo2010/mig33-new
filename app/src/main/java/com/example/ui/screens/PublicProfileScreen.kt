package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.FeedPost
import com.example.ui.components.MigLevelBadgeView
import com.example.ui.components.MigSendGiftDialog
import com.example.ui.components.MigSpecialBadgeView
import java.util.Random

// Pre-defined detailed profile specs for recognizable mig33 community members
data class PublicUserData(
    val username: String,
    val displayName: String,
    val level: Int,
    val points: Long,
    val credits: Int,
    val gender: String,
    val age: Int,
    val country: String,
    val relationshipStatus: String,
    val statusMessage: String,
    val bio: String,
    val avatarRes: Int,
    val profileViews: Int,
    val joinDate: String,
    val specialBadgeNumber: String? = null,
    val receivedGifts: List<ReceivedGiftData> = emptyList()
)

data class ReceivedGiftData(
    val giftName: String,
    val emoji: String,
    val fromUser: String,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    username: String,
    repository: MigRepository,
    onBack: () -> Unit,
    onOpenDirectChat: (String) -> Unit,
    onTransferCredits: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val friendsList by repository.friends.collectAsState()
    val allPosts by repository.feedPosts.collectAsState()
    val currentUserProfile by repository.userProfile.collectAsState()

    val isFriend = friendsList.any { it.equals(username, ignoreCase = true) }

    // Resolve or procedurally generate profile details for this username
    val userData = remember(username) {
        getPublicUserData(username)
    }

    // Dynamic or filtered user posts
    val userPosts = remember(allPosts, username) {
        val matches = allPosts.filter { it.authorUsername.equals(username, ignoreCase = true) }
        if (matches.isNotEmpty()) {
            matches
        } else {
            // Provide realistic mig33 mini-blog posts for this user
            listOf(
                FeedPost(
                    id = "post_${username}_1",
                    authorUsername = username,
                    authorDisplayName = userData.displayName,
                    authorAvatarRes = userData.avatarRes,
                    isVerified = userData.level >= 25,
                    tag = "#migLife",
                    content = "Enjoying the good old mig33 vibes! Come chat with me in Danger 1 or drop a message ^_^",
                    imageRes = if (username.hashCode() % 2 == 0) R.drawable.cat_glasses_meme else null,
                    timeAgo = "1 hour ago via mig33",
                    repliesCount = 3,
                    shareCount = 1,
                    starCount = 14,
                    isStarred = false
                ),
                FeedPost(
                    id = "post_${username}_2",
                    authorUsername = username,
                    authorDisplayName = userData.displayName,
                    authorAvatarRes = userData.avatarRes,
                    isVerified = userData.level >= 25,
                    tag = null,
                    content = userData.statusMessage,
                    imageRes = null,
                    timeAgo = "Yesterday",
                    repliesCount = 6,
                    shareCount = 2,
                    starCount = 29,
                    isStarred = true
                )
            )
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Posts, 1: Gifts, 2: About
    var showGiftDialog by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            toastMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userData.displayName,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🟢",
                            fontSize = 9.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        toastMessage = "Username @$username copied to clipboard"
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF8C00)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFEEEEEE))
                .testTag("public_profile_screen")
        ) {
            // 1. Header Card with Avatar, Level Badge and Bio
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Avatar Box with border & equipped badge overlay
                            Box(modifier = Modifier.size(90.dp)) {
                                if (username.equals(currentUserProfile.username, ignoreCase = true)) {
                                    com.example.ui.components.AvatarView(
                                        config = currentUserProfile.avatarConfig,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = userData.avatarRes),
                                        contentDescription = "Avatar of $username",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(2.dp, Color(0xFF0288D1), RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.TopCenter
                                    )
                                }

                                if (userData.specialBadgeNumber != null) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .offset(x = 6.dp, y = 6.dp)
                                    ) {
                                        MigSpecialBadgeView(
                                            badgeNumber = userData.specialBadgeNumber,
                                            icon = if (userData.specialBadgeNumber == "001") "🛡️" else "👑",
                                            size = 28.dp,
                                            isOwned = true
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Details Column
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userData.displayName,
                                        color = Color(0xFF0288D1),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (userData.level >= 25) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "⭐", fontSize = 13.sp)
                                    }
                                }

                                Text(
                                    text = "@${userData.username}",
                                    color = Color(0xFF757575),
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Level Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    MigLevelBadgeView(
                                        level = userData.level,
                                        size = 20.dp,
                                        isOwned = true
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Level ${userData.level}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${userData.gender}, ${userData.age}y",
                                        fontSize = 12.sp,
                                        color = Color(0xFF616161)
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "📍 ${userData.country}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF616161)
                                )

                                Text(
                                    text = "❤️ ${userData.relationshipStatus}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE91E63)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Quote Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F8E9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC5E1A5))
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💬", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = userData.statusMessage,
                                    fontSize = 13.sp,
                                    color = Color(0xFF33691E),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Primary Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Message Button
                            Button(
                                onClick = { onOpenDirectChat(username) },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Message", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Add/Remove Friend Button
                            Button(
                                onClick = {
                                    if (isFriend) {
                                        repository.removeFriend(username)
                                        toastMessage = "Removed @$username from friends"
                                    } else {
                                        repository.addFriend(username)
                                        toastMessage = "Added @$username as a friend!"
                                    }
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFriend) Color(0xFF455A64) else Color(0xFF2E7D32),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFriend) Icons.Default.Check else Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isFriend) "Friends ✓" else "+ Add Friend", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Send Gift Button
                            Button(
                                onClick = { showGiftDialog = true },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Text("🎁 Gift", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Send Credits Button
                            if (onTransferCredits != null && !username.equals(currentUserProfile.username, ignoreCase = true)) {
                                Button(
                                    onClick = { onTransferCredits(username) },
                                    modifier = Modifier.weight(1.1f).height(38.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Text("🪙 Send cr", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Poke Button
                            OutlinedButton(
                                onClick = {
                                    toastMessage = "You poked @$username! 👉 (Nudge sent)"
                                },
                                modifier = Modifier.height(38.dp),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("👉 Poke", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Stats Strip
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF263238))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪙 Credits", fontSize = 11.sp, color = Color(0xFFB0BEC5))
                        Text("${userData.credits}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⭐ Points", fontSize = 11.sp, color = Color(0xFFB0BEC5))
                        Text("${userData.points}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81D4FA))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎁 Gifts", fontSize = 11.sp, color = Color(0xFFB0BEC5))
                        Text("${userData.receivedGifts.size + 8}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A80))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👀 Views", fontSize = 11.sp, color = Color(0xFFB0BEC5))
                        Text("${userData.profileViews}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 3. Badges Showcase Row
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏅 Badges & Honors",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0288D1)
                            )
                            Text(
                                text = "${minOf(userData.level / 5 + 3, 12)} Earned",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                BadgeChip("👑", "migVIP Gold", Color(0xFFFFD54F))
                            }
                            item {
                                BadgeChip("🎮", "Trivia Pro", Color(0xFF81D4FA))
                            }
                            item {
                                BadgeChip("🔥", "Top Chatter", Color(0xFFFFAB91))
                            }
                            item {
                                BadgeChip("🎁", "Gift Collector", Color(0xFFCE93D8))
                            }
                            if (userData.level >= 20) {
                                item {
                                    BadgeChip("🛡️", "Guardian", Color(0xFFA5D6A7))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Content Tab Selector (Posts, Gifts, About)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF0288D1)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Mini-Blog (${userPosts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Gifts (${userData.receivedGifts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("About Info", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // 5. Tab Contents
            when (selectedTab) {
                0 -> {
                    // Posts Tab
                    if (userPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No mini-blog posts yet from @$username.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(userPosts) { post ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = post.authorAvatarRes),
                                            contentDescription = post.authorUsername,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, Color(0xFF0288D1), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = post.authorDisplayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF0288D1)
                                            )
                                            Text(
                                                text = post.timeAgo,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = post.content,
                                        fontSize = 13.sp,
                                        color = Color(0xFF212121)
                                    )

                                    if (post.imageRes != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Image(
                                            painter = painterResource(id = post.imageRes),
                                            contentDescription = "Post image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⭐ ${post.starCount} stars", fontSize = 11.sp, color = Color.Gray)
                                        Text("💬 ${post.repliesCount} replies", fontSize = 11.sp, color = Color.Gray)
                                        Text("🔄 ${post.shareCount} shares", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Gifts Received Tab
                    items(userData.receivedGifts) { gift ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(gift.emoji, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(gift.giftName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                                    Text("From: @${gift.fromUser}", fontSize = 12.sp, color = Color(0xFF0288D1))
                                    Text("\"${gift.message}\"", fontSize = 11.sp, color = Color(0xFF616161))
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // About Tab
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                InfoRow("Username", "@${userData.username}")
                                InfoRow("Display Name", userData.displayName)
                                InfoRow("Account Level", "${userData.level} (${userData.points} XP)")
                                InfoRow("Bio", userData.bio)
                                InfoRow("Country / Region", userData.country)
                                InfoRow("Gender", userData.gender)
                                InfoRow("Member Since", userData.joinDate)
                                InfoRow("Relationship", userData.relationshipStatus)
                                InfoRow("Profile Views", "${userData.profileViews} views")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Gift Dialog
    if (showGiftDialog) {
        MigSendGiftDialog(
            gifts = repository.availableGifts,
            userCredits = currentUserProfile.credits,
            recipientName = username,
            onSendGift = { gift ->
                repository.sendVirtualGift("public_profile", gift, username)
                toastMessage = "Sent a ${gift.name} ${gift.emoji} to @$username!"
                showGiftDialog = false
            },
            onDismiss = { showGiftDialog = false }
        )
    }
}

@Composable
private fun BadgeChip(icon: String, title: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF263238))
    }
}

// Procedural and predefined profiles for any user in the mig33 ecosystem
private fun getPublicUserData(username: String): PublicUserData {
    return when (username.lowercase()) {
        "f1yingkit3" -> PublicUserData(
            username = "f1yingkit3",
            displayName = "Flying Kite 🪁",
            level = 18,
            points = 64200,
            credits = 4200,
            gender = "Female",
            age = 22,
            country = "Singapore 🇸🇬",
            relationshipStatus = "Single",
            statusMessage = "Catch me if you can! Trivia addict (h)",
            bio = "Love mig33 chat rooms, retro avatars, and playing quiz tournaments every evening!",
            avatarRes = R.drawable.avatar_f1yingkit,
            profileViews = 8412,
            joinDate = "Oct 2010",
            specialBadgeNumber = "002",
            receivedGifts = listOf(
                ReceivedGiftData("Red Rose", "🌹", "b4sejump", "Good luck in the tournament!"),
                ReceivedGiftData("Teddy Bear", "🧸", "migCute", "Welcome to mig33!"),
                ReceivedGiftData("mig33 Crown", "👑", "t00fi3", "Queen of trivia!")
            )
        )
        "b4sejump" -> PublicUserData(
            username = "b4sejump",
            displayName = "Axton Hale",
            level = 21,
            points = 92500,
            credits = 8900,
            gender = "Male",
            age = 25,
            country = "Australia 🇦🇺",
            relationshipStatus = "It's complicated",
            statusMessage = "Skater & gamer. Catch me in Danger 1 room!",
            bio = "Extreme sports fan, adrenaline junkie, and long-time mig33 moderator.",
            avatarRes = R.drawable.avatar_b4sejump,
            profileViews = 12450,
            joinDate = "June 2009",
            specialBadgeNumber = "001",
            receivedGifts = listOf(
                ReceivedGiftData("Sports Car", "🏎️", "f1yingkit3", "Vroom vroom!"),
                ReceivedGiftData("Golden Trophy", "🏆", "kalidawirx", "Best moderator award!")
            )
        )
        "t00fi3" -> PublicUserData(
            username = "t00fi3",
            displayName = "Taufiq Indo",
            level = 15,
            points = 45000,
            credits = 1500,
            gender = "Male",
            age = 20,
            country = "Indonesia 🇮🇩",
            relationshipStatus = "In a relationship",
            statusMessage = "Ngopi santai lur ☕",
            bio = "Mahasiswa, suka ngobrol dan nongkrong di chat room mig33.",
            avatarRes = R.drawable.avatar_hipst4r,
            profileViews = 4320,
            joinDate = "Jan 2012",
            receivedGifts = listOf(
                ReceivedGiftData("Red Rose", "🌹", "dhivehin_queens", "Salam kenal ya!"),
                ReceivedGiftData("Love Heart", "💖", "migCute", "Stay active on mig33!")
            )
        )
        "kalidawirx" -> PublicUserData(
            username = "kalidawirx",
            displayName = "Kalidawir Master",
            level = 27,
            points = 158000,
            credits = 12500,
            gender = "Male",
            age = 28,
            country = "Indonesia 🇮🇩",
            relationshipStatus = "Married",
            statusMessage = "Komunitas Kalidawir Bersatu! Salam santun (y)",
            bio = "Founder of kalidawirx room. Mari jalin silaturahmi yang erat.",
            avatarRes = R.drawable.avatar_hipst4r,
            profileViews = 24190,
            joinDate = "March 2008",
            specialBadgeNumber = "001",
            receivedGifts = listOf(
                ReceivedGiftData("mig33 Crown", "👑", "seru_boy", "Leader sejati!"),
                ReceivedGiftData("Golden Trophy", "🏆", "b4sejump", "Respect from Australia!")
            )
        )
        "migcute" -> PublicUserData(
            username = "migCute",
            displayName = "migCute Official 🐱",
            level = 50,
            points = 999999,
            credits = 50000,
            gender = "Other",
            age = 3,
            country = "Global 🌐",
            relationshipStatus = "Loving everyone!",
            statusMessage = "Where are my spectacles?! MEOW~ (m)",
            bio = "Official mascot of mig33 community! Here to spread happiness and virtual gifts.",
            avatarRes = R.drawable.mig33_app_icon,
            profileViews = 99999,
            joinDate = "Day 1",
            specialBadgeNumber = "000",
            receivedGifts = listOf(
                ReceivedGiftData("Birthday Cake", "🎂", "All Community", "Happy mig33 Day!"),
                ReceivedGiftData("mig33 Crown", "👑", "mig33_Staff", "Supreme Mascot")
            )
        )
        else -> {
            val seed = username.hashCode().toLong()
            val rnd = Random(seed)
            val lvl = rnd.nextInt(45) + 5
            val pts = com.example.model.MigLevelCalculator.xpRequiredForLevel(lvl) + rnd.nextInt(1500)
            val cred = rnd.nextInt(9500) + 200
            val age = rnd.nextInt(18) + 18
            val countries = listOf("Singapore 🇸🇬", "Indonesia 🇮🇩", "Malaysia 🇲🇾", "Nepal 🇳🇵", "Maldives 🇲🇻", "Philippines 🇵🇭", "India 🇮🇳")
            val statuses = listOf(
                "Chatting on mig33 is pure nostalgia! :-D",
                "Looking for fun trivia opponents!",
                "Coffee, music, and good friends (c)",
                "Living my best virtual life in mig33 🌟",
                "Hit me up in the chat rooms anytime! (y)"
            )
            val gifts = listOf(
                ReceivedGiftData("Red Rose", "🌹", "Friend_${rnd.nextInt(99)}", "Welcome to my favorites!"),
                ReceivedGiftData("Love Heart", "💖", "migCute", "Keep smiling!")
            )

            PublicUserData(
                username = username,
                displayName = username.replaceFirstChar { it.uppercase() },
                level = lvl,
                points = pts,
                credits = cred,
                gender = if (rnd.nextBoolean()) "Male" else "Female",
                age = age,
                country = countries[rnd.nextInt(countries.size)],
                relationshipStatus = if (rnd.nextBoolean()) "Single" else "In a relationship",
                statusMessage = statuses[rnd.nextInt(statuses.size)],
                bio = "Active mig33 member! Enjoys custom avatars, mini-blogs, and making friends across the globe.",
                avatarRes = if (rnd.nextBoolean()) R.drawable.avatar_hipst4r else R.drawable.avatar_b4sejump,
                profileViews = rnd.nextInt(12000) + 300,
                joinDate = "201${rnd.nextInt(4)}",
                receivedGifts = gifts
            )
        }
    }
}
