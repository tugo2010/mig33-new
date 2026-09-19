package com.example.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.model.MigLevelCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun MigBadgeText(
    text: String,
    fontSize: TextUnit,
    color: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Black
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            textAlign = TextAlign.Center
        ),
        maxLines = 1,
        softWrap = false
    )
}

/**
 * Cache for loaded level badge bitmaps to ensure instant rendering across all 150 levels.
 */
object MigLevelImageCache {
    private val memoryCache = mutableMapOf<Int, ImageBitmap?>()

    fun get(level: Int): ImageBitmap? = memoryCache[level]

    fun put(level: Int, bitmap: ImageBitmap?) {
        if (bitmap != null) {
            memoryCache[level] = bitmap
        }
    }

    fun hasKey(level: Int): Boolean = memoryCache[level] != null

    fun clear() {
        memoryCache.clear()
    }
}

/**
 * MigLevelBadgeView:
 * Renders the official 150 mig33 level badge image (`mig_levels/1.png` to `mig_levels/150.png`).
 * Supports loading from:
 * 1) Android assets (`assets/mig_levels/$level.png`)
 * 2) Public web/assets directory (`/app/applet/public/mig_levels/$level.png`)
 * 3) Root directory (`/app/applet/mig_levels/$level.png`)
 *
 * If the image is not yet on the filesystem, it falls back to an authentic mig33
 * level badge graphic with tier coloring, rank icon and level number.
 */
@Composable
fun MigLevelBadgeView(
    level: Int,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    isOwned: Boolean = true,
    showLevelFallbackText: Boolean = true
) {
    val context = LocalContext.current
    var bitmap by remember(level) { mutableStateOf(MigLevelImageCache.get(level)) }

    LaunchedEffect(level) {
        if (!MigLevelImageCache.hasKey(level)) {
            withContext(Dispatchers.IO) {
                var loaded: ImageBitmap? = null
                // 1. Check Android assets
                try {
                    context.assets.open("mig_levels/$level.png").use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        if (bmp != null) {
                            loaded = bmp.asImageBitmap()
                        }
                    }
                } catch (_: Exception) {
                    // 2. Check local directories
                    val candidates = listOf(
                        File("app/src/main/assets/mig_levels/$level.png"),
                        File("/app/applet/app/src/main/assets/mig_levels/$level.png"),
                        File("assets/mig_levels/$level.png"),
                        File("/app/applet/assets/mig_levels/$level.png"),
                        File("/app/applet/public/mig_levels/$level.png"),
                        File("/app/applet/mig_levels/$level.png"),
                        File(context.filesDir, "mig_levels/$level.png")
                    )
                    for (file in candidates) {
                        if (file.exists() && file.canRead()) {
                            try {
                                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                                if (bmp != null) {
                                    loaded = bmp.asImageBitmap()
                                    break
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }
                MigLevelImageCache.put(level, loaded)
                bitmap = loaded
            }
        } else {
            bitmap = MigLevelImageCache.get(level)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = "migLevel $level Badge (شارة المستوى $level)",
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(size)
                .alpha(if (isOwned) 1f else 0.4f)
        )
    } else {
        // Fallback: Full mig33 Badge Art with emblem, frame, and level banner (not just a single number)
        val tierColors = when {
            level >= 100 -> listOf(Color(0xFFFFD54F), Color(0xFFFF6F00), Color(0xFFB71C1C))
            level >= 50  -> listOf(Color(0xFFBA68C8), Color(0xFF7B1FA2), Color(0xFF4A148C))
            level >= 24  -> listOf(Color(0xFFFFB74D), Color(0xFFF57C00), Color(0xFFE65100))
            level >= 10  -> listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF01579B))
            else         -> listOf(Color(0xFF81C784), Color(0xFF388E3C), Color(0xFF1B5E20))
        }
        val rankIcon = MigLevelCalculator.getRankIcon(level)

        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.linearGradient(tierColors))
                .border(1.5.dp, Color(0xFFFFD54F), CircleShape)
                .alpha(if (isOwned) 1f else 0.45f),
            contentAlignment = Alignment.Center
        ) {
            if (size >= 36.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Emblem / Icon (e.g. Robot 🤖, Crown 👑, Star ⭐)
                    MigBadgeText(
                        text = rankIcon,
                        fontSize = (size.value * 0.40f).sp
                    )
                    // Golden level tag ribbon
                    Box(
                        modifier = Modifier
                            .background(Color(0xCC000000), RoundedCornerShape(3.dp))
                            .padding(horizontal = 3.dp, vertical = 0.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MigBadgeText(
                            text = "L$level",
                            color = Color(0xFFFFD54F),
                            fontSize = (size.value * 0.22f).sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            } else {
                MigBadgeText(
                    text = rankIcon,
                    fontSize = (size.value * 0.55f).sp
                )
            }
        }
    }
}

/**
 * Interactive Level Badge Chip used in Profile headers, drawer, and chat message headers.
 */
@Composable
fun MigLevelChip(
    level: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRankTitle: Boolean = false,
    useClassicStyle: Boolean = true
) {
    val rankTitle = remember(level) { MigLevelCalculator.getRankTitle(level) }

    if (useClassicStyle) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable { onClick() }
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = "migLevel $level",
                color = Color(0xFF333333),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            MigLevelBadgeView(
                level = level,
                size = 18.dp,
                isOwned = true
            )
            if (showRankTitle) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "• $rankTitle",
                    color = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFFF3E0))
                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(6.dp))
                .clickable { onClick() }
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            MigLevelBadgeView(
                level = level,
                size = 20.dp,
                isOwned = true
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "migLevel $level",
                color = Color(0xFFE65100),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            if (showRankTitle) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "• $rankTitle",
                    color = Color(0xFF795548),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}

/**
 * MigSpecialBadgeView:
 * Renders high-resolution vector/circular badge designs for special badges #001 to #100.
 * Recreates the authentic mig33 badge graphics for Admin, Mods, Security, Devs, Verification badges, etc.
 */
@Composable
fun MigSpecialBadgeView(
    badgeNumber: String?,
    icon: String,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    isOwned: Boolean = true
) {
    val alphaVal = if (isOwned) 1f else 0.45f
    when (badgeNumber) {
        "001" -> { // Admin: Orange circle + White "A"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6D00))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("A", fontSize = (size.value * 0.55f).sp)
            }
        }
        "002" -> { // Super Mod: Yellow ring, Purple circle + White "SM"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF4A148C))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("SM", fontSize = (size.value * 0.42f).sp)
            }
        }
        "003" -> { // Moderator: Purple circle + White "M"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF8E24AA))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("M", fontSize = (size.value * 0.55f).sp)
            }
        }
        "004" -> { // Security: Navy Blue circle + White "S"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF1A237E))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("S", fontSize = (size.value * 0.55f).sp)
            }
        }
        "005" -> { // Bot Master: Slate ring + Dark Slate circle + White "BM"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF78909C))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF37474F))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("BM", fontSize = (size.value * 0.42f).sp)
            }
        }
        "006" -> { // System Bot: Slate circle + White "B"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF455A64))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("B", fontSize = (size.value * 0.55f).sp)
            }
        }
        "007" -> { // Official Staff: White ring + Dark Charcoal circle + White "ST"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF212121))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("ST", fontSize = (size.value * 0.42f).sp)
            }
        }
        "008" -> { // Auditor: Deep Teal circle + White "AU"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF00695C))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("AU", fontSize = (size.value * 0.42f).sp)
            }
        }
        "009" -> { // Lead Dev: Cyan ring + Royal Blue circle + White "DEV"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF00E5FF))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("DEV", fontSize = (size.value * 0.35f).sp)
            }
        }
        "010" -> { // Support Care: Teal circle + White "CS"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF00796B))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("CS", fontSize = (size.value * 0.42f).sp)
            }
        }
        "011" -> { // Verified Blue: Bright Blue circle + White Checkmark
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF1E88E5))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("✓", fontSize = (size.value * 0.60f).sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        "012" -> { // Verified Gold: Gold circle + White Checkmark
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFB300))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("✓", fontSize = (size.value * 0.60f).sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        "013" -> { // Verified Green: Green circle + White Checkmark
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("✓", fontSize = (size.value * 0.60f).sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        "014" -> { // Celebrity Star: Magenta circle + White Star ★
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFD81B60))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("★", fontSize = (size.value * 0.60f).sp, fontWeight = FontWeight.Bold)
            }
        }
        "015" -> { // Protected ID: White ring + Blue-grey circle + White "ID"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCFD8DC), CircleShape)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF607D8B))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("ID", fontSize = (size.value * 0.42f).sp)
            }
        }
        "016" -> { // Merchant: Bright Blue circle + White "M"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF0288D1))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("M", fontSize = (size.value * 0.55f).sp)
            }
        }
        "017" -> { // Master Merch: White ring + Orange circle + White "$"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFFF57C00))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("$", fontSize = (size.value * 0.55f).sp)
            }
        }
        "018" -> { // Global Reseller: Yellow ring + Blue circle + White "GR"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF0277BD))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("GR", fontSize = (size.value * 0.42f).sp)
            }
        }
        "019" -> { // Top Merchant: Orange circle + White "#1"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFE65100))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("#1", fontSize = (size.value * 0.42f).sp)
            }
        }
        "020" -> { // Credit Dealer: Dark Cyan circle + White "CR"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF00838F))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("CR", fontSize = (size.value * 0.42f).sp)
            }
        }
        "021" -> { // Fast Pay: Orange circle + White Lightning ⚡
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6F00))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("⚡", fontSize = (size.value * 0.52f).sp)
            }
        }
        "022" -> { // Official Bank: Dark Slate Grey circle + White "BK"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF37474F))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("BK", fontSize = (size.value * 0.42f).sp)
            }
        }
        "023" -> { // Escrow Agent: Dark Brown circle + White "EA"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF4E342E))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("EA", fontSize = (size.value * 0.42f).sp)
            }
        }
        "024" -> { // Shop Owner: Purple circle + White "SO"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF512DA8))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("SO", fontSize = (size.value * 0.42f).sp)
            }
        }
        "025" -> { // Prime Seller: Yellow ring + Red circle + White "P$"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("P$", fontSize = (size.value * 0.38f).sp)
            }
        }
        "026" -> { // Bronze VIP: Brown circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF8D6E63))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "027" -> { // Silver VIP: Silver Grey circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF9E9E9E))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "028" -> { // Gold VIP: Gold circle + Black "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC107))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", color = Color.Black, fontSize = (size.value * 0.36f).sp)
            }
        }
        "029" -> { // Platinum VIP: White ring + Dark Slate Blue circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF546E7A))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "030" -> { // Ruby VVIP: Yellow ring + Red circle + White "VVIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VVIP", fontSize = (size.value * 0.30f).sp)
            }
        }
        "031" -> { // Sapphire VIP: White ring + Royal Blue circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "032" -> { // Emerald VIP: Yellow ring + Forest Green circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "033" -> { // Diamond VIP: White ring + Bright Cyan circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF00ACC1))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "034" -> { // Black Elite: Yellow ring + Black circle + White "VIP"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF212121))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("VIP", fontSize = (size.value * 0.36f).sp)
            }
        }
        "035" -> { // VIP Supreme: Yellow ring + Plum Purple circle + White "V+"
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD600))
                    .padding(size * 0.08f)
                    .clip(CircleShape)
                    .background(Color(0xFF6A1B9A))
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("V+", fontSize = (size.value * 0.42f).sp)
            }
        }
        "036" -> { // Official Room Host
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF5722)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1A237E)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏠", fontSize = (size.value * 0.50f).sp)
            }
        }
        "037" -> { // Chatroom Co-Host
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF00BCD4)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF004D40)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🤝", fontSize = (size.value * 0.50f).sp)
            }
        }
        "038" -> { // Room Enforcer (Kicker)
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD600)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFB71C1C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = (size.value * 0.52f).sp)
            }
        }
        "039" -> { // migRadio Live DJ
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFAB47BC)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎧", fontSize = (size.value * 0.50f).sp)
            }
        }
        "040" -> { // Room Security Guard
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFB0BEC5)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF263238)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "041" -> { // Legendary Room Master
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A0007)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏰", fontSize = (size.value * 0.50f).sp)
            }
        }
        "042" -> { // VIP Room Host
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFC107)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF311B92)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🌟", fontSize = (size.value * 0.50f).sp)
            }
        }
        "043" -> { // Voice & Talent Singer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFEC407A)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF880E4F)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎤", fontSize = (size.value * 0.50f).sp)
            }
        }
        "044" -> { // Official Event Judge
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF8D6E63)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF3E2723)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⚖️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "045" -> { // Room Veteran Regular
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFB300)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF5D4037)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎖️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "046" -> { // Room VIP Star
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD600)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFC62828)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐", fontSize = (size.value * 0.52f).sp)
            }
        }
        "047" -> { // Chatroom Sovereign
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏰", fontSize = (size.value * 0.50f).sp)
            }
        }
        "048" -> { // Danger1 Quiz Master
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFEA00)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF0D47A1)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("❓", fontSize = (size.value * 0.50f).sp)
            }
        }
        "049" -> { // Bot Fleet Commander
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF29B6F6)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF37474F)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⚙️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "050" -> { // mig33 Legacy Member
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1B5E20)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("33", color = Color(0xFFFFD700), fontSize = (size.value * 0.45f).sp)
            }
        }
        "051" -> { // Golden Voice Performer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD600)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFE65100)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎤", fontSize = (size.value * 0.50f).sp)
            }
        }
        "052" -> { // Official Tournament Host
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF9800)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFBF360C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎟️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "053" -> { // Grand Benefactor
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color.White).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF2E7D32)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💰", fontSize = (size.value * 0.50f).sp)
            }
        }
        "054" -> { // migWorld Guild Leader
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFCFD8DC)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1A237E)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "055" -> { // Imperial Royal Shield
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF0D47A1)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🔱", fontSize = (size.value * 0.50f).sp)
            }
        }
        "056" -> { // Bronze Crown of Honor
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFCD7F32)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF3E2723)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🪖", fontSize = (size.value * 0.50f).sp)
            }
        }
        "057" -> { // Silver Royal Crown
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFE0E0E0)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF455A64)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🥈", fontSize = (size.value * 0.50f).sp)
            }
        }
        "058" -> { // Golden Imperial Crown
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFFFB300)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("👑", fontSize = (size.value * 0.50f).sp)
            }
        }
        "059" -> { // Platinum Emperor Crown
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color.White).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF00838F)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = (size.value * 0.50f).sp)
            }
        }
        "060" -> { // Supreme Imperial Crown
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⚜️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "061" -> { // Crystal Star Gem
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF80DEEA)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF311B92)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🔮", fontSize = (size.value * 0.50f).sp)
            }
        }
        "062" -> { // Cyan Blue Diamond
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF00E5FF)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF01579B)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = (size.value * 0.50f).sp)
            }
        }
        "063" -> { // Deep Ocean Diamond
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF0288D1)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF002171)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🌊", fontSize = (size.value * 0.50f).sp)
            }
        }
        "064" -> { // Rare Pink Diamond
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFF48FB1)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFAD1457)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🌸", fontSize = (size.value * 0.50f).sp)
            }
        }
        "065" -> { // Mythic Black Diamond
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF121212)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💎", fontSize = (size.value * 0.50f).sp)
            }
        }
        "066" -> { // Gift Star
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFEA00)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFF57C00)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐", fontSize = (size.value * 0.52f).sp)
            }
        }
        "067" -> { // Gift Hero
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF9800)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1565C0)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🦸", fontSize = (size.value * 0.50f).sp)
            }
        }
        "068" -> { // Gift King of the Room
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF6A1B9A)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("👑", fontSize = (size.value * 0.50f).sp)
            }
        }
        "069" -> { // Gift Angel
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFF8BBD0)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF0288D1)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("👼", fontSize = (size.value * 0.50f).sp)
            }
        }
        "070" -> { // Mega Gifter Titan
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFB71C1C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎁", fontSize = (size.value * 0.50f).sp)
            }
        }
        "071" -> { // Bronze Gamer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFCD7F32)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4E342E)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🥉", fontSize = (size.value * 0.50f).sp)
            }
        }
        "072" -> { // Silver Gamer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFE0E0E0)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF37474F)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🥈", fontSize = (size.value * 0.50f).sp)
            }
        }
        "073" -> { // Gold Gamer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFE65100)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🥇", fontSize = (size.value * 0.50f).sp)
            }
        }
        "074" -> { // Arcade Master Pro
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF00E676)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF121212)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🕹️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "075" -> { // Danger 1 Trivia Showdown Ace
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFEA00)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF651FFF)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = (size.value * 0.52f).sp)
            }
        }
        "076" -> { // Dice & High Roller Champion
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF1744)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1B5E20)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎲", fontSize = (size.value * 0.50f).sp)
            }
        }
        "077" -> { // migPoker Card Shark
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color.White).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF2E7D32)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText("♠️", color = Color.White, fontSize = (size.value * 0.52f).sp)
            }
        }
        "078" -> { // Chess & Strategy Master
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color.White).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF212121)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("♟️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "079" -> { // Casino High Roller
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFC62828)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎰", fontSize = (size.value * 0.50f).sp)
            }
        }
        "080" -> { // Global migGaming Champion
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1A237E)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = (size.value * 0.50f).sp)
            }
        }
        "081" -> { // Top Fan Heart
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFF8BBD0)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFC2185B)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💖", fontSize = (size.value * 0.50f).sp)
            }
        }
        "082" -> { // True Love Couple
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF80AB)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF880E4F)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💕", fontSize = (size.value * 0.50f).sp)
            }
        }
        "083" -> { // Romantic Soul
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFD50000)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFB71C1C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🌹", fontSize = (size.value * 0.50f).sp)
            }
        }
        "084" -> { // Heart Collector
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFE040FB)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💝", fontSize = (size.value * 0.50f).sp)
            }
        }
        "085" -> { // Mini-Blog Famous Icon
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF6D00)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFDD2C00)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🔥", fontSize = (size.value * 0.50f).sp)
            }
        }
        "086" -> { // Charm & Charisma Master
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = (size.value * 0.50f).sp)
            }
        }
        "087" -> { // Top Profile Footprints
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF76FF03)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF004D40)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("👣", fontSize = (size.value * 0.50f).sp)
            }
        }
        "088" -> { // 30-Day Active Streak
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF40C4FF)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF0D47A1)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("📅", fontSize = (size.value * 0.50f).sp)
            }
        }
        "089" -> { // 2005-2006 J2ME Pioneer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF1B5E20)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("📟", fontSize = (size.value * 0.50f).sp)
            }
        }
        "090" -> { // 10-Year mig33 Loyalty Veteran
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF263238)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "091" -> { // Official Content Creator
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFE040FB)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF311B92)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎨", fontSize = (size.value * 0.50f).sp)
            }
        }
        "092" -> { // UI & Theme Artist
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFF18FFFF)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF006064)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🖌️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "093" -> { // Emoticon Pack Designer
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD600)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFFFF6F00)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("😊", fontSize = (size.value * 0.50f).sp)
            }
        }
        "094" -> { // Avatar Fashion Stylist
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF80AB)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF512DA8)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("👗", fontSize = (size.value * 0.50f).sp)
            }
        }
        "095" -> { // Official migRadio Broadcaster
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFF1744)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF212121)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎙️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "096" -> { // migPoet & Literature Author
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A0007)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("📜", fontSize = (size.value * 0.50f).sp)
            }
        }
        "097" -> { // Global Community Ambassador
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF0D47A1)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🎖️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "098" -> { // mig33 Hall of Fame Immortal
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF000000)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛️", fontSize = (size.value * 0.50f).sp)
            }
        }
        "099" -> { // Community Star of the Year
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFEA00)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF311B92)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("🌟", fontSize = (size.value * 0.50f).sp)
            }
        }
        "100" -> { // Absolute mig33 Living Legend
            Box(
                modifier = modifier.size(size).clip(CircleShape).background(Color(0xFFFFD700)).padding(size * 0.08f).clip(CircleShape).background(Color(0xFF4A148C)).alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                Text("💫", fontSize = (size.value * 0.52f).sp)
            }
        }
        else -> { // Fallback to icon
            Box(
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(if (isOwned) Color(0xFFFFF8E1) else Color(0xFFE0E0E0))
                    .border(1.5.dp, if (isOwned) Color(0xFFFFB300) else Color(0xFFBDBDBD), CircleShape)
                    .alpha(alphaVal),
                contentAlignment = Alignment.Center
            ) {
                MigBadgeText(text = icon, fontSize = (size.value * 0.52f).sp)
            }
        }
    }
}
