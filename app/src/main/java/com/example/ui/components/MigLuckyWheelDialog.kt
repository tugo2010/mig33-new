package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelSector(
    val points: Int,
    val color: Color,
    val textColor: Color,
    val weight: Int, // Probability weight (higher = more likely)
    val isJackpot: Boolean = false
)

object LuckyWheelConfig {
    // Points from 10 to 50 with distinct probability weights
    val SECTORS = listOf(
        WheelSector(points = 10, color = Color(0xFF2E7D32), textColor = Color.White, weight = 35), // 35% probability
        WheelSector(points = 15, color = Color(0xFF0288D1), textColor = Color.White, weight = 25), // 25% probability
        WheelSector(points = 20, color = Color(0xFF7B1FA2), textColor = Color.White, weight = 18), // 18% probability
        WheelSector(points = 25, color = Color(0xFFF57C00), textColor = Color.White, weight = 12), // 12% probability
        WheelSector(points = 35, color = Color(0xFFC2185B), textColor = Color.White, weight = 7),  // 7% probability
        WheelSector(points = 50, color = Color(0xFFFFB300), textColor = Color(0xFF3E2723), weight = 3, isJackpot = true) // 3% Jackpot
    )

    fun getRandomSectorIndex(): Int {
        val totalWeight = SECTORS.sumOf { it.weight }
        val rand = Random.nextInt(totalWeight)
        var cumulative = 0
        for (i in SECTORS.indices) {
            cumulative += SECTORS[i].weight
            if (rand < cumulative) {
                return i
            }
        }
        return 0
    }
}

@Composable
fun MigLuckyWheelDialog(
    canSpinToday: Boolean,
    lastSpinPoints: Int,
    onSpinComplete: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPoints by remember { mutableStateOf<Int?>(null) }
    var showWinResult by remember { mutableStateOf(false) }

    val sectors = LuckyWheelConfig.SECTORS
    val sectorAngle = 360f / sectors.size

    Dialog(
        onDismissRequest = {
            if (!isSpinning) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                .shadow(16.dp, RoundedCornerShape(16.dp))
                .testTag("lucky_wheel_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E272C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎡", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Daily Lucky Wheel",
                                color = Color(0xFFFFD54F),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "عجلة الحظ اليومية (10 - 50 رصيد Credits)",
                                color = Color(0xFFB0BEC5),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { if (!isSpinning) onDismiss() },
                        enabled = !isSpinning,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // The Lucky Wheel Canvas & Pointer
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Canvas Wheel
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("lucky_wheel_canvas")
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        rotate(degrees = rotation.value, pivot = center) {
                            // Draw outer golden glowing rim
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFE082), Color(0xFFFF8F00), Color(0xFF3E2723)),
                                    center = center,
                                    radius = radius
                                ),
                                radius = radius,
                                center = center
                            )

                            // Draw each sector
                            val innerRadius = radius - 8.dp.toPx()
                            for (i in sectors.indices) {
                                val startAngle = (i * sectorAngle) - 90f - (sectorAngle / 2f)
                                drawArc(
                                    color = sectors[i].color,
                                    startAngle = startAngle,
                                    sweepAngle = sectorAngle,
                                    useCenter = true,
                                    topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                                    size = Size(innerRadius * 2f, innerRadius * 2f)
                                )

                                // Draw sector dividing lines
                                val angleRad = (startAngle * PI / 180f).toFloat()
                                val lineEndX = center.x + innerRadius * cos(angleRad)
                                val lineEndY = center.y + innerRadius * sin(angleRad)
                                drawLine(
                                    color = Color(0xFFFFFFFF).copy(alpha = 0.6f),
                                    start = center,
                                    end = Offset(lineEndX, lineEndY),
                                    strokeWidth = 2.dp.toPx()
                                )

                                // Draw sector text (points)
                                val textAngle = (i * sectorAngle)
                                val textAngleRad = ((textAngle - 90f) * PI / 180f).toFloat()
                                val textDist = innerRadius * 0.62f
                                val textX = center.x + textDist * cos(textAngleRad)
                                val textY = center.y + textDist * sin(textAngleRad)

                                drawContext.canvas.nativeCanvas.apply {
                                    save()
                                    rotate(textAngle, textX, textY)
                                    val paint = Paint().apply {
                                        color = sectors[i].textColor.toArgb()
                                        textSize = if (sectors[i].isJackpot) 34f else 32f
                                        isFakeBoldText = true
                                        textAlign = Paint.Align.CENTER
                                        typeface = Typeface.DEFAULT_BOLD
                                        setShadowLayer(4f, 1f, 1f, android.graphics.Color.BLACK)
                                    }
                                    val textStr = if (sectors[i].isJackpot) "★ 50 ★" else "${sectors[i].points} Cr"
                                    drawText(textStr, textX, textY + 10f, paint)
                                    restore()
                                }
                            }

                            // Rim lights / golden dots
                            val dotCount = 12
                            for (d in 0 until dotCount) {
                                val dAngle = (d * (360f / dotCount) * PI / 180f).toFloat()
                                val dotDist = radius - 4.dp.toPx()
                                val dotX = center.x + dotDist * cos(dAngle)
                                val dotY = center.y + dotDist * sin(dAngle)
                                drawCircle(
                                    color = if (d % 2 == 0) Color(0xFFFFF9C4) else Color(0xFFFFD54F),
                                    radius = 3.dp.toPx(),
                                    center = Offset(dotX, dotY)
                                )
                            }

                            // Center Hub
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFD54F), Color(0xFFE65100), Color(0xFF263238)),
                                    center = center,
                                    radius = 24.dp.toPx()
                                ),
                                radius = 24.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = center
                            )
                        }
                    }

                    // Top Pointer Needle pointing down at the wheel
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(28.dp, 32.dp)
                            .offset(y = (-6).dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height) // tip pointing down
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        // Pointer Shadow
                        drawPath(
                            path = path,
                            color = Color(0x99000000)
                        )
                        // Pointer Body
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF1744), Color(0xFFD50000), Color(0xFFFFD54F))
                            )
                        )
                        // Pointer Border
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status info or winner banner
                if (showWinResult && wonPoints != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFE65100), Color(0xFFFF8F00), Color(0xFFE65100))
                                )
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎉 CONGRATULATIONS! 🎉",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "You won +$wonPoints Credits! 🪙",
                                color = Color(0xFFFFF9C4),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "تم إضافة الرصيد إلى محفظتك بنجاح",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else if (!canSpinToday) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF263238))
                            .border(1.dp, Color(0xFF455A64), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "✅ You spun the wheel today!",
                                color = Color(0xFF81C784),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Last win: +$lastSpinPoints Credits • Come back tomorrow for next spin!",
                                color = Color(0xFFB0BEC5),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text(
                        text = "⚡ Press Spin below to test your luck! (1 spin daily)",
                        color = Color(0xFFFFD54F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                if (canSpinToday && !showWinResult) {
                    Button(
                        onClick = {
                            if (!isSpinning && canSpinToday) {
                                isSpinning = true
                                wonPoints = null
                                showWinResult = false

                                coroutineScope.launch {
                                    val targetSectorIndex = LuckyWheelConfig.getRandomSectorIndex()
                                    val targetSector = sectors[targetSectorIndex]

                                    // Pointer is at the top (angle 0).
                                    // Sector `targetSectorIndex` is centered at `targetSectorIndex * sectorAngle`.
                                    // To have this sector align with top pointer, the wheel needs to rotate such that:
                                    // angle = (360 - targetSectorIndex * sectorAngle)
                                    val targetSectorOffset = (360f - (targetSectorIndex * sectorAngle)) % 360f
                                    val fullRotations = 5 * 360f // 5 full rounds
                                    val finalRotation = fullRotations + targetSectorOffset

                                    // Spin animation with realistic deceleration
                                    rotation.snapTo(0f)
                                    rotation.animateTo(
                                        targetValue = finalRotation,
                                        animationSpec = tween(
                                            durationMillis = 4200,
                                            easing = CubicBezierEasing(0.15f, 0.85f, 0.25f, 1.0f)
                                        )
                                    )

                                    // Spin complete
                                    isSpinning = false
                                    wonPoints = targetSector.points
                                    showWinResult = true
                                    onSpinComplete(targetSector.points)
                                }
                            }
                        },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("spin_wheel_action_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300),
                            disabledContainerColor = Color(0xFF546E7A)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSpinning) "SPINNING... 🎡" else "SPIN NOW! (دور الآن)",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("close_wheel_dialog_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37474F)
                        )
                    ) {
                        Text(
                            text = if (showWinResult) "Awesome! (رائع)" else "Close (إغلاق)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
