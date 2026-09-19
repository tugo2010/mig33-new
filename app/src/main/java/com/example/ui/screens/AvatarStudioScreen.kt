package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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
import com.example.data.MigRepository
import com.example.model.AvatarAssets
import com.example.model.AvatarConfig
import com.example.model.MigPresetAvatar
import com.example.ui.components.AvatarView
import com.example.ui.components.MigHeaderBar

enum class Avatar2DCategory(val title: String, val titleAr: String, val icon: String) {
    ALL("All 2D Avatars", "جميع الأطقم", "🌟"),
    ROYALTY("Royalty & Couture", "الأزياء الملكية", "👑"),
    REBEL_STREET("Rebel & Streetwear", "الستريت والتيكوير", "🔥"),
    DARK_FANTASY("Gothic & Fantasy", "القوطي والفانتازيا", "🎭"),
    CLASSIC("Classic mig33", "كلاسيكيات 2008", "🕹️")
}

@Composable
fun AvatarStudioScreen(
    repository: MigRepository,
    onBack: () -> Unit,
    onOpen2DBuilder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val userProfile by repository.userProfile.collectAsState()
    var currentConfig by remember(userProfile.avatarConfig) { mutableStateOf(userProfile.avatarConfig) }
    var selectedCategory by remember { mutableStateOf(Avatar2DCategory.ALL) }
    var isSaved by remember { mutableStateOf(false) }

    val filteredPresets = remember(selectedCategory) {
        when (selectedCategory) {
            Avatar2DCategory.ALL -> AvatarAssets.PRESETS
            Avatar2DCategory.ROYALTY -> AvatarAssets.PRESETS.filter {
                it.id in listOf("mig_holo_noir_couture", "mig_crimson_rose", "mig_sapphire_empress", "mig_holo_princess", "mig_pink_coquette")
            }
            Avatar2DCategory.REBEL_STREET -> AvatarAssets.PRESETS.filter {
                it.id in listOf("mig_kagero_rebel", "mig_shinya_flame", "mig_cyber_techwear", "mig_dark_angel_punk", "mig_street_kitty")
            }
            Avatar2DCategory.DARK_FANTASY -> AvatarAssets.PRESETS.filter {
                it.id in listOf("mig_shadow_tuxedo", "mig_blood_moon", "mig_pierrot_jester", "mig_shadow_lord", "mig_cyber_assassin", "mig_dark_angel_punk")
            }
            Avatar2DCategory.CLASSIC -> AvatarAssets.PRESETS.filter {
                it.id in listOf("mig_denim_roses", "mig_leopard_chic", "mig_rocker_star", "mig_hipst4r_classic", "mig_b4sejump_retro", "mig_flyingkit_retro")
            }
        }
    }

    val selectedPreset = remember(currentConfig) {
        AvatarAssets.PRESETS.firstOrNull { it.id == currentConfig.presetId || it.drawableRes == currentConfig.customImageRes }
            ?: AvatarAssets.PRESETS.first()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onBack)
                // Studio Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFE91E63), Color(0xFFC2185B))
                            )
                        )
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.clickable { onBack() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2D Avatar Boutique",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIP Level ${userProfile.migLevel}",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("avatar_save_bar"),
                color = Color(0xFF1E262B),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedPreset.name,
                            color = Color(0xFFFFD54F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (selectedPreset.nameAr.isNotBlank()) selectedPreset.nameAr else "طقم أنمي فاخر 2D",
                            color = Color(0xFFB0BEC5),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            repository.updateAvatarConfig(currentConfig)
                            isSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) Color(0xFF388E3C) else Color(0xFFE91E63)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("save_avatar_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSaved) "Equipped & Saved!" else "Equip / ارتداء وحفظ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
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
                .background(Color(0xFF0F141A))
                .testTag("avatar_studio_screen")
        ) {
            // 1. Showcase Hero Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A232A), Color(0xFF0F141A))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Avatar Card Preview
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .fillMaxHeight()
                    ) {
                        AvatarView(
                            config = currentConfig,
                            modifier = Modifier.fillMaxSize(),
                            cornerRadius = 10.dp,
                            borderColor = Color(0xFFFFB74D),
                            borderWidth = 2.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Details Card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE91E63))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = selectedPreset.badgeLabel,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = selectedPreset.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (selectedPreset.nameAr.isNotBlank()) {
                            Text(
                                text = selectedPreset.nameAr,
                                color = Color(0xFFFFD54F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = selectedPreset.description,
                            color = Color(0xFFB0BEC5),
                            fontSize = 10.5.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2. Filter Tabs Strip
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF182026))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Avatar2DCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color(0xFFE91E63) else Color(0xFF263238)
                            )
                            .clickable {
                                selectedCategory = cat
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("avatar_cat_${cat.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = cat.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cat.titleAr,
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 3. 2D Avatar Collection List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPresets) { preset ->
                    val isEquipped = currentConfig.presetId == preset.id || currentConfig.customImageRes == preset.drawableRes

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isSaved = false
                                currentConfig = preset.config.copy(
                                    presetId = preset.id,
                                    customImageRes = preset.drawableRes
                                )
                            }
                            .testTag("studio_preset_${preset.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEquipped) Color(0xFF241B26) else Color(0xFF1E262B)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isEquipped) 2.dp else 1.dp,
                            if (isEquipped) Color(0xFFE91E63) else Color(0xFF37474F)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = preset.drawableRes),
                                    contentDescription = preset.name,
                                    modifier = Modifier
                                        .size(width = 65.dp, height = 90.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            1.dp,
                                            if (isEquipped) Color(0xFFE91E63) else Color(0xFFFFB74D),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = preset.name,
                                            color = if (isEquipped) Color(0xFFFF80AB) else Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE91E63))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = preset.badgeLabel,
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (preset.nameAr.isNotBlank()) {
                                        Text(
                                            text = preset.nameAr,
                                            color = Color(0xFFFFD54F),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = preset.description,
                                        color = Color(0xFFB0BEC5),
                                        fontSize = 10.5.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Equipped",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
