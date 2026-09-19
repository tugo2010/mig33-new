package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MigRetroKeyboard(
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onEmoticonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCaps by remember { mutableStateOf(false) }
    var isSymbols by remember { mutableStateOf(false) }

    val row1Letters = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    val row2Letters = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    val row3Letters = listOf("Z", "X", "C", "V", "B", "N", "M")

    val row1Symbols = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row2Symbols = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")")
    val row3Symbols = listOf("*", "\"", "'", ":", ";", "!", "?")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mig_retro_keyboard"),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A2A2A), Color(0xFF181818))
                    )
                )
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val items = if (isSymbols) row1Symbols else row1Letters
                items.forEachIndexed { index, char ->
                    val displayChar = if (isCaps && !isSymbols) char else if (!isSymbols) char.lowercase() else char
                    KeyboardKey(
                        text = displayChar,
                        subText = if (!isSymbols) "${(index + 1) % 10}" else null,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPress(displayChar) }
                    )
                }
            }

            // Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val items = if (isSymbols) row2Symbols else row2Letters
                items.forEach { char ->
                    val displayChar = if (isCaps && !isSymbols) char else if (!isSymbols) char.lowercase() else char
                    KeyboardKey(
                        text = displayChar,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPress(displayChar) }
                    )
                }
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shift / Caps Key
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCaps) Color(0xFF555555) else Color(0xFF383838))
                        .border(1.dp, Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
                        .clickable { isCaps = !isCaps },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Shift",
                        tint = if (isCaps) Color(0xFF81D4FA) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                val items = if (isSymbols) row3Symbols else row3Letters
                items.forEach { char ->
                    val displayChar = if (isCaps && !isSymbols) char else if (!isSymbols) char.lowercase() else char
                    KeyboardKey(
                        text = displayChar,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPress(displayChar) }
                    )
                }

                // Backspace Key
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF383838))
                        .border(1.dp, Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
                        .clickable { onBackspace() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Row 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ?123 Mode Toggle
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSymbols) Color(0xFF555555) else Color(0xFF383838))
                        .border(1.dp, Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
                        .clickable { isSymbols = !isSymbols },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSymbols) "ABC" else "?123",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comma
                KeyboardKey(
                    text = ",",
                    modifier = Modifier.weight(0.9f),
                    onClick = { onKeyPress(",") }
                )

                // Spacebar
                Box(
                    modifier = Modifier
                        .weight(3.8f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF454545))
                        .border(1.dp, Color(0xFF555555), RoundedCornerShape(4.dp))
                        .clickable { onKeyPress(" ") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "mig33", color = Color(0xFF888888), fontSize = 11.sp)
                }

                // Period
                KeyboardKey(
                    text = ".",
                    modifier = Modifier.weight(0.9f),
                    onClick = { onKeyPress(".") }
                )

                // Emoticon Key :-)
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF383838))
                        .border(1.dp, Color(0xFF4A4A4A), RoundedCornerShape(4.dp))
                        .clickable { onEmoticonClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ":-)",
                        color = Color(0xFFFFD54F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardKey(
    text: String,
    subText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF525252), Color(0xFF3E3E3E))
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
            if (subText != null) {
                Text(
                    text = subText,
                    color = Color(0xFFAAAAAA),
                    fontSize = 8.sp,
                    lineHeight = 9.sp
                )
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
