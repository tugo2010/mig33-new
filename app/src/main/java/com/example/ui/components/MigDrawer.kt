package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.hasAdminBadge

@Composable
fun MigDrawerContent(
    userProfile: UserProfile,
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit,
    onSearch: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(Color.White)
            .testTag("mig_drawer_content")
    ) {
        // 1. Top Search Header Bar (Authentic mig33 style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search for a user or post",
                        color = Color(0xFF888888),
                        fontSize = 12.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF222222),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_search_input")
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            // Orange Search Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                        )
                    )
                    .clickable { onSearch(searchQuery) }
                    .testTag("drawer_search_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Yellow/Gold Menu Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFCA28), Color(0xFFFFA000))
                        )
                    )
                    .clickable { onNavigateTo("chat_rooms") }
                    .testTag("drawer_menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ViewList,
                    contentDescription = "Menu List",
                    tint = Color(0xFF2E1C00),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. User Profile Strip Card (Authentic mig33 User Header)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9))
                .clickable { onNavigateTo("profile") }
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .testTag("drawer_user_profile_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Thumbnail with 2D AvatarView
                AvatarView(
                    config = userProfile.avatarConfig,
                    modifier = Modifier.size(54.dp, 66.dp),
                    cornerRadius = 6.dp,
                    borderColor = Color(0xFFFFB74D),
                    borderWidth = 1.5.dp,
                    alignment = Alignment.TopCenter
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Online Indicator + Username + Badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Green Online Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (userProfile.isInvisible) Color.Gray else Color(0xFF00E676))
                                .border(0.5.dp, Color(0xFF1B5E20), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))

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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Level Badge Icon
                        MigLevelBadgeView(level = userProfile.migLevel, size = 16.dp, isOwned = true)

                        // Special Badge if equipped
                        userProfile.equippedBadgeNumbers.firstOrNull { it != "000" && !it.startsWith("lvl_") }?.let { key ->
                            Spacer(modifier = Modifier.width(3.dp))
                            val b = com.example.model.MigLevelCalculator.ALL_MIG33_BADGES.find { it.badgeNumber == key }
                            MigSpecialBadgeView(
                                badgeNumber = key,
                                icon = b?.icon ?: "",
                                size = 16.dp,
                                isOwned = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // migLevel row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MigLevelBadgeView(
                            level = userProfile.migLevel,
                            size = 14.dp,
                            isOwned = true
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "migLevel ${userProfile.migLevel}",
                            color = Color(0xFF37474F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Gender, Birthday, Country
                    val locationText = listOfNotNull(
                        userProfile.gender.ifBlank { "Male" },
                        userProfile.birthday.ifBlank { "1 Jan 1990" },
                        userProfile.country.ifBlank { "migWorld" }
                    ).joinToString(", ")

                    Text(
                        text = locationText,
                        color = Color(0xFF78909C),
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Credits & Points Ribbon (Classic Dark Charcoal Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E262B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙 ", fontSize = 11.sp)
                Text(text = "Credits: ", color = Color(0xFFFFD54F), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "${userProfile.credits}", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐ ", fontSize = 11.sp)
                Text(text = "Points: ", color = Color(0xFF81D4FA), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "${userProfile.points}", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 3. Section Header: "Discover" (Authentic Solid Black Bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Discover",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        // 4. Clean Retro mig33 Menu Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            item {
                DrawerMenuItem(
                    icon = Icons.Default.AccountCircle,
                    title = "My Profile",
                    onClick = { onNavigateTo("profile") },
                    testTag = "menu_my_profile"
                )
            }
            if (userProfile.hasAdminBadge()) {
                item {
                    DrawerMenuItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Admin Panel",
                        onClick = { onNavigateTo("admin_dashboard") },
                        badgeText = "ADMIN",
                        testTag = "menu_admin_dashboard"
                    )
                }
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.EmojiEvents,
                    title = "Badges Showcase",
                    onClick = { onNavigateTo("badges") },
                    testTag = "menu_badges"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.People,
                    title = "Friends List",
                    onClick = { onNavigateTo("friends") },
                    testTag = "menu_friends"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Edit Profile",
                    onClick = { onNavigateTo("edit_profile") },
                    testTag = "menu_edit_profile"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Face,
                    title = "2D Avatar Boutique",
                    onClick = { onNavigateTo("avatar_studio") },
                    badgeText = "VIP 2D",
                    testTag = "menu_avatar_studio"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Palette,
                    title = "2D Wardrobe Gallery",
                    onClick = { onNavigateTo("avatar_builder") },
                    badgeText = "NEW",
                    testTag = "menu_avatar_builder"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    title = "Chat Rooms",
                    onClick = { onNavigateTo("chat_rooms") },
                    badgeText = "8.5k",
                    testTag = "menu_chat_rooms"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Storefront,
                    title = "mig33 Store (المتجر)",
                    onClick = { onNavigateTo("gifts_shop") },
                    badgeText = "HOT",
                    testTag = "menu_gifts_shop"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Send,
                    title = "Transfer Credits (تحويل الكريدت)",
                    onClick = { onNavigateTo("credit_transfer") },
                    badgeText = "BANK",
                    testTag = "menu_credit_transfer"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.RssFeed,
                    title = "Mini-blog / Feeds",
                    onClick = { onNavigateTo("feeds") },
                    testTag = "menu_feeds"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Forum,
                    title = "Active Chats",
                    onClick = { onNavigateTo("home_chats") },
                    testTag = "menu_active_chats"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.PersonAdd,
                    title = "Invite a friend",
                    onClick = { onNavigateTo("invite") },
                    testTag = "menu_invite"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.ThumbUp,
                    title = "Recommendations",
                    onClick = { onNavigateTo("recommendations") },
                    testTag = "menu_recommendations"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Groups,
                    title = "Groups",
                    onClick = { onNavigateTo("groups") },
                    testTag = "menu_groups"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Public,
                    title = "migWorld",
                    onClick = { onNavigateTo("mig_world") },
                    testTag = "menu_mig_world"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.AlternateEmail,
                    title = "Mentions",
                    onClick = { onNavigateTo("mentions") },
                    testTag = "menu_mentions"
                )
            }
            item {
                DrawerMenuItem(
                    icon = Icons.Default.Star,
                    title = "Watchlist",
                    onClick = { onNavigateTo("watchlist") },
                    testTag = "menu_watchlist"
                )
            }
            item {
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Log Out",
                    onClick = onLogout,
                    textColor = Color(0xFFD32F2F),
                    testTag = "menu_logout"
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    badgeText: String? = null,
    textColor: Color = Color(0xFF263238),
    testTag: String = ""
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF455A64),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (badgeText != null) {
                    val badgeBg = when (badgeText) {
                        "HOT" -> Color(0xFFE64A19)
                        "ADMIN" -> Color(0xFF0288D1)
                        "CLASSIC" -> Color(0xFF0288D1)
                        "2D ENGINE" -> Color(0xFF0097A7)
                        else -> Color(0xFF0288D1)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.8.dp)
        }
    }
}
