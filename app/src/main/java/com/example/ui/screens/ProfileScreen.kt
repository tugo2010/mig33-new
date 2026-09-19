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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.UserProfile
import com.example.model.hasAdminBadge
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onOpenAvatarStudio: () -> Unit,
    onOpenGiftsShop: () -> Unit,
    onOpenDirectChat: (String) -> Unit = {},
    onOpenBadges: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenAdminDashboard: () -> Unit = {},
    onOpen2DAvatarBuilder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()
    val allPosts by repository.feedPosts.collectAsState()
    val friendsList by repository.friends.collectAsState()
    val followersList by repository.followers.collectAsState()
    val followingList by repository.following.collectAsState()
    val userPosts = allPosts.filter { it.authorUsername == userProfile.username }
    val userBadges = remember(userProfile.migLevel, userProfile.points) {
        com.example.model.MigLevelCalculator.getBadgesForUser(userProfile.migLevel, userProfile.points.toLong())
    }
    val ownedBadgesCount = remember(userBadges) { userBadges.count { it.isOwned } }
    
    var showGiftDialog by remember { mutableStateOf(false) }
    var showFriendsDialog by remember { mutableStateOf(false) }
    var showFollowersDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }
    var showWatchlistDialog by remember { mutableStateOf(false) }
    
    // Dedicated Modals for Badges, Edit Profile, and Level System
    var showBadgesDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLevelSystemDialog by remember { mutableStateOf(false) }
    var showMoreMenuDropdown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                // Profile Header Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color(0xFF2B2B2B))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👤", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = userProfile.username,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "mig33 Profile",
                        color = Color(0xFFB0BEC5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
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
                .testTag("profile_screen")
        ) {
            // 1. Profile Hero Section matching mig33 Mobile Layout
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left: Full Body Anime Avatar Card with pet & scenery
                    Box(
                        modifier = Modifier
                            .width(135.dp)
                            .height(200.dp)
                            .clickable { onOpenAvatarStudio() }
                    ) {
                        MigAvatarCard(
                            avatarConfig = userProfile.avatarConfig,
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Show equipped special badge (admin/special) on profile picture
                        val equippedSpecialBadge = userProfile.equippedBadgeNumbers.firstOrNull()
                        if (equippedSpecialBadge != null) {
                            val b = com.example.model.MigLevelCalculator.ALL_MIG33_BADGES.find { it.badgeNumber == equippedSpecialBadge }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 8.dp, y = 8.dp)
                            ) {
                                com.example.ui.components.MigSpecialBadgeView(
                                    badgeNumber = equippedSpecialBadge, 
                                    icon = b?.icon ?: "", 
                                    size = 32.dp, 
                                    isOwned = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right: User Details & Actions
                    Column(modifier = Modifier.weight(1f)) {
                        val adminBadgeId = userProfile.equippedBadgeNumbers.firstOrNull { it != "000" && !it.startsWith("lvl_") }
                        val badgeColors by com.example.model.AdminSettings.badgeNameColors.collectAsState()
                        val nameColor = if (adminBadgeId != null && badgeColors[adminBadgeId] != null) {
                            Color(badgeColors[adminBadgeId]!!)
                        } else {
                            Color(0xFF0288D1)
                        }

                        Text(
                            text = userProfile.username,
                            color = nameColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Green Online Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (userProfile.isOnline) Color(0xFF76FF03) else Color.Gray)
                                    .border(1.dp, if (userProfile.isOnline) Color(0xFF33691E) else Color.DarkGray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userProfile.isInvisible) "Invisible" else if (userProfile.isOnline) "Online" else "Offline",
                                color = Color(0xFF424242),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // migLevel badge (Clickable to open Level System dialog!)
                        val progression = remember(userProfile.points) {
                            com.example.model.MigLevelCalculator.calculateProgression(userProfile.points.toLong())
                        }
                        MigLevelChip(
                            level = progression.currentLevel,
                            showRankTitle = false,
                            useClassicStyle = true,
                            onClick = { showLevelSystemDialog = true },
                            modifier = Modifier.testTag("profile_level_badge")
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "${userProfile.gender}, ${userProfile.birthday}, ${userProfile.country}",
                            color = Color(0xFF757575),
                            fontSize = 12.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status Speech Bubble - Clickable to edit status!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF555555))
                                .clickable { onOpenEditProfile() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "💬 ${userProfile.statusMessage}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons: "Avatar Studio", "2D Builder (New)" & "Send Gift"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                                        )
                                    )
                                    .clickable { onOpenAvatarStudio() }
                                    .testTag("customize_avatar_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Studio", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF8E24AA), Color(0xFF5E35B1))
                                        )
                                    )
                                    .clickable { onOpen2DAvatarBuilder() }
                                    .testTag("open_2d_avatar_builder_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎨 2D Builder", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                                        )
                                    )
                                    .clickable { showGiftDialog = true }
                                    .testTag("profile_gift_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Send Gift", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. 8-tile Stats Grid matching Screenshot 8 - ALL FULLY INTERACTIVE
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProfileStatTile(
                            number = "${friendsList.size}",
                            label = "Friends",
                            onClick = onOpenFriends,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            number = "${userPosts.size}",
                            label = "Posts",
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            number = "${followersList.size}",
                            label = "Followers",
                            onClick = { showFollowersDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            number = "${followingList.size}",
                            label = "Following",
                            onClick = { showFollowingDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProfileStatTile(
                            number = "$ownedBadgesCount",
                            label = "Badges",
                            onClick = onOpenBadges,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            number = "${userProfile.giftsCount}",
                            label = "Gifts",
                            onClick = onOpenGiftsShop,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            number = "${repository.watchList.size}",
                            label = "Watch List",
                            onClick = { showWatchlistDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileStatTile(
                                number = "⋯",
                                label = "More",
                                onClick = { showMoreMenuDropdown = true },
                                modifier = Modifier.fillMaxWidth().testTag("profile_stat_more_btn")
                            )
                            DropdownMenu(
                                expanded = showMoreMenuDropdown,
                                onDismissRequest = { showMoreMenuDropdown = false }
                            ) {
                                if (userProfile.hasAdminBadge()) {
                                    DropdownMenuItem(
                                        text = { Text("👑 Admin Panel (لوحة تحكم الأدمن)", fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C)) },
                                        leadingIcon = { Text("👑", fontSize = 16.sp) },
                                        onClick = {
                                            showMoreMenuDropdown = false
                                            onOpenAdminDashboard()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("⚙️ Edit Full Profile", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Text("⚙️", fontSize = 16.sp) },
                                    onClick = {
                                        showMoreMenuDropdown = false
                                        onOpenEditProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🏆 mig33 Badges (Owned & All)", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Text("🏆", fontSize = 16.sp) },
                                    onClick = {
                                        showMoreMenuDropdown = false
                                        onOpenBadges()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🤖 migLevel & XP System", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Text("🤖", fontSize = 16.sp) },
                                    onClick = {
                                        showMoreMenuDropdown = false
                                        showLevelSystemDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("👗 Avatar Studio", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Text("👗", fontSize = 16.sp) },
                                    onClick = {
                                        showMoreMenuDropdown = false
                                        onOpenAvatarStudio()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🎁 Virtual Gifts Shop", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Text("🎁", fontSize = 16.sp) },
                                    onClick = {
                                        showMoreMenuDropdown = false
                                        onOpenGiftsShop()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2.5 Personal Details & Bio Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👤 Profile Details & Bio (المعلومات والنبذة)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0288D1)
                            )
                            Text(
                                text = "⚙️ Edit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1),
                                modifier = Modifier.clickable { onOpenEditProfile() }
                            )
                        }

                        if (userProfile.bio.isNotBlank()) {
                            Text(
                                text = userProfile.bio,
                                fontSize = 12.sp,
                                color = Color(0xFF37474F),
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📍 Location: ${userProfile.city}, ${userProfile.country}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                                Text(
                                    text = "🎂 Birthday: ${userProfile.birthday}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                                Text(
                                    text = "🚻 Gender: ${userProfile.gender}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "💍 Status: ${userProfile.relationshipStatus}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                                Text(
                                    text = "🎨 Hobbies: ${userProfile.hobbies}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Section Header: "hipst4r's Recent Posts"
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF263238))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${userProfile.username}'s Recent Posts (${userPosts.size})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. User Posts List
            items(userPosts) { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_hipst4r),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.authorDisplayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "@${post.authorUsername}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                            Text(
                                text = post.timeAgo,
                                fontSize = 11.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = post.content,
                        fontSize = 13.sp,
                        color = Color(0xFF333333),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Post actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💬 ${post.repliesCount} replies",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "⭐ ${post.starCount} stars",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "🔄 ${post.shareCount} shares",
                            fontSize = 11.sp,
                            color = Color(0xFF757575)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                }
            }
        }
    }

    // Interactive Dialogs
    // 1. Friends List Dialog
    if (showFriendsDialog) {
        AlertDialog(
            onDismissRequest = { showFriendsDialog = false },
            title = { Text("👥 Friends List (${friendsList.size})", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(friendsList) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    showFriendsDialog = false
                                    onOpenDirectChat(friend)
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF81D4FA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👤", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(friend, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }
                            Text("Chat 💬", color = Color(0xFF0288D1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFriendsDialog = false }) { Text("Close") } }
        )
    }

    // 2. Followers List Dialog
    if (showFollowersDialog) {
        AlertDialog(
            onDismissRequest = { showFollowersDialog = false },
            title = { Text("🌟 Followers (${followersList.size})", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(followersList) { follower ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    showFollowersDialog = false
                                    onOpenDirectChat(follower)
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(follower, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Message 💬", color = Color(0xFF0288D1), fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFollowersDialog = false }) { Text("Close") } }
        )
    }

    // 3. Following List Dialog
    if (showFollowingDialog) {
        AlertDialog(
            onDismissRequest = { showFollowingDialog = false },
            title = { Text("➡️ Following (${followingList.size})", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(followingList) { followingUser ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    showFollowingDialog = false
                                    onOpenDirectChat(followingUser)
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(followingUser, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Message 💬", color = Color(0xFF0288D1), fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFollowingDialog = false }) { Text("Close") } }
        )
    }

    // 4. Watch List Dialog
    if (showWatchlistDialog) {
        AlertDialog(
            onDismissRequest = { showWatchlistDialog = false },
            title = { Text("👀 mig33 Watch List", color = Color(0xFF0288D1), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    repository.watchList.forEach { item ->
                        Text("📌 $item", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showWatchlistDialog = false }) { Text("Close") } }
        )
    }

    // 5. Badges Showcase Dialog
    if (showBadgesDialog) {
        MigBadgesShowcaseDialog(
            repository = repository,
            onDismiss = { showBadgesDialog = false }
        )
    }

    // 6. Edit Full Profile Dialog
    if (showEditProfileDialog) {
        MigFullEditProfileDialog(
            userProfile = userProfile,
            repository = repository,
            onDismiss = { showEditProfileDialog = false }
        )
    }

    // 7. mig33 Level System Dialog
    if (showLevelSystemDialog) {
        MigLevelSystemDialog(
            userProfile = userProfile,
            repository = repository,
            onDismiss = { showLevelSystemDialog = false }
        )
    }

    // 8. Virtual Gift Dialog
    if (showGiftDialog) {
        MigSendGiftDialog(
            gifts = repository.availableGifts,
            userCredits = userProfile.credits,
            recipientName = userProfile.username,
            onSendGift = { gift ->
                repository.sendVirtualGift("profile", gift, userProfile.username)
            },
            onDismiss = { showGiftDialog = false }
        )
    }
}

@Composable
fun ProfileStatTile(
    number: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF555555), Color(0xFF383838), Color(0xFF2B2B2B))
                )
            )
            .border(1.dp, Color(0xFF616161), RoundedCornerShape(4.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = Color(0xFFCFD8DC),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
