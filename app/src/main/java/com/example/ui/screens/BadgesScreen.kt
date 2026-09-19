package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.model.MigBadge
import com.example.model.MigLevelCalculator
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigLevelBadgeView
import com.example.ui.components.MigSpecialBadgeView

@Composable
fun BadgesScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()
    val allBadges = remember(userProfile.migLevel, userProfile.points) {
        MigLevelCalculator.getBadgesForUser(userProfile.migLevel, userProfile.points.toLong())
    }
    val ownedCount = remember(allBadges) { allBadges.count { it.isOwned } }
    val categories = remember(ownedCount) {
        listOf(
            "Owned ($ownedCount)",
            "All (${allBadges.size})",
            "Admin & Devs (الأدمن والمطورين)",
            "Verification (التفعيل والتوثيق)",
            "Recharge & VIP (شحن ورصيد)",
            "Rooms (الرومات والمشرفين)",
            "Gifts (الهدايا والرومانسية)",
            "Special Events (مناسبات خاصة)",
            "Games (الألعاب)",
            "Levels (المستويات)"
        )
    }

    var showOnlyOwned by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Owned ($ownedCount)") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBadgeForDetail by remember { mutableStateOf<MigBadge?>(null) }

    val filteredBadges = remember(selectedCategory, searchQuery, showOnlyOwned, allBadges) {
        val catFiltered = when {
            selectedCategory.startsWith("Owned") -> allBadges.filter { it.isOwned }
            selectedCategory.startsWith("All") -> if (showOnlyOwned) allBadges.filter { it.isOwned } else allBadges
            selectedCategory.startsWith("Admin") -> allBadges.filter { it.category == "Admin & Staff" && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Verification") -> allBadges.filter { it.category == "Verification" && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Recharge") -> allBadges.filter { (it.category == "Merchants & Recharging" || it.category == "VIP & Crowns" || it.category == "Merchant & VIP") && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Rooms") -> allBadges.filter { it.category == "Rooms & Hosts" && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Gifts") -> allBadges.filter { it.category == "Gifts & Romance" && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Special") -> allBadges.filter { (it.category == "Special & Events" || it.category == "History & Community") && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Games") -> allBadges.filter { (it.category == "Games & Trivia" || it.category == "Games & Contests") && (!showOnlyOwned || it.isOwned) }
            selectedCategory.startsWith("Levels") -> allBadges.filter { it.category == "Levels" && (!showOnlyOwned || it.isOwned) }
            else -> allBadges.filter { it.category == selectedCategory && (!showOnlyOwned || it.isOwned) }
        }
        if (searchQuery.isBlank()) {
            catFiltered
        } else {
            val q = searchQuery.trim().lowercase()
            catFiltered.filter { b ->
                b.name.lowercase().contains(q) ||
                (b.badgeNumber != null && b.badgeNumber.contains(q)) ||
                b.description.lowercase().contains(q) ||
                b.category.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                // Title Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B)))
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🏆 mig33 Badges Showcase (الشارات)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Owned: $ownedCount / ${allBadges.size}",
                        color = Color(0xFFB3E5FC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFECEFF1))
                .testTag("badges_screen")
        ) {
            // Categories Tab Row
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 8.dp,
                containerColor = Color.White,
                contentColor = Color(0xFF0288D1)
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search badges by name or # (بحث عن شارة)...", fontSize = 12.sp, color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0288D1),
                    unfocusedBorderColor = Color(0xFFB0BEC5)
                ),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
            )

            // Hide Unowned Badges Toggle Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showOnlyOwned) "إخفاء الشارات غير المملوكة ✓ ($ownedCount)" else "عرض جميع الشارات (${allBadges.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("المملوكة فقط", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = showOnlyOwned,
                        onCheckedChange = { showOnlyOwned = it },
                        modifier = Modifier.scale(0.75f)
                    )
                }
            }

            // Badges List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredBadges) { badge ->
                    val badgeKey = badge.badgeNumber ?: "lvl_${badge.levelNumber}"
                    val isEquipped = userProfile.equippedBadgeNumbers.contains(badgeKey)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBadgeForDetail = badge },
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) Color(0xFFE8F5E9) else if (badge.isOwned) Color.White else Color(0xFFF5F5F5)
                        ),
                        border = if (isEquipped) {
                            BorderStroke(1.5.dp, Color(0xFF2E7D32))
                        } else if (badge.isOwned) {
                            CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                                )
                            )
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge Icon
                            if (badge.levelNumber != null) {
                                MigLevelBadgeView(
                                    level = badge.levelNumber,
                                    size = 46.dp,
                                    isOwned = badge.isOwned
                                )
                            } else {
                                MigSpecialBadgeView(
                                    badgeNumber = badge.badgeNumber,
                                    icon = badge.icon,
                                    size = 46.dp,
                                    isOwned = badge.isOwned
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    if (badge.badgeNumber != null) {
                                        Text(
                                            text = "#${badge.badgeNumber}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0288D1)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = badge.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (badge.isOwned) Color(0xFF263238) else Color(0xFF757575),
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                when (badge.rarity) {
                                                    "Legendary" -> Color(0xFFE91E63)
                                                    "Epic" -> Color(0xFF7B1FA2)
                                                    "Rare" -> Color(0xFF0288D1)
                                                    else -> Color(0xFF78909C)
                                                }
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(badge.rarity, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = badge.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF616161),
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "🎯 " + badge.unlockCriteria,
                                    fontSize = 10.sp,
                                    color = Color(0xFF0288D1),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Status / Action Column
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (badge.isOwned) {
                                    if (isEquipped) {
                                        Button(
                                            onClick = { repository.toggleEquipBadge(badgeKey) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("✓ مُفعلة", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { repository.toggleEquipBadge(badgeKey) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0288D1)),
                                            border = BorderStroke(1.dp, Color(0xFF0288D1)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("استخدام", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFEEEEEE))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text("🔒 Locked", color = Color(0xFF9E9E9E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Badge Details Dialog
    if (selectedBadgeForDetail != null) {
        val b = selectedBadgeForDetail!!
        AlertDialog(
            onDismissRequest = { selectedBadgeForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (b.levelNumber != null) {
                        MigLevelBadgeView(
                            level = b.levelNumber,
                            size = 38.dp,
                            isOwned = b.isOwned
                        )
                    } else {
                        MigSpecialBadgeView(
                            badgeNumber = b.badgeNumber,
                            icon = b.icon,
                            size = 38.dp,
                            isOwned = b.isOwned
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(b.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0288D1))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(b.description, fontSize = 13.sp, color = Color(0xFF37474F))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Text("Category: ${b.category}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Rarity: ${b.rarity}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    
                    val grantMethodText = when (b.grantType) {
                        "ADMIN_ONLY" -> "👑 Granted exclusively by official mig33 Administration & Developers"
                        "RECHARGE" -> "💳 Obtained via credit recharging, credit merchant network & VIP tiers"
                        "SPECIAL_EVENT" -> "🎉 Special historic occasions, tournaments & annual anniversaries"
                        "LEVEL_LOCK" -> "📈 Exclusively unlocked by reaching the required migLevel on the XP curve"
                        else -> "🎯 Activity criteria, virtual gifts & room interactions"
                    }
                    Text("Grant Method: $grantMethodText", fontSize = 11.sp, color = Color(0xFF455A64), fontWeight = FontWeight.SemiBold)

                    Text("Requirement: ${b.unlockCriteria}", fontSize = 12.sp, color = Color(0xFF0288D1), fontWeight = FontWeight.Medium)
                    if (b.isOwned) {
                        Text("Status: ✓ Unlocked", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        if (b.unlockedDate != null) {
                            Text("Unlocked: ${b.unlockedDate}", fontSize = 11.sp, color = Color(0xFF558B2F))
                        }
                    } else {
                        if (b.requiredLevel != null) {
                            val reqXP = MigLevelCalculator.xpRequiredForLevel(b.requiredLevel)
                            val remainingXP = (reqXP - userProfile.points).coerceAtLeast(0)
                            Text(
                                text = "Status: 🔒 Locked (Current Level: ${userProfile.migLevel} / ${b.requiredLevel})",
                                fontSize = 12.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Need %,d more XP to reach Level ${b.requiredLevel}".format(remainingXP),
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text("Status: 🔒 Locked", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (b.isOwned) {
                        val badgeKey = b.badgeNumber ?: "lvl_${b.levelNumber}"
                        val isEquipped = userProfile.equippedBadgeNumbers.contains(badgeKey)
                        Button(
                            onClick = {
                                repository.toggleEquipBadge(badgeKey)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) Color(0xFFC62828) else Color(0xFF2E7D32)
                            )
                        ) {
                            Text(
                                text = if (isEquipped) "إلغاء التفعيل (Unequip)" else "استخدام الشارة (Equip Badge)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    TextButton(onClick = { selectedBadgeForDetail = null }) {
                        Text("إغلاق (Close)")
                    }
                }
            }
        )
    }
}
