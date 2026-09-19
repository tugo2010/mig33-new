package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MigRepository
import com.example.ui.components.MigAvatarCard
import com.example.ui.components.MigHeaderBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    repository: MigRepository,
    onOpenDrawer: () -> Unit,
    onBack: () -> Unit,
    onOpenAvatarStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()

    var statusInput by remember(userProfile.statusMessage) { mutableStateOf(userProfile.statusMessage) }
    var bioInput by remember(userProfile.bio) { mutableStateOf(userProfile.bio) }
    var genderInput by remember(userProfile.gender) { mutableStateOf(userProfile.gender) }
    var birthdayInput by remember(userProfile.birthday) { mutableStateOf(userProfile.birthday) }
    var countryInput by remember(userProfile.country) { mutableStateOf(userProfile.country) }
    var cityInput by remember(userProfile.city) { mutableStateOf(userProfile.city) }
    var relationshipInput by remember(userProfile.relationshipStatus) { mutableStateOf(userProfile.relationshipStatus) }
    var hobbiesInput by remember(userProfile.hobbies) { mutableStateOf(userProfile.hobbies) }
    var isInvisibleInput by remember(userProfile.isInvisible) { mutableStateOf(userProfile.isInvisible) }
    
    val allBadges = remember {
        listOf(
            "001" to "👑 Admin Badge",
            "002" to "🏆 Master VIP",
            "010" to "⚡ Moderator",
            "003" to "⭐ Star Chatter",
            "004" to "💎 Diamond Member",
            "005" to "🔥 Top Contributor"
        )
    }
    
    var selectedBadges by remember(userProfile.equippedBadgeNumbers) {
        mutableStateOf(userProfile.equippedBadgeNumbers.toSet())
    }
    
    var isSavedToastVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onOpenDrawer)
                // Ribbon Header
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
                            text = "⚙️ Edit Full Profile (تعديل البروفايل الكامل)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFECEFF1))
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .testTag("edit_profile_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Success Notification Banner
            if (isSavedToastVisible) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Saved", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تم حفظ جميع تعديلات البروفايل بنجاح! (Profile Saved Successfully)",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Avatar Preview Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(110.dp)
                            .clickable { onOpenAvatarStudio() }
                    ) {
                        MigAvatarCard(
                            avatarConfig = userProfile.avatarConfig,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "@${userProfile.username}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0288D1)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تغيير وتخصيص الأفاتار والملابس والخلفيات",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenAvatarStudio,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("فتح Avatar Studio 👗", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // 1. Primary Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "👤 المعلومات الأساسية (Basic Info)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0288D1)
                    )

                    // Status Message
                    Column {
                        Text("الحالة الشخصية (Status Message / Motto):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = statusInput,
                            onValueChange = { statusInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    // Bio / About Me
                    Column {
                        Text("النبذة الشخصية (Bio / About Me):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = bioInput,
                            onValueChange = { bioInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(6.dp)
                        )
                    }
                }
            }

            // 2. Personal & Social Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📋 التفاصيل الشخصية (Personal Details)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0288D1)
                    )

                    // Gender Selector
                    Column {
                        Text("الجنس (Gender):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Male", "Female", "Secret").forEach { g ->
                                FilterChip(
                                    selected = genderInput == g,
                                    onClick = { genderInput = g },
                                    label = { Text(g, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    // Birthday
                    Column {
                        Text("تاريخ الميلاد (Birthday):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = birthdayInput,
                            onValueChange = { birthdayInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("e.g. 1 Jan 1995") },
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    // Country & City Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("الدولة (Country):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = countryInput,
                                onValueChange = { countryInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("المدينة (City):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                    }

                    // Relationship Status
                    Column {
                        Text("الحالة الاجتماعية (Relationship Status):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Single", "In a Relationship", "Married", "Engaged", "It's Complicated", "Secret").forEach { status ->
                                FilterChip(
                                    selected = relationshipInput == status,
                                    onClick = { relationshipInput = status },
                                    label = { Text(status, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Hobbies & Interests
                    Column {
                        Text("الهوايات والاهتمامات (Hobbies & Interests):", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = hobbiesInput,
                            onValueChange = { hobbiesInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("e.g. Chatting, Gaming, Music, Travel") },
                            shape = RoundedCornerShape(6.dp)
                        )
                    }
                }
            }

            // 3. Privacy & Mode Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🔒 وضع الظهور والخصوصية (Privacy Settings)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0288D1)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وضع التخفي / إخفاء حالة الاتصال (Invisible Mode)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = "يظهرك كحالة غير متصل (Offline) للأصدقاء في الرومات والرادار",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = isInvisibleInput,
                            onCheckedChange = { isInvisibleInput = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0288D1))
                        )
                    }
                }
            }

            // 4. Equipped Badges Management
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎖️ الشارات المعروضة في البروفايل (Featured Badges)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0288D1)
                    )
                    Text(
                        text = "اختر الشارات التي تريد إبرازها بجانب اسمك ورأس البروفايل:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allBadges.forEach { (badgeKey, badgeTitle) ->
                            val isSelected = selectedBadges.contains(badgeKey)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newSet = mutableSetOf<String>()
                                    if (!isSelected) newSet.add(badgeKey)
                                    selectedBadges = newSet
                                },
                                label = { Text(badgeTitle, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save Button
            Button(
                onClick = {
                    repository.saveUserProfile(
                        displayName = userProfile.displayName, // Keep whatever is saved, or handle it as username if you prefer
                        statusMessage = statusInput,
                        bio = bioInput,
                        gender = genderInput,
                        birthday = birthdayInput,
                        country = countryInput,
                        city = cityInput,
                        relationshipStatus = relationshipInput,
                        hobbies = hobbiesInput,
                        isInvisible = isInvisibleInput,
                        equippedBadgeNumbers = selectedBadges.toList()
                    )
                    isSavedToastVisible = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 46.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "💾 حفظ كل التغييرات (Save Full Profile)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
