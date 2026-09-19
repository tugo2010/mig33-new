package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.EmoticonItem
import com.example.model.VirtualGift
import com.example.ui.theme.*

@Composable
fun MigRoomContextMenu(
    onDismiss: () -> Unit,
    onPlayGames: () -> Unit = {},
    onInsertEmoticon: () -> Unit,
    onSendGift: () -> Unit,
    onKick: () -> Unit,
    onParticipants: () -> Unit,
    onRoomInfo: () -> Unit,
    onAddToFavorites: () -> Unit,
    onGroups: () -> Unit,
    onReportAbuse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF616161), RoundedCornerShape(8.dp))
            .testTag("mig_room_context_menu"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cute mig creatures banner on top of the menu
            Image(
                painter = painterResource(id = R.drawable.mig_creatures),
                contentDescription = "mig creatures",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            // Menu Items matching Screenshot 5 + Room Games Bot
            RoomMenuOption(icon = Icons.Default.SportsEsports, title = "🎮 Play Games (ألعاب)", onClick = { onPlayGames(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.Mood, title = "Insert Emoticon", onClick = { onInsertEmoticon(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.CardGiftcard, title = "Send Gift", onClick = { onSendGift(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.FlashOn, title = "Kick", onClick = { onKick(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.PeopleAlt, title = "Participants", onClick = { onParticipants(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.Info, title = "Room Info", onClick = { onRoomInfo(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.FavoriteBorder, title = "Add to Favorites", onClick = { onAddToFavorites(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.Groups, title = "Groups", onClick = { onGroups(); onDismiss() })
            RoomMenuOption(icon = Icons.Default.Warning, title = "Report Abuse", onClick = { onReportAbuse(); onDismiss() })
        }
    }
}

@Composable
private fun RoomMenuOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        HorizontalDivider(color = Color(0xFF424242), thickness = 0.5.dp)
    }
}

@Composable
fun MigEmoticonPickerDialog(
    emoticons: List<EmoticonItem>,
    onSelect: (EmoticonItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF0288D1), RoundedCornerShape(8.dp))
                .testTag("emoticon_picker_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "mig33 Emoticons",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = Color(0xFF455A64), modifier = Modifier.padding(vertical = 6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(emoticons) { emo ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF37474F))
                                .border(1.dp, Color(0xFF546E7A), RoundedCornerShape(6.dp))
                                .clickable {
                                    onSelect(emo)
                                    onDismiss()
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = emo.symbol, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = emo.code,
                                    color = Color(0xFFFFD54F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MigSendGiftDialog(
    gifts: List<VirtualGift>,
    userCredits: Int,
    recipientName: String,
    onSendGift: (VirtualGift) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGift by remember { mutableStateOf(gifts.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.5.dp, Color(0xFFFF9800), RoundedCornerShape(8.dp))
                .testTag("send_gift_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁 Send Virtual Gift", color = Color(0xFFFFD54F), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Sending to: $recipientName",
                    color = Color(0xFF81D4FA),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF333333))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Your Balance:", color = Color(0xFFCCCCCC), fontSize = 12.sp)
                    Text(text = "$userCredits Credits", color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gifts) { gift ->
                        val isSelected = selectedGift?.id == gift.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF5D4037) else Color(0xFF333333))
                                .border(
                                    1.5.dp,
                                    if (isSelected) Color(0xFFFF9800) else Color(0xFF424242),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedGift = gift }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = gift.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = gift.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${gift.priceCredits} cr",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Button
                val canAfford = selectedGift != null && userCredits >= (selectedGift?.priceCredits ?: 0)
                Button(
                    onClick = {
                        selectedGift?.let {
                            onSendGift(it)
                            onDismiss()
                        }
                    },
                    enabled = canAfford,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("confirm_send_gift_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6F00),
                        disabledContainerColor = Color(0xFF616161)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (canAfford) "Send Gift (${selectedGift?.priceCredits ?: 0} cr)" else "Insufficient Credits",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MigRoomBottomBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickYellowClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onKeyboardToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mig_room_bottom_bar"),
        color = Color(0xFF222222),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF383838), Color(0xFF1E1E1E))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Yellow mig logo icon button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                            )
                        )
                        .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(6.dp))
                        .clickable { onQuickYellowClick() }
                        .testTag("yellow_mig_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF212121))
                            .border(2.dp, Color(0xFFFFD54F), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Home Icon
                IconButton(onClick = onHomeClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
                }

                // Profile / Avatar Icon
                IconButton(onClick = onProfileClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
                }

                // Settings Gear Icon
                IconButton(onClick = onSettingsClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Text input field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = messageText,
                        onValueChange = onMessageChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_input_field")
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button with classic orange/red gradient
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF9800), Color(0xFFE65100))
                            )
                        )
                        .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(4.dp))
                        .clickable { onSendMessage() }
                        .padding(horizontal = 12.dp)
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Send",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
