package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun MigHeaderBar(
    onMenuClick: () -> Unit,
    onLogoClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    unreadNotifications: Int = 3,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("mig_header_bar"),
        color = Color(0xFF222222),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF383838), Color(0xFF1E1E1E), Color(0xFF121212))
                    )
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Menu Drawer Toggle Button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("menu_drawer_button")
            ) {
                Column(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Transparent),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(1.dp))
                        )
                    }
                }
            }

            // Central Glossy mig33 Logo
            Box(
                modifier = Modifier
                    .height(38.dp)
                    .clickable { onLogoClick() }
                    .testTag("mig33_logo_button"),
                contentAlignment = Alignment.Center
            ) {
                // Outer Cyan Cloud Bubble
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF29B6F6), Color(0xFF0288D1), Color(0xFF01579B))
                            )
                        )
                        .border(1.5.dp, Color(0xFF81D4FA), RoundedCornerShape(17.dp))
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "mig",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        // Red/Orange 33 Badge
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFF5722), Color(0xFFD84315))
                                    )
                                )
                                .border(1.dp, Color(0xFFFFCCBC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "33",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Right Action Icons: Notification Bell & Network Activity
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bell with unread badge
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFE0E0E0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (unreadNotifications > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = (-4).dp, y = 6.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(MigBadgeRed)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unreadNotifications.toString(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Wave/Activity Indicator
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFC107))
                        .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(6.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Waves,
                        contentDescription = "Activity",
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MigSubHeaderBar(
    leftText: String = "Home",
    leftAvatarRes: Int? = null,
    centerTitle: String = "migCute",
    rightAvatarRes: Int? = null,
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .testTag("mig_sub_header"),
        color = Color(0xFF4A4A4A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF5E5E5E), Color(0xFF424242), Color(0xFF353535))
                    )
                )
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Quick Flip
            Row(
                modifier = Modifier
                    .clickable { onLeftClick() }
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(12.dp)
                )
                if (leftAvatarRes != null) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Image(
                        painter = painterResource(id = leftAvatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .border(0.5.dp, Color.White, RoundedCornerShape(2.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = leftText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Center Current Stream Title
            Text(
                text = centerTitle,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Right Quick Flip
            Row(
                modifier = Modifier
                    .clickable { onRightClick() }
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (rightAvatarRes != null) {
                    Image(
                        painter = painterResource(id = rightAvatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .border(0.5.dp, Color.White, RoundedCornerShape(2.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
