package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AvatarConfig

@Composable
fun MigAvatarCard(
    avatarConfig: AvatarConfig,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.5.dp, Color(0xFFD7CCC8), RoundedCornerShape(6.dp))
            .testTag("mig_avatar_card"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
                    )
                )
        ) {
            // 2D Layered Avatar View Rendering (Placeholder Shape System)
            AvatarView(
                config = avatarConfig,
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 0.dp,
                borderWidth = 0.dp
            )

            // Dynamic Accessory / Customization Badges Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pet indicator if equipped
                if (avatarConfig.pet != "none") {
                    val petEmoji = when {
                        avatarConfig.pet.contains("cat") -> "🐱"
                        avatarConfig.pet.contains("dog") || avatarConfig.pet.contains("husky") -> "🐶"
                        avatarConfig.pet.contains("drone") -> "🛸"
                        else -> "🐾"
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC3E2723))
                            .border(1.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = petEmoji, fontSize = 12.sp)
                    }
                }

                // VIP crown badge if high level or equipped
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC004D40))
                        .border(1.dp, Color(0xFF80CBC4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 12.sp)
                }
            }

            // Bottom Pet & Item summary banner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x99000000))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val clothesLabel = avatarConfig.clothes.replace("cloth_", "").replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }
                    val petLabel = if (avatarConfig.pet != "none") avatarConfig.pet.replace("pet_", "").replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() } else "No Pet"
                    Text(
                        text = "$clothesLabel • $petLabel",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
