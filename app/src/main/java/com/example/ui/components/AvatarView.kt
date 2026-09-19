package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AvatarConfig

@Composable
fun AvatarView(
    config: AvatarConfig,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    cornerRadius: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit
) {
    val directImageRes: Int = when (config.presetId) {
        "mig_shadow_tuxedo" -> R.drawable.img_avatar_shadow_tuxedo
        "mig_blood_moon" -> R.drawable.img_avatar_blood_moon
        "mig_kagero_rebel" -> R.drawable.img_avatar_kagero_rebel
        "mig_pierrot_jester" -> R.drawable.img_avatar_pierrot_jester
        "mig_shinya_flame" -> R.drawable.img_avatar_shinya_flame
        "mig_shadow_lord" -> R.drawable.img_avatar_shadow_lord
        "mig_cyber_assassin" -> R.drawable.img_avatar_cyber_assassin
        "mig_holo_noir_couture" -> R.drawable.img_avatar_holo_noir_couture
        "mig_crimson_rose" -> R.drawable.img_avatar_crimson_rose
        "mig_sapphire_empress" -> R.drawable.img_avatar_sapphire_empress
        "mig_dark_angel_punk" -> R.drawable.img_avatar_dark_angel
        "mig_holo_princess" -> R.drawable.img_avatar_holo_princess
        "mig_cyber_techwear" -> R.drawable.img_avatar_cyber_techwear
        "mig_denim_roses" -> R.drawable.img_mig_avatar_denim_roses
        "mig_leopard_chic" -> R.drawable.img_mig_avatar_leopard_chic
        "mig_rocker_star" -> R.drawable.img_mig_avatar_rocker_star
        "mig_pink_coquette" -> R.drawable.img_mig_avatar_pink_coquette
        "mig_street_kitty" -> R.drawable.img_mig_avatar_street_kitty
        "mig_hipst4r_classic" -> R.drawable.avatar_hipst4r
        "mig_b4sejump_retro" -> R.drawable.avatar_b4sejump
        "mig_flyingkit_retro" -> R.drawable.avatar_f1yingkit
        else -> config.customImageRes ?: R.drawable.img_avatar_shadow_tuxedo
    }

    val boxModifier = modifier
        .then(
            if (size != null) Modifier.size(width = size, height = size * 1.42f)
            else Modifier
        )
        .clip(RoundedCornerShape(cornerRadius))
        .then(
            if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            else Modifier
        )
        .background(Color(0xFF000000))

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = directImageRes),
            contentDescription = "Avatar",
            contentScale = contentScale,
            alignment = alignment,
            modifier = Modifier.fillMaxSize()
        )
    }
}
