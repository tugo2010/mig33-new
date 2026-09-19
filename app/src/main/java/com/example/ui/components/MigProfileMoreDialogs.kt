package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.MigRepository
import com.example.model.MigBadge
import com.example.model.MigLevelCalculator
import com.example.model.UserProfile

@Composable
fun MigBadgesShowcaseDialog(
    repository: MigRepository,
    onDismiss: () -> Unit
) {
    val userProfile by repository.userProfile.collectAsState()
    val allBadges = remember(userProfile.migLevel, userProfile.points) {
        MigLevelCalculator.getBadgesForUser(userProfile.migLevel, userProfile.points.toLong())
    }
    val ownedCount = remember(allBadges) { allBadges.count { it.isOwned } }
    val categories = remember(ownedCount) {
        listOf(
            "All (الكل)",
            "Owned ($ownedCount)",
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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF01579B)))
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "mig33 Badges Showcase",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Owned: ${allBadges.count { it.isOwned }} / ${allBadges.size} Badges",
                                    color = Color(0xFFB3E5FC),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Category Filter Scrollable Bar
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
                        .padding(horizontal = 10.dp, vertical = 4.dp),
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
                                // Badge Icon Container
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

                                    // Grant Type Tag
                                    Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        when {
                                            badge.grantType == "ADMIN_ONLY" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFFFF8E1))
                                                        .border(0.5.dp, Color(0xFFFFB300), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        if (badge.category == "Verification") "🔷 Verification (توثيق وتفعيل من الإدارة)"
                                                        else "👑 Admin & Dev (تمنح من الإدارة والمطورين)",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFFF57F17),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            badge.grantType == "RECHARGE" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFE8F5E9))
                                                        .border(0.5.dp, Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("💳 Recharge & VIP (عن طريق الشحن والرصيد)", fontSize = 9.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            badge.category == "Rooms & Hosts" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFEDE7F6))
                                                        .border(0.5.dp, Color(0xFF7E57C2), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("🏠 Rooms (خاصة بالرومات والمضيفين)", fontSize = 9.sp, color = Color(0xFF512DA8), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            badge.category == "Gifts & Romance" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFFCE4EC))
                                                        .border(0.5.dp, Color(0xFFEC407A), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("🎁 Gifts (عن طريق الهدايا والتفاعل)", fontSize = 9.sp, color = Color(0xFFC2185B), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            badge.grantType == "SPECIAL_EVENT" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFF3E5F5))
                                                        .border(0.5.dp, Color(0xFFAB47BC), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("🎉 Special Event (مناسبات خاصة تمنح من الأدمن)", fontSize = 9.sp, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            badge.grantType == "LEVEL_LOCK" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFFE1F5FE))
                                                        .border(0.5.dp, Color(0xFF0288D1), RoundedCornerShape(2.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("📈 Level Locked (مقيدة بالمستوى)", fontSize = 9.sp, color = Color(0xFF0277BD), fontWeight = FontWeight.Bold)
                                                }
                                            }
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

                                // Status & Equip Action Button
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

                // Footer Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

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
                    
                    // Grant Method Info
                    val grantMethodText = when (b.grantType) {
                        "ADMIN_ONLY" -> "👑 Granted exclusively by official mig33 Administration & Developers (تمنح حصرياً من قبل إدارة التطبيق والمطورين)"
                        "RECHARGE" -> "💳 Obtained via credit recharging, credit merchant network & VIP tiers (عن طريق شحن الرصيد والتجار وعضويات VIP)"
                        "SPECIAL_EVENT" -> "🎉 Special historic occasions, tournaments & annual anniversaries (مناسبات خاصة ومسابقات كبرى وإرث تاريخي)"
                        "LEVEL_LOCK" -> "📈 Exclusively unlocked by reaching the required migLevel on the XP curve (بتحقيق المستوى المطلوب حصرياً)"
                        else -> "🎯 Activity criteria, virtual gifts & room interactions (عن طريق التفاعل والنشاط والهدايا)"
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

@Composable
fun MigFullEditProfileDialog(
    userProfile: UserProfile,
    repository: MigRepository,
    onDismiss: () -> Unit
) {
    var statusMessage by remember { mutableStateOf(userProfile.statusMessage) }
    var gender by remember { mutableStateOf(userProfile.gender) }
    var birthday by remember { mutableStateOf(userProfile.birthday) }
    var country by remember { mutableStateOf(userProfile.country) }
    var isOnline by remember { mutableStateOf(userProfile.isOnline) }
    var isInvisible by remember { mutableStateOf(userProfile.isInvisible) }
    var saveSuccess by remember { mutableStateOf(false) }

    val countryOptions = listOf("Singapore", "Indonesia", "Malaysia", "India", "Nepal", "Maldives", "Philippines", "United States", "United Kingdom", "Australia", "Saudi Arabia", "UAE", "Egypt")
    var countryMenuExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFFFF9800), Color(0xFFE65100)))
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚙️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Edit mig33 Profile",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Scrollable Form Container
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (saveSuccess) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E7D32))
                                .padding(8.dp)
                        ) {
                            Text("✓ Profile successfully updated!", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 1. Account Identity Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ACCOUNT IDENTITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Username: @${userProfile.username} (Fixed)", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                    }

                    // 2. Status & Bio Bubble Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("STATUS BUBBLE & MOTTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = statusMessage,
                                onValueChange = { statusMessage = it },
                                label = { Text("What's on your mind? (Status)") },
                                placeholder = { Text("e.g. Hello world! Let's chat!") },
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth().testTag("edit_status_input")
                            )
                        }
                    }

                    // 3. Personal Details Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PERSONAL INFORMATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Gender selector
                            Text("Gender:", fontSize = 12.sp, color = Color(0xFF546E7A))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Male", "Female", "Other").forEach { g ->
                                    val isSel = gender == g
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSel) Color(0xFF0288D1) else Color(0xFFECEFF1))
                                            .clickable { gender = g }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = g,
                                            color = if (isSel) Color.White else Color(0xFF37474F),
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Birthday
                            OutlinedTextField(
                                value = birthday,
                                onValueChange = { birthday = it },
                                label = { Text("Birthday") },
                                placeholder = { Text("e.g. 1 Jan 1988") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("edit_birthday_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Country Selector Dropdown
                            OutlinedTextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country / Region") },
                                trailingIcon = {
                                    IconButton(onClick = { countryMenuExpanded = !countryMenuExpanded }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Country")
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("edit_country_input")
                            )
                            DropdownMenu(
                                expanded = countryMenuExpanded,
                                onDismissRequest = { countryMenuExpanded = false }
                            ) {
                                countryOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            country = opt
                                            countryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4. Privacy & Presence Settings Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PRESENCE & PRIVACY SETTINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Show Online Status", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Display green circle on your avatar", fontSize = 11.sp, color = Color(0xFF757575))
                                }
                                Switch(
                                    checked = isOnline,
                                    onCheckedChange = { isOnline = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF9800), checkedTrackColor = Color(0xFFFFCC80))
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Invisible Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Appear offline in room user lists", fontSize = 11.sp, color = Color(0xFF757575))
                                }
                                Switch(
                                    checked = isInvisible,
                                    onCheckedChange = { isInvisible = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0288D1), checkedTrackColor = Color(0xFF81D4FA))
                                )
                            }
                        }
                    }
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF757575))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            repository.updateFullProfile(
                                displayName = userProfile.displayName,
                                statusMessage = statusMessage,
                                gender = gender,
                                birthday = birthday,
                                country = country,
                                isOnline = isOnline,
                                isInvisible = isInvisible
                            )
                            saveSuccess = true
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.testTag("save_full_profile_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MigLevelSystemDialog(
    userProfile: UserProfile,
    repository: MigRepository,
    onDismiss: () -> Unit
) {
    val currentPoints = userProfile.points.toLong()
    val progression = remember(userProfile.points, userProfile.migLevel) {
        MigLevelCalculator.calculateProgression(currentPoints)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF263238), Color(0xFF102027)))
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(progression.rankIcon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "mig33 Level & XP System",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Authentic Exponential Difficulty Curve",
                                    color = Color(0xFF80CBC4),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Current Level & Progress Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFF9800), Color(0xFFFFB74D)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    MigLevelBadgeView(
                                        level = progression.currentLevel,
                                        size = 46.dp,
                                        isOwned = true
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "migLevel ${progression.currentLevel}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Color(0xFFE65100)
                                        )
                                        Text(
                                            text = progression.rankTitle,
                                            fontSize = 12.sp,
                                            color = Color(0xFF5D4037),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${progression.currentXP} XP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0288D1)
                                    )
                                    Text(
                                        text = "Next: ${progression.xpForNextLevel} XP",
                                        fontSize = 11.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // XP Progress Bar
                            LinearProgressIndicator(
                                progress = { progression.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = Color(0xFFFF9800),
                                trackColor = Color(0xFFEEEEEE),
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Level ${progression.currentLevel} (${progression.xpForCurrentLevel} XP)",
                                    fontSize = 10.sp,
                                    color = Color(0xFF757575)
                                )
                                Text(
                                    text = "${(progression.progressPercent * 100).toInt()}% Progress",
                                    fontSize = 10.sp,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Level ${progression.currentLevel + 1} (${progression.xpForNextLevel} XP)",
                                    fontSize = 10.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }

                    // How to earn XP in mig33 Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("⚡ HOW TO EARN XP & POINTS IN MIG33", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                            Spacer(modifier = Modifier.height(8.dp))

                            val xpActions = listOf(
                                Triple("⏳ Active Online Time", "+12 XP/hr", "Gain 12 XP per hour while using mig33"),
                                Triple("💬 Send Chat Message", "+8 XP", "Participate in room chit-chat"),
                                Triple("🎁 Send Virtual Gift", "+35 XP", "Send roses, crowns & sports cars"),
                                Triple("📝 Publish Feed Post", "+25 XP", "Share updates on mini-blog"),
                                Triple("💬 Reply to Posts", "+15 XP", "Engage with friends' posts"),
                                Triple("🏆 Win Trivia / Games", "+30 XP", "Answer questions in Danger 1"),
                                Triple("👥 Add Friends", "+20 XP", "Expand your mig33 social circle"),
                                Triple("🎁 Claim Daily Bonus", "+50 XP", "Daily login streak reward")
                            )

                            xpActions.forEach { act ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(act.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF263238))
                                        Text(act.third, fontSize = 10.sp, color = Color(0xFF757575))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFE1F5FE))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(act.second, color = Color(0xFF0288D1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Level Milestones Table
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📜 CLASSIC LEVEL MILESTONES & REWARDS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.height(8.dp))

                            val milestones = listOf(
                                Triple("Level 1-4", "0 - 1,500 XP", "Beginner avatar & standard chat access"),
                                Triple("Level 5-9", "2,250 - 10,000 XP", "Custom colored status bubble & smiley packs"),
                                Triple("Level 10-19", "12,250 - 65,000 XP", "Create custom chatrooms & host games"),
                                Triple("Level 20-29", "72,500 - 190,000 XP", "Robot 🤖 & Star badges, VIP room priority"),
                                Triple("Level 30-49", "205,000 - 750,000 XP", "Grand Master aura & golden nickname style"),
                                Triple("Level 50-99", "800,000 - 4,500,000 XP", "Royal Emperor Crown & Global megaphones"),
                                Triple("Level 100-150", "4,800,000 - 13,500,000 XP", "Supreme Demiurge & Absolute Hall of Fame")
                            )

                            milestones.forEach { m ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(m.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF263238))
                                        Text(m.third, fontSize = 10.sp, color = Color(0xFF546E7A))
                                    }
                                    Text(m.second, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                }
                                HorizontalDivider(color = Color(0xFFF0F0F0))
                            }
                        }
                    }

                    // 150 Level Badges Preview Gallery Card
                    var showAll150Levels by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "🎖️ ALL 150 LEVEL BADGES (150 شارة مستوى)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        "Matching assets: mig_levels/1.png to mig_levels/150.png",
                                        fontSize = 10.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                                TextButton(onClick = { showAll150Levels = !showAll150Levels }) {
                                    Text(
                                        if (showAll150Levels) "Hide List" else "View All (1-150)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Horizontal Quick Preview of Key Milestone Badges
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                listOf(1, 10, 24, 50, 100, 150).forEach { lvl ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        MigLevelBadgeView(
                                            level = lvl,
                                            size = 36.dp,
                                            isOwned = progression.currentLevel >= lvl
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            "L$lvl",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (progression.currentLevel >= lvl) Color(0xFFE65100) else Color.Gray
                                        )
                                    }
                                }
                            }

                            if (showAll150Levels) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    (1..150).forEach { lvl ->
                                        val isAchieved = progression.currentLevel >= lvl
                                        val xp = MigLevelCalculator.xpRequiredForLevel(lvl)
                                        val title = MigLevelCalculator.getRankTitle(lvl)

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (isAchieved) Color(0xFFFFF8E1) else Color(0xFFF9F9F9),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            MigLevelBadgeView(
                                                level = lvl,
                                                size = 30.dp,
                                                isOwned = isAchieved
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    "Level $lvl: $title",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAchieved) Color(0xFFE65100) else Color(0xFF424242)
                                                )
                                                Text(
                                                    "Required: %,d XP".format(xp),
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF757575)
                                                )
                                            }
                                            if (isAchieved) {
                                                Text("✓", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                            } else {
                                                Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(12.dp), tint = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Simulator Button: Gain +100 XP to test
                    Button(
                        onClick = {
                            repository.addXP(100)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().testTag("gain_test_xp_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Practice Training: +100 XP", fontWeight = FontWeight.Bold)
                    }
                }

                // Footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
