package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.model.BannedUser
import com.example.model.MigBadge
import com.example.model.MigLevelCalculator
import com.example.model.hasAdminBadge
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigSpecialBadgeView

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()
    val bannedUsers by repository.bannedUsers.collectAsState()
    val adminLogs by repository.adminLogs.collectAsState()

    val isAdmin = remember(userProfile) { userProfile.hasAdminBadge() }

    // Global target user selected across all tabs for maximum ease of use
    var globalTargetUser by remember { mutableStateOf(userProfile.username) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Presets, 1: All Badges, 2: Kick & Notice, 3: Level Up, 4: Ban & Unban, 5: Logs
    var actionNotification by remember { mutableStateOf<String?>(null) }

    val allSpecialBadges = remember { MigLevelCalculator.ALL_SPECIAL_BADGES }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                // Admin Ribbon Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFB71C1C), Color(0xFF880E4F), Color(0xFF4A148C)))
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
                            text = "👑 Admin Control Panel (لوحة تحكم الأدمن السريعة)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (isAdmin) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD54F))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ADMIN ACTIVE ✓", color = Color(0xFF3E2723), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (!isAdmin) {
            // Access Denied Screen for non-admin users
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFECEFF1))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Denied",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "⛔ Access Denied (غير مصرح لك بالدخول)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "هذه الصفحة مخصصة فقط للأدمن وحاملي الشارات الإدارية في mig33.\n\n(This control panel is strictly reserved for official mig33 Administrators and users with an Admin Badge).",
                            fontSize = 12.sp,
                            color = Color(0xFF455A64),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "العودة إلى القائمة الرئيسية (Go Back)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else {
            // Admin Panel Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFECEFF1))
                    .testTag("admin_dashboard_screen")
            ) {
                // Top Global Target User Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Target", tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "المستخدم المستهدف الإجمالي (Target User):",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "@$globalTargetUser",
                                color = Color(0xFFFFD54F),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = globalTargetUser,
                                onValueChange = { globalTargetUser = it },
                                placeholder = { Text("اكتب اسم المستخدم...", color = Color.Gray, fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFD54F),
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF333333),
                                    unfocusedContainerColor = Color(0xFF333333)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Select Chips (Horizontally Scrollable)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("اختيار سريع:", color = Color.LightGray, fontSize = 10.sp)
                            listOf(userProfile.username, "spammer99", "troll_x", "room_intruder", "f1yingkit3", "danger_master").forEach { name ->
                                val isSelected = globalTargetUser.equals(name, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isSelected) Color(0xFFFFD54F) else Color(0xFF424242))
                                        .clickable { globalTargetUser = name }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "@$name",
                                        color = if (isSelected) Color(0xFF212121) else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Notification Toast
                if (actionNotification != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = actionNotification!!,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            IconButton(onClick = { actionNotification = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                    }
                }

                // Scrollable Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 6.dp,
                    containerColor = Color.White,
                    contentColor = Color(0xFFB71C1C)
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("⚡ بنقرة واحدة", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("🏆 الشارات (50+)", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("🚨 طرد وتنبيه", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                        Text("📈 المستوى والنقاط", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }) {
                        Text("⛔ الحظر (${bannedUsers.size})", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }) {
                        Text("📜 سجل الإجراءات", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }) {
                        Text("🎨 ألوان الإدارة", modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Tab Content View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    when (selectedTab) {
                        0 -> QuickPresetsTab(repository, globalTargetUser, allSpecialBadges) { actionNotification = it }
                        1 -> AllSpecialBadgesTab(repository, globalTargetUser, allSpecialBadges) { actionNotification = it }
                        2 -> KickAndNoticeTab(repository, globalTargetUser) { actionNotification = it }
                        3 -> LevelAndXPTab(repository, globalTargetUser) { actionNotification = it }
                        4 -> BanAndUnbanTab(repository, globalTargetUser, bannedUsers) { actionNotification = it }
                        5 -> AdminLogsTab(adminLogs)
                        6 -> AdminNameColorsTab(allSpecialBadges) { actionNotification = it }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 0: QUICK PRESETS (إجراءات بنقرة واحدة)
// ==========================================
@Composable
fun QuickPresetsTab(
    repository: MigRepository,
    targetUsername: String,
    allBadges: List<MigBadge>,
    onNotify: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Quick", tint = Color(0xFFD81B60))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ إجراءات سريعة بنقرة واحدة للمستخدم: @$targetUsername",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFD81B60)
                    )
                }

                Text(
                    text = "نفّذ القرارات الإدارية الفورية بدون كتابة نماذج طويلة:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                // Grid of Preset Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Kick
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.kickUser(targetUsername.trim(), "Quick Admin Kick (طرد سريع)", 15)
                                    onNotify("تم طرد @${targetUsername.trim()} لمدة 15 دقيقة بنجاح!")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🚨 طرد 15 دقيقة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Quick Ban 24h
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.banUser(targetUsername.trim(), "Quick Admin Ban 24h (حظر مؤقت 24 ساعة)", 24)
                                    onNotify("تم حظر @${targetUsername.trim()} لمدة 24 ساعة بنجاح!")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⛔ حظر 24 ساعة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Grant Admin Badge #001
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.grantBadgeToUser(targetUsername.trim(), "001")
                                    onNotify("تم منح شارة الأدمن الرسمية #001 للمستخدم @${targetUsername.trim()} بنجاح! 👑")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "👑 منح شارة الأدمن",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Grant Verified Blue #011
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.grantBadgeToUser(targetUsername.trim(), "011")
                                    onNotify("تم منح النجمة الزرقاء الموثقة #011 للمستخدم @${targetUsername.trim()} بنجاح! 🔷")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🔷 توثيق بنجمة زرقاء",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Level Up +10
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.increaseUserLevel(targetUsername.trim(), 10)
                                    onNotify("تم رفع مستوى @${targetUsername.trim()} بمقدار +10 مستويات بنجاح! 📈")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "📈 رفع المستوى (+10)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Send Warning Notice
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.sendNoticeToUser(targetUsername.trim(), "تنبيه رسمي من الإدارة: يرجى الالتزام بقوانين الرومات والدردشة.")
                                    onNotify("تم إرسال تنبيه رسمي إلى @${targetUsername.trim()} بنجاح! 💬")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "💬 إرسال تحذير رسمي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Unban
                        Button(
                            onClick = {
                                if (targetUsername.isNotBlank()) {
                                    repository.unbanUser(targetUsername.trim())
                                    onNotify("تم فك حظر @${targetUsername.trim()} بنجاح! 🔓")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 42.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🔓 فك الحظر الفوري عن @$targetUsername",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: ALL SPECIAL BADGES (عرض وتصفية جميع الشارات 50+)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllSpecialBadgesTab(
    repository: MigRepository,
    targetUsername: String,
    allBadges: List<MigBadge>,
    onNotify: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember {
        listOf("All", "Admin & Staff", "Verification", "Merchants & Recharging", "VIP & Crowns", "Gifts & Romance", "Games & Trivia", "Rooms & Hosts", "Special & Events")
    }

    val filteredBadges = remember(searchQuery, selectedCategory) {
        allBadges.filter { badge ->
            val matchesCategory = (selectedCategory == "All" || badge.category.equals(selectedCategory, ignoreCase = true))
            val matchesSearch = searchQuery.isBlank() ||
                    badge.name.contains(searchQuery, ignoreCase = true) ||
                    badge.description.contains(searchQuery, ignoreCase = true) ||
                    (badge.badgeNumber != null && badge.badgeNumber.contains(searchQuery))
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث عن شارة باسمها، رقمها أو الوصف...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        // Categories Filter Chips (Horizontally Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { cat ->
                val isSel = selectedCategory == cat
                val displayTitle = when(cat) {
                    "All" -> "الكل (${allBadges.size})"
                    "Admin & Staff" -> "الإدارة والصيانة"
                    "Verification" -> "التوثيق والتأكيد"
                    "Merchants & Recharging" -> "التجارة والشحن"
                    "VIP & Crowns" -> "كبار الشخصيات"
                    "Gifts & Romance" -> "الهدايا والرومانسية"
                    "Games & Trivia" -> "الألعاب والمسابقات"
                    "Rooms & Hosts" -> "الرومات والاستضافة"
                    else -> "المناسبات والفعاليات"
                }
                FilterChip(
                    selected = isSel,
                    onClick = { selectedCategory = cat },
                    label = { Text(displayTitle, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7B1FA2),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "قائمة جميع شارات mig33 الرسمية (${filteredBadges.size} شارة):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = "المستهدف: @$targetUsername",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2)
            )
        }

        if (filteredBadges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد شارات تطابق نتائج البحث", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredBadges) { badge ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MigSpecialBadgeView(
                                badgeNumber = badge.badgeNumber,
                                icon = badge.icon,
                                size = 36.dp,
                                isOwned = true
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${badge.badgeNumber ?: "000"}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF7B1FA2)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = badge.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF263238),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = badge.description,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFEDE7F6))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(badge.category, fontSize = 9.sp, color = Color(0xFF512DA8), fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFFFF3E0))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(badge.rarity, fontSize = 9.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = {
                                    val target = if (targetUsername.isBlank()) "self" else targetUsername.trim()
                                    val badgeKey = badge.badgeNumber ?: badge.id
                                    repository.grantBadgeToUser(target, badgeKey)
                                    onNotify("تم منح الشارة #${badge.badgeNumber} (${badge.name}) للمستخدم @$target بنجاح! 🏆")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("🏆 منح", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: KICK & NOTICE (طرد وتنبيه رسمي)
// ==========================================
@Composable
fun KickAndNoticeTab(
    repository: MigRepository,
    targetUsername: String,
    onNotify: (String) -> Unit
) {
    var kickReason by remember { mutableStateOf("Violation of chatroom safety guidelines") }
    var kickDurationMins by remember { mutableStateOf(15) }
    var noticeText by remember { mutableStateOf("تنبيه رسمي من إدارة mig33: يرجى الالتزام بالآداب العامة وقوانين الرومات.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Send Warning Notice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("💬 إرسال تنبيه أو تحذير إداري رسمي (Official Notice)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFF57C00))

                OutlinedTextField(
                    value = noticeText,
                    onValueChange = { noticeText = it },
                    label = { Text("نص التنبيه الإداري (Warning Message)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(6.dp)
                )

                Button(
                    onClick = {
                        if (targetUsername.isNotBlank() && noticeText.isNotBlank()) {
                            repository.sendNoticeToUser(targetUsername.trim(), noticeText.trim())
                            onNotify("تم إرسال التنبيه الإداري الرسمي إلى @${targetUsername.trim()} بنجاح!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notice")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💬 إرسال التنبيه الآن", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Kick Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🚨 طرد مستخدم من الرومات (Kick User)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD32F2F))

                OutlinedTextField(
                    value = kickReason,
                    onValueChange = { kickReason = it },
                    label = { Text("سبب الطرد (Kick Reason)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("مدة الطرد بالدقائق:", fontSize = 11.sp, color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 15, 30, 60, 1440).forEach { mins ->
                        FilterChip(
                            selected = kickDurationMins == mins,
                            onClick = { kickDurationMins = mins },
                            label = { Text(if (mins >= 1440) "24h" else "${mins}m", fontSize = 11.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (targetUsername.isNotBlank()) {
                            repository.kickUser(targetUsername.trim(), kickReason, kickDurationMins)
                            onNotify("تم طرد المستخدم @${targetUsername.trim()} لمدة $kickDurationMins دقيقة بنجاح!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.PersonRemove, contentDescription = "Kick")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🚨 تنفيذ الطرد فوراً", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ==========================================
// TAB 3: LEVEL & XP BOOST (رفع المستوى والنقاط)
// ==========================================
@Composable
fun LevelAndXPTab(
    repository: MigRepository,
    targetUsername: String,
    onNotify: (String) -> Unit
) {
    var levelsToAdd by remember { mutableStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📈 رفع مستوى الحساب والنقاط (Level & XP Boost)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0288D1))

                Text(
                    text = "المستخدم المستهدف: @$targetUsername",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0288D1)
                )

                Text("اختر عدد المستويات الإضافية لتزويدها للحساب:", fontSize = 12.sp, color = Color.Gray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 5, 10, 25, 50).forEach { lvl ->
                        FilterChip(
                            selected = levelsToAdd == lvl,
                            onClick = { levelsToAdd = lvl },
                            label = { Text("+$lvl Levels", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (targetUsername.isNotBlank()) {
                            repository.increaseUserLevel(targetUsername.trim(), levelsToAdd)
                            onNotify("تم زيادة مستوى @${targetUsername.trim()} بمقدار +$levelsToAdd مستويات بنجاح! 📈")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Level Up")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📈 تطبيق رفع المستوى والنقاط", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ==========================================
// TAB 4: BAN & UNBAN (حظر وفك الحظر)
// ==========================================
@Composable
fun BanAndUnbanTab(
    repository: MigRepository,
    targetUsername: String,
    bannedUsers: List<BannedUser>,
    onNotify: (String) -> Unit
) {
    var banReason by remember { mutableStateOf("Violation of Terms of Service and community safety rules") }
    var banDurationHours by remember { mutableStateOf(24) }
    var searchUnbanQuery by remember { mutableStateOf("") }

    val filteredBannedUsers = remember(bannedUsers, searchUnbanQuery) {
        if (searchUnbanQuery.isBlank()) bannedUsers
        else bannedUsers.filter { it.username.contains(searchUnbanQuery, ignoreCase = true) || it.reason.contains(searchUnbanQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Ban Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⛔ حظر مستخدم مع بيان السبب (Ban User)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFC62828))

                Text("المستهدف: @$targetUsername", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC62828))

                OutlinedTextField(
                    value = banReason,
                    onValueChange = { banReason = it },
                    label = { Text("سبب الحظر التفصيلي (Ban Reason)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(6.dp)
                )

                Text("مدة الحظر بالساعات:", fontSize = 11.sp, color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(12, 24, 72, 168, 8760).forEach { hrs ->
                        FilterChip(
                            selected = banDurationHours == hrs,
                            onClick = { banDurationHours = hrs },
                            label = {
                                val txt = when (hrs) {
                                    12 -> "12h"
                                    24 -> "24h (1D)"
                                    72 -> "3 Days"
                                    168 -> "7 Days"
                                    else -> "Permanent (دائم)"
                                }
                                Text(txt, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (targetUsername.isNotBlank() && banReason.isNotBlank()) {
                            repository.banUser(targetUsername.trim(), banReason.trim(), banDurationHours)
                            onNotify("تم حظر المستخدم @${targetUsername.trim()} بنجاح! ⛔")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Block, contentDescription = "Ban")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⛔ تنفيذ الحظر الآن", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Banned Users List Header & Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("قائمة المحظورين حالياً (${bannedUsers.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }

        OutlinedTextField(
            value = searchUnbanQuery,
            onValueChange = { searchUnbanQuery = it },
            placeholder = { Text("بحث في المحظورين بالاسم أو السبب...", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        if (filteredBannedUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("لا يوجد مستخدمين محظورين يطابقون البحث", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredBannedUsers) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("@${user.username}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFB71C1C))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("${user.durationHours}h", color = Color(0xFFC62828), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("السبب: ${user.reason}", fontSize = 11.sp, color = Color(0xFF424242))
                                Text("تاريخ الحظر: ${user.bannedAt}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    repository.unbanUser(user.username)
                                    onNotify("تم فك حظر @${user.username} بنجاح! 🔓")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("🔓 فك الحظر", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 5: ACTION LOGS (سجل الإجراءات والإرشادات)
// ==========================================
@Composable
fun AdminLogsTab(
    logs: List<com.example.model.AdminActionLog>
) {
    var logFilter by remember { mutableStateOf("ALL") }

    val filteredLogs = remember(logs, logFilter) {
        if (logFilter == "ALL") logs
        else logs.filter { it.actionType.equals(logFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("سجل القرارات والإجراءات الإدارية (${filteredLogs.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }

        // Log Filter Chips (Horizontally Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("ALL", "BAN", "KICK", "GRANT_BADGE", "LEVEL_UP", "NOTICE", "UNBAN").forEach { filterKey ->
                val isSel = logFilter == filterKey
                FilterChip(
                    selected = isSel,
                    onClick = { logFilter = filterKey },
                    label = { Text(filterKey, fontSize = 9.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد إجراءات إدارية مسجلة في هذا التصنيف", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (badgeBg, badgeText) = when (log.actionType) {
                                "BAN" -> Pair(Color(0xFFB71C1C), "BAN")
                                "KICK" -> Pair(Color(0xFFE65100), "KICK")
                                "GRANT_BADGE" -> Pair(Color(0xFF7B1FA2), "BADGE")
                                "LEVEL_UP" -> Pair(Color(0xFF0288D1), "LEVEL")
                                "NOTICE" -> Pair(Color(0xFFF57C00), "NOTICE")
                                else -> Pair(Color(0xFF2E7D32), "UNBAN")
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(badgeText, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("@${log.targetUsername}: ${log.details}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                                Text("Timestamp: ${log.timestamp}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 6: ADMIN NAME COLORS (ألوان الإدارة)
// ==========================================
@Composable
fun AdminNameColorsTab(
    allBadges: List<com.example.model.MigBadge>,
    onNotify: (String) -> Unit
) {
    // Fetch all special badges that can be equipped
    val adminBadges = remember(allBadges) {
        allBadges.filter { it.badgeNumber != null && !it.badgeNumber.startsWith("lvl_") }
    }
    
    val badgeColorsState by com.example.model.AdminSettings.badgeNameColors.collectAsState()

    val availableColors = listOf(
        Pair("أحمر ساطع (Red)", 0xFFE53935),
        Pair("أزرق بحري (Blue)", 0xFF1E88E5),
        Pair("ذهبي ملوكي (Gold)", 0xFFFF8F00),
        Pair("بنفسجي (Purple)", 0xFF8E24AA),
        Pair("أخضر داكن (Green)", 0xFF43A047),
        Pair("أسود فاحم (Black)", 0xFF212121),
        Pair("وردي (Pink)", 0xFFD81B60),
        Pair("أزرق فاتح (Cyan)", 0xFF00ACC1),
        Pair("برتقالي (Orange)", 0xFFF4511E)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("تخصيص ألوان الأسماء في الدردشة", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(4.dp))
                Text("حدد لون الاسم الذي سيظهر في غرف الدردشة لكل مستخدم يمتلك شارة إدارية محددة.", fontSize = 11.sp, color = Color.DarkGray)
            }
        }
        
        adminBadges.forEach { badge ->
            val currentColor = badgeColorsState[badge.badgeNumber] ?: 0xFF0288D1 // Default Mig Blue
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MigSpecialBadgeView(badgeNumber = badge.badgeNumber!!, icon = badge.icon, size = 28.dp, isOwned = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(badge.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
                            Text("رقم الشارة: ${badge.badgeNumber}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("اختر لون الاسم عند استخدام هذه الشارة:", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableColors.forEach { (colorName, colorVal) ->
                            val isSel = currentColor == colorVal
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    com.example.model.AdminSettings.setNameColorForBadge(badge.badgeNumber!!, colorVal)
                                    onNotify("تم تغيير لون الشارة ${badge.name} بنجاح!")
                                },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(colorVal)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(colorName, fontSize = 10.sp)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE3F2FD),
                                    selectedLabelColor = Color(0xFF1565C0)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
