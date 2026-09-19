package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MigRepository
import com.example.model.*
import com.example.ui.components.MigHeaderBar
import com.example.ui.components.MigLuckyWheelDialog
import com.example.ui.components.MigSendGiftDialog
import java.util.*

@Composable
fun GiftsShopScreen(
    repository: MigRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenAvatarStudio: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userProfile by repository.userProfile.collectAsState()
    val canSpinToday by repository.canSpinDaily.collectAsState()
    val lastSpinPoints by repository.lastSpinPoints.collectAsState()

    var selectedCategory by remember { mutableStateOf(StoreCategory.EMOTICONS) }
    var searchQuery by remember { mutableStateOf("") }
    var priceSortOrder by remember { mutableStateOf(0) } // 0: Default, 1: Low to High, 2: High to Low
    var showLuckyWheelDialog by remember { mutableStateOf(false) }

    // Dialog state
    var itemToPurchase by remember { mutableStateOf<MigStoreItem?>(null) }
    var selectedGiftForSend by remember { mutableStateOf<VirtualGift?>(null) }
    var itemDetailDialog by remember { mutableStateOf<MigStoreItem?>(null) }
    var selectedEmoticonPackForPreview by remember { mutableStateOf<MigStoreItem?>(null) }

    // State for user owned store items
    var ownedItemIds by remember {
        mutableStateOf(
            mutableSetOf(
                "emo_animal", "emo_classic", "ava_shadow", "addon_silent_7d", "addon_mediashare_7d"
            )
        )
    }

    LaunchedEffect(Unit) {
        repository.refreshDailySpinState()
    }

    // Comprehensive catalog matching all screenshot categories
    val allStoreItems = remember {
        listOf(
            // 1. EMOTICONS (😊 Emoticons - Classic mig33 Packs)
            MigStoreItem("emo_classic", "Classic mig33 Original", StoreCategory.EMOTICONS, "50 classic original mig33 smileys and shortcuts.", 0, status = StoreItemStatus.OWNED, emoji = "💬", tagText = "Tap to equip"),
            MigStoreItem("emo_animal", "Animal Kingdom Pack", StoreCategory.EMOTICONS, "75 cute animal expressions and pet icons.", 0, status = StoreItemStatus.OWNED, emoji = "🐾", tagText = "Tap to equip"),
            MigStoreItem("emo_ace", "Ace Pack", StoreCategory.EMOTICONS, "24 card tricks, lucky spades and poker animations.", 100, emoji = "🃏", tagText = "Emoticons"),
            MigStoreItem("emo_adro", "Adro Robot Pack", StoreCategory.EMOTICONS, "20 classic mig33 green alien robot emoticons.", 75, emoji = "👾", tagText = "Emoticons"),
            MigStoreItem("emo_alpha", "Alphabet 3D Pack", StoreCategory.EMOTICONS, "36 glowing 3D letters and numbers shortcuts.", 100, emoji = "🔤", tagText = "Emoticons"),
            MigStoreItem("emo_alter", "Alternate Moods Pack", StoreCategory.EMOTICONS, "21 alternative retro pixel expressions.", 100, emoji = "🎭", tagText = "Emoticons"),
            MigStoreItem("emo_anime", "Anime Chibi Pack", StoreCategory.EMOTICONS, "16 chibi anime emotions, tears and sparkles.", 75, emoji = "✨", tagText = "Emoticons"),
            MigStoreItem("emo_anime2", "Anime 2 Sparkles Pack", StoreCategory.EMOTICONS, "15 blushing, shocked and cute anime reactions.", 75, emoji = "🌸", tagText = "Emoticons"),
            MigStoreItem("emo_big", "Big Animated Faces Pack", StoreCategory.EMOTICONS, "20 giant wide-mouth animated smileys.", 75, emoji = "😄", tagText = "Emoticons"),
            MigStoreItem("emo_devil", "Devil & Angel Pack", StoreCategory.EMOTICONS, "22 mischievous red devils and holy angels.", 90, emoji = "😇", tagText = "Emoticons"),
            MigStoreItem("emo_love", "Love & Romance Pack", StoreCategory.EMOTICONS, "28 sweet kisses, blooming roses and Cupid hearts.", 85, emoji = "💖", tagText = "Emoticons"),
            MigStoreItem("emo_gangsta", "Gangsta & Hip-Hop Pack", StoreCategory.EMOTICONS, "24 street caps, dark shades, boombox and swag.", 95, emoji = "🕶️", tagText = "Emoticons"),
            MigStoreItem("emo_sports", "Football & Sports Pack", StoreCategory.EMOTICONS, "25 soccer balls, referee cards and victory cups.", 80, emoji = "⚽", tagText = "Emoticons"),
            MigStoreItem("emo_horror", "Horror & Halloween Pack", StoreCategory.EMOTICONS, "20 spooky skulls, ghosts, pumpkins and vampires.", 85, emoji = "💀", tagText = "Emoticons"),
            MigStoreItem("emo_party", "Food & Party Fiesta Pack", StoreCategory.EMOTICONS, "30 pizza slices, birthday cheers and disco balls.", 75, emoji = "🍕", tagText = "Emoticons"),
            MigStoreItem("emo_meme", "Troll & Rage Meme Pack", StoreCategory.EMOTICONS, "22 classic internet rage faces and troll laughs.", 90, emoji = "🤪", tagText = "Emoticons"),
            MigStoreItem("emo_neon", "Cyber Neon Glow Pack", StoreCategory.EMOTICONS, "30 animated cyber neon reaction icons.", 120, emoji = "⚡", tagText = "Emoticons"),
            MigStoreItem("emo_festive", "Festive & Eid Lights Pack", StoreCategory.EMOTICONS, "24 crescent moons, lanterns and fireworks.", 80, emoji = "🌙", tagText = "Emoticons"),
            MigStoreItem("emo_arrow", "Arrow & Signs Pack", StoreCategory.EMOTICONS, "18 glowing direction arrows and pointers.", 75, emoji = "🔄", tagText = "Emoticons"),
            MigStoreItem("emo_zodiac", "Zodiac & Astrology Pack", StoreCategory.EMOTICONS, "12 glowing constellation horoscope signs.", 100, emoji = "♈", tagText = "Emoticons"),

            // 2. AVATAR (👤 Avatar)
            MigStoreItem("ava_shadow", "Shadow Tuxedo Set", StoreCategory.AVATAR, "Complete dark tuxedo with purple smoke aura.", 0, status = StoreItemStatus.OWNED, imageRes = R.drawable.img_avatar_shadow_tuxedo, tagText = "Equipped", avatarPresetId = "mig_shadow_tuxedo"),
            MigStoreItem("ava_blood", "Blood Moon Assassin", StoreCategory.AVATAR, "Crimson cape & glowing dark katana.", 300, imageRes = R.drawable.img_avatar_blood_moon, tagText = "Avatar", avatarPresetId = "mig_blood_moon"),
            MigStoreItem("ava_kagero", "Kagero Street Rebel", StoreCategory.AVATAR, "Cyberpunk streetwear & holographic visor.", 250, imageRes = R.drawable.img_avatar_kagero_rebel, tagText = "Avatar", avatarPresetId = "mig_kagero_rebel"),
            MigStoreItem("ava_pierrot", "Pierrot Dark Jester", StoreCategory.AVATAR, "Gothic jester with enchanted cards.", 200, imageRes = R.drawable.img_avatar_pierrot_jester, tagText = "Avatar", avatarPresetId = "mig_pierrot_jester"),
            MigStoreItem("ava_holo", "Holo Princess Dress", StoreCategory.AVATAR, "Cyber neon royal dress & tiara.", 280, imageRes = R.drawable.img_avatar_holo_princess, tagText = "Avatar", avatarPresetId = "mig_holo_princess"),
            MigStoreItem("ava_angel", "Dark Angel Punk", StoreCategory.AVATAR, "Feathered dark wings & leather boots.", 350, imageRes = R.drawable.img_avatar_dark_angel, tagText = "Avatar", avatarPresetId = "mig_dark_angel_punk"),
            MigStoreItem("ava_noir", "Holo Noir Couture", StoreCategory.AVATAR, "Haute couture cyber anime style.", 280, imageRes = R.drawable.img_avatar_holo_noir_couture, tagText = "Avatar", avatarPresetId = "mig_holo_noir_couture"),
            MigStoreItem("ava_hipst4r", "Classic Hipst4r", StoreCategory.AVATAR, "Original 2008 mig33 hipster boy.", 150, imageRes = R.drawable.avatar_hipst4r, tagText = "Avatar", avatarPresetId = "mig_hipst4r_classic"),

            // 3. PREMIUM GIFTS (🏅 Premium Gifts)
            MigStoreItem("pgift_crown", "Golden Crown", StoreCategory.PREMIUM_GIFTS, "Command gift item. Code: golden crown", 200, emoji = "👑", commandCode = "golden crown", tagText = "Command"),
            MigStoreItem("pgift_yacht", "Luxury Yacht", StoreCategory.PREMIUM_GIFTS, "Superyacht global room broadcast. Code: luxury yacht", 350, emoji = "🛥️", commandCode = "luxury yacht", tagText = "Command"),
            MigStoreItem("pgift_jet", "Private Jet", StoreCategory.PREMIUM_GIFTS, "Sky jet flight VIP broadcast. Code: private jet", 500, emoji = "🛩️", commandCode = "private jet", tagText = "Command"),
            MigStoreItem("pgift_tiara", "Diamond Tiara", StoreCategory.PREMIUM_GIFTS, "Sparkling diamond royal tiara. Code: diamond tiara", 180, emoji = "💎", commandCode = "diamond tiara", tagText = "Command"),
            MigStoreItem("pgift_car", "Sports Supercar", StoreCategory.PREMIUM_GIFTS, "Red exotic racing supercar. Code: sports car", 250, emoji = "🏎️", commandCode = "sports car", tagText = "Command"),
            MigStoreItem("pgift_champ", "Champagne Tower", StoreCategory.PREMIUM_GIFTS, "Luxury VIP celebration toast. Code: champagne tower", 120, emoji = "🍾", commandCode = "champagne tower", tagText = "Command"),
            MigStoreItem("pgift_castle", "Crystal Castle", StoreCategory.PREMIUM_GIFTS, "Majestic glowing crystal kingdom. Code: crystal castle", 400, emoji = "🏰", commandCode = "crystal castle", tagText = "Command"),
            MigStoreItem("pgift_pegasus", "Pegasus Wings", StoreCategory.PREMIUM_GIFTS, "Mythical glowing celestial wings. Code: pegasus wings", 300, emoji = "🦄", commandCode = "pegasus wings", tagText = "Command"),

            // 4. GIFTS (🎁 Classic Gifts)
            MigStoreItem("gift_rose", "Red Rose", StoreCategory.GIFTS, "Romantic fresh red blossom. Code: rose", 10, emoji = "🌹", commandCode = "rose", tagText = "Command"),
            MigStoreItem("gift_heart", "Love Heart", StoreCategory.GIFTS, "Sweet heart love expression. Code: heart", 15, emoji = "❤️", commandCode = "heart", tagText = "Command"),
            MigStoreItem("gift_teddy", "Teddy Bear", StoreCategory.GIFTS, "Cute cuddly bear for friends. Code: teddy", 25, emoji = "🧸", commandCode = "teddy", tagText = "Command"),
            MigStoreItem("gift_cake", "Birthday Cake", StoreCategory.GIFTS, "Sweet celebration birthday cake. Code: cake", 30, emoji = "🎂", commandCode = "cake", tagText = "Command"),
            MigStoreItem("gift_coffee", "Cup of Coffee", StoreCategory.GIFTS, "Warm coffee treat. Code: coffee", 5, emoji = "☕", commandCode = "coffee", tagText = "Command"),
            MigStoreItem("gift_choco", "Box of Chocolates", StoreCategory.GIFTS, "Sweet Belgian chocolates. Code: chocolate", 20, emoji = "🍫", commandCode = "chocolate", tagText = "Command"),
            MigStoreItem("gift_fireworks", "Party Fireworks", StoreCategory.GIFTS, "Sparkling firework sky show. Code: fireworks", 40, emoji = "🎆", commandCode = "fireworks", tagText = "Command"),
            MigStoreItem("gift_trophy", "Golden Trophy", StoreCategory.GIFTS, "Grand champion trophy cup. Code: trophy", 50, emoji = "🏆", commandCode = "trophy", tagText = "Command"),
            MigStoreItem("gift_clover", "Lucky Clover", StoreCategory.GIFTS, "4-leaf good luck charm. Code: clover", 8, emoji = "🍀", commandCode = "clover", tagText = "Command"),

            // 5. VVIP GIFTS (💎 VVIP Gifts from Screenshot 2)
            MigStoreItem("vvip_topaz", "Heart Topaz", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: heart topaz", 100, emoji = "💙", commandCode = "heart topaz", tagText = "Command"),
            MigStoreItem("vvip_pearl", "Blue Pearl", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: blue pearl", 65, emoji = "🔮", commandCode = "blue pearl", tagText = "Command"),
            MigStoreItem("vvip_azurite", "Azurite", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: azurite", 60, emoji = "🔷", commandCode = "azurite", tagText = "Command"),
            MigStoreItem("vvip_howlite", "Howlite", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: howlite", 60, emoji = "⚪", commandCode = "howlite", tagText = "Command"),
            MigStoreItem("vvip_sapphire_pendant", "Topaz Sapphire Pendant", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: topaz sapphire pendant", 60, emoji = "📿", commandCode = "topaz sapphire pendant", tagText = "Command"),
            MigStoreItem("vvip_butterfly_brooch", "Colorful Gemstone Butterfly Brooch", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: colorful gemstone butterfly brooch", 51, emoji = "🦋", commandCode = "colorful gemstone butterfly brooch", tagText = "Command"),
            MigStoreItem("vvip_diamond", "Fancy Diamond", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: fancy diamond", 50, emoji = "💎", commandCode = "fancy diamond", tagText = "Command"),
            MigStoreItem("vvip_sun_jewel", "Sun Jewel", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: sun jewel", 50, emoji = "☀️", commandCode = "sun jewel", tagText = "Command"),
            MigStoreItem("vvip_gucci_bag", "Gucci Rabbit Bag", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: gucci rabbit bag", 35, emoji = "👜", commandCode = "gucci rabbit bag", tagText = "Command"),
            MigStoreItem("vvip_emerald_ring", "Emerald Dragon Ring", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: emerald ring", 85, emoji = "💍", commandCode = "emerald ring", tagText = "Command"),
            MigStoreItem("vvip_ruby_locket", "Ruby Heart Locket", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: ruby locket", 75, emoji = "💖", commandCode = "ruby locket", tagText = "Command"),
            MigStoreItem("vvip_black_opal", "Black Opal Tiara", StoreCategory.VVIP_GIFTS, "Command gift item.\nCode: black opal", 120, emoji = "👑", commandCode = "black opal", tagText = "Command"),

            // 6. ADDONS (⭐ Addons from Screenshot 3)
            MigStoreItem("addon_room_1slot", "Create Managed Room 1 Slot", StoreCategory.ADDONS, "Add one managed room creation slot.", 1000, emoji = "🏢", tagText = "0/20 extra slots"),
            MigStoreItem("addon_room_4slots", "Create Managed Room 4 Slots", StoreCategory.ADDONS, "Add four managed room creation slots.", 3000, emoji = "🏬", tagText = "0/20 extra slots"),
            MigStoreItem("addon_change_owner", "Change Managed Owner", StoreCategory.ADDONS, "Transfer one managed room owner to another verified user.", 1000, emoji = "👥", tagText = "Addons"),
            MigStoreItem("addon_silent_7d", "Silent Invisible Join Room - 7 Days", StoreCategory.ADDONS, "Hide entry and exit broadcasts for 7 days.", 0, status = StoreItemStatus.OWNED, emoji = "🔕", tagText = "Tap to extend"),
            MigStoreItem("addon_silent_1m", "Silent Invisible Join Room - 1 Month", StoreCategory.ADDONS, "Hide entry and exit broadcasts for 1 month.", 1000, emoji = "🔕", tagText = "Addons"),
            MigStoreItem("addon_email_change", "Change Email ID", StoreCategory.ADDONS, "Buy one verified email change credit.", 2000, emoji = "✉️", tagText = "Addons"),
            MigStoreItem("addon_full_inv_7d", "Full Invisible Join Room - 7 Days", StoreCategory.ADDONS, "Soon will be released.", 0, status = StoreItemStatus.SOON, emoji = "👻", tagText = "Soon will be released"),
            MigStoreItem("addon_full_inv_1m", "Full Invisible Join Room - 1 Month", StoreCategory.ADDONS, "Soon will be released.", 0, status = StoreItemStatus.SOON, emoji = "👻", tagText = "Soon will be released"),
            MigStoreItem("addon_full_pass_7d", "Room Full Pass Everywhere - 7 Days", StoreCategory.ADDONS, "Enter public and managed rooms even when the room is full for 7 days.", 150, emoji = "🎫", tagText = "Addons"),
            MigStoreItem("addon_full_pass_1m", "Room Full Pass Everywhere - 1 Month", StoreCategory.ADDONS, "Enter public and managed rooms even when the room is full for 1 month.", 500, emoji = "🎟️", tagText = "Addons"),
            MigStoreItem("addon_mediashare_7d", "Chatroom MediaShare - 7 Days", StoreCategory.ADDONS, "Send room photos and stickers for 7 days.", 0, status = StoreItemStatus.OWNED, emoji = "📸", tagText = "Tap to extend"),
            MigStoreItem("addon_mediashare_1m", "Chatroom MediaShare - 1 Month", StoreCategory.ADDONS, "Send room photos and stickers for 1 month.", 1000, emoji = "📸", tagText = "Addons")
        )
    }

    // Filter items based on active category, search query, and sort
    val filteredItems = remember(selectedCategory, searchQuery, priceSortOrder, ownedItemIds) {
        allStoreItems
            .filter { it.category == selectedCategory }
            .filter { item ->
                if (searchQuery.isBlank()) true
                else {
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    (item.commandCode != null && item.commandCode.contains(searchQuery, ignoreCase = true))
                }
            }
            .map { item ->
                if (ownedItemIds.contains(item.id)) {
                    item.copy(status = StoreItemStatus.OWNED)
                } else {
                    item
                }
            }
            .let { list ->
                when (priceSortOrder) {
                    1 -> list.sortedBy { it.priceCredits }
                    2 -> list.sortedByDescending { it.priceCredits }
                    else -> list
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MigHeaderBar(onMenuClick = onBack)
                // Sub Header: Store Title with Back & Live Credits
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏬", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Store",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Daily Lucky Spin Shortcut
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF9800))
                            .clickable { showLuckyWheelDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (canSpinToday) "Free Spin!" else "Daily Spin",
                            color = Color.White,
                            fontSize = 11.sp,
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
                .background(Color(0xFFF1F6FA))
                .testTag("mig33_store_screen")
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                // 1. mig33 Store Blue Banner Box (Matching screenshot 1, 2, 3)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0B3C87), Color(0xFF1565C0))
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🏪", fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "mig33 Store",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Buy emoticons, avatars, and addons.\nBrowse gift command items.",
                                        color = Color(0xFFE3F2FD),
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            // Credit Badge (Orange Pill as in screenshot e.g. "278,0 cr")
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFF6D00))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${userProfile.credits},0 cr",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Category Chips Row (Matching screenshot tabs)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreCategory.values().forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    selectedCategory = cat
                                    searchQuery = ""
                                }
                                .testTag("store_tab_${cat.name.lowercase()}"),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFFFF6D00) else Color.White,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB)),
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.icon,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.label,
                                    color = if (isSelected) Color.White else Color(0xFF0D47A1),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Search Bar + Sort Control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Input Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        placeholder = {
                            Text(
                                text = "Search ${selectedCategory.label} name or code...",
                                fontSize = 12.sp,
                                color = Color(0xFF90A4AE),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF78909C)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF0288D1),
                            unfocusedBorderColor = Color(0xFFCFD8DC)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price Filter Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                priceSortOrder = (priceSortOrder + 1) % 3
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCFD8DC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Filter",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (priceSortOrder) {
                                    1 -> "Price: Low to High"
                                    2 -> "Price: High to Low"
                                    else -> "Price: Default"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF37474F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Section Items Title
                Text(
                    text = "${selectedCategory.label} Items",
                    color = Color(0xFF0D47A1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5. 3-Column Items Grid (Matching Screenshots 1, 2, 3)
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔍", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No items found for \"$searchQuery\"",
                                color = Color(0xFF78909C),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("store_items_grid"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredItems, key = { it.id }) { item ->
                            StoreItemCard(
                                item = item,
                                onClick = {
                                    when (item.status) {
                                        StoreItemStatus.SOON -> {
                                            Toast.makeText(context, "Coming soon in upcoming release!", Toast.LENGTH_SHORT).show()
                                        }
                                        StoreItemStatus.OWNED -> {
                                            if (item.category == StoreCategory.AVATAR && item.avatarPresetId != null) {
                                                repository.updateAvatarConfig(
                                                    userProfile.avatarConfig.copy(presetId = item.avatarPresetId)
                                                )
                                                Toast.makeText(context, "Avatar equipped successfully!", Toast.LENGTH_SHORT).show()
                                            } else if (item.category == StoreCategory.EMOTICONS) {
                                                selectedEmoticonPackForPreview = item
                                            } else {
                                                itemDetailDialog = item
                                            }
                                        }
                                        StoreItemStatus.EQUIPPED -> {
                                            if (item.category == StoreCategory.EMOTICONS) {
                                                selectedEmoticonPackForPreview = item
                                            } else {
                                                Toast.makeText(context, "Currently active and equipped!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        StoreItemStatus.AVAILABLE -> {
                                            if (item.category == StoreCategory.EMOTICONS) {
                                                selectedEmoticonPackForPreview = item
                                            } else if (item.category == StoreCategory.GIFTS || item.category == StoreCategory.PREMIUM_GIFTS || item.category == StoreCategory.VVIP_GIFTS) {
                                                itemDetailDialog = item
                                            } else {
                                                itemToPurchase = item
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Purchase Dialog
    itemToPurchase?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToPurchase = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Purchase Item", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(text = item.name, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.description, fontSize = 12.sp, color = Color(0xFF546E7A))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Price:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text(text = "${item.priceCredits} cr", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your Balance: ${userProfile.credits} cr",
                        fontSize = 11.sp,
                        color = if (userProfile.credits >= item.priceCredits) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userProfile.credits >= item.priceCredits) {
                            val success = repository.deductCredits(item.priceCredits)
                            if (success) {
                                ownedItemIds = (ownedItemIds + item.id).toMutableSet()
                                if (item.category == StoreCategory.AVATAR && item.avatarPresetId != null) {
                                    repository.updateAvatarConfig(
                                        userProfile.avatarConfig.copy(presetId = item.avatarPresetId)
                                    )
                                }
                                Toast.makeText(context, "Purchased ${item.name} successfully!", Toast.LENGTH_SHORT).show()
                            }
                            itemToPurchase = null
                        } else {
                            Toast.makeText(context, "Insufficient credits! Spin the Lucky Wheel for free credits.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Buy Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToPurchase = null }) {
                    Text("Cancel", color = Color(0xFF546E7A))
                }
            }
        )
    }

    // Detail / Action Dialog (for Command Gifts, VVIP, Addons)
    itemDetailDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { itemDetailDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Column {
                    Text(text = item.description, fontSize = 12.sp, color = Color(0xFF455A64))
                    Spacer(modifier = Modifier.height(10.dp))

                    if (item.commandCode != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFFECEFF1)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "/gift ${item.commandCode}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D47A1)
                                )
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Gift Command", "/gift ${item.commandCode}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Command copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(text = "Copy", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Category: ${item.category.label}", fontSize = 11.sp, color = Color(0xFF78909C))
                        if (item.priceCredits > 0) {
                            Text(text = "${item.priceCredits} cr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                    }
                }
            },
            confirmButton = {
                if (item.category == StoreCategory.GIFTS || item.category == StoreCategory.PREMIUM_GIFTS || item.category == StoreCategory.VVIP_GIFTS) {
                    Button(
                        onClick = {
                            selectedGiftForSend = VirtualGift(
                                id = item.id,
                                name = item.name,
                                emoji = item.emoji,
                                priceCredits = item.priceCredits,
                                category = item.category.label,
                                effectDescription = item.description
                            )
                            itemDetailDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Send Gift 🎁", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (item.status == StoreItemStatus.AVAILABLE) {
                    Button(
                        onClick = {
                            itemDetailDialog = null
                            itemToPurchase = item
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Purchase", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { itemDetailDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "OK", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { itemDetailDialog = null }) {
                    Text("Close", color = Color(0xFF546E7A))
                }
            }
        )
    }

    // Send Gift Dialog integration
    selectedGiftForSend?.let { gift ->
        MigSendGiftDialog(
            gifts = listOf(gift) + repository.availableGifts,
            userCredits = userProfile.credits,
            recipientName = "Friends & Rooms",
            onSendGift = { g ->
                repository.sendVirtualGift("general", g, "Friends")
                selectedGiftForSend = null
                Toast.makeText(context, "Gift sent to chat!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { selectedGiftForSend = null }
        )
    }

    // Emoticon Pack Preview Dialog
    selectedEmoticonPackForPreview?.let { pack ->
        val packEmoticons = remember(pack.id) { getEmoticonsForPack(pack.id) }
        val isOwned = ownedItemIds.contains(pack.id) || pack.priceCredits == 0

        AlertDialog(
            onDismissRequest = { selectedEmoticonPackForPreview = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = pack.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = pack.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0D47A1))
                            Text(text = "${packEmoticons.size} Emoticons", fontSize = 11.sp, color = Color(0xFF546E7A))
                        }
                    }
                    if (isOwned) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "Unlocked ✓",
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = pack.description,
                        fontSize = 12.sp,
                        color = Color(0xFF455A64)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط على أي إيموجي لنسخ كود الاختصار للشات:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Emoticon Grid
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(packEmoticons) { (icon, code) ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Emoticon Code", code)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "تم نسخ الكود: $code", Toast.LENGTH_SHORT).show()
                                        },
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1)),
                                    shadowElevation = 0.5.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = icon, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = code,
                                            fontSize = 9.sp,
                                            color = Color(0xFF0288D1),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!isOwned && pack.priceCredits > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFF3E0))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "سعر الحزمة:", fontSize = 11.sp, color = Color(0xFF5D4037))
                            Text(text = "${pack.priceCredits} cr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                    }
                }
            },
            confirmButton = {
                if (!isOwned && pack.priceCredits > 0) {
                    Button(
                        onClick = {
                            if (userProfile.credits >= pack.priceCredits) {
                                val success = repository.deductCredits(pack.priceCredits)
                                if (success) {
                                    ownedItemIds = (ownedItemIds + pack.id).toMutableSet()
                                    Toast.makeText(context, "تم شراء حزمة ${pack.name} بنجاح!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "رصيد الكريدت غير كافٍ! يمكنك تدوير عجلة الحظ يومياً للربح مجاناً.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "شراء (${pack.priceCredits} cr)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            Toast.makeText(context, "تم تفعيل حزمة ${pack.name} لشات mig33!", Toast.LENGTH_SHORT).show()
                            selectedEmoticonPackForPreview = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "استخدام الحزمة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEmoticonPackForPreview = null }) {
                    Text("إغلاق", color = Color(0xFF546E7A))
                }
            }
        )
    }

    // Daily Lucky Spin Wheel Dialog
    if (showLuckyWheelDialog) {
        MigLuckyWheelDialog(
            canSpinToday = canSpinToday,
            lastSpinPoints = lastSpinPoints,
            onSpinComplete = { pointsWon ->
                repository.performDailySpin(pointsWon)
            },
            onDismiss = { showLuckyWheelDialog = false }
        )
    }
}

fun getEmoticonsForPack(packId: String): List<Pair<String, String>> {
    return when (packId) {
        "emo_classic" -> listOf(
            "😊" to ":)", "😢" to ":(", "😄" to ":D", "😛" to ":P",
            "😉" to ";)", "❤️" to "(h)", "💋" to "(k)", "😍" to "(l)",
            "💔" to "(u)", "😈" to "(devil)", "😇" to "(angel)", "🎉" to "(party)",
            "😴" to "(sleepy)", "🤓" to "(nerd)", "😡" to "(angry)", "💣" to "(bomb)",
            "🍺" to "(beer)", "☕" to "(coffee)", "🤗" to "(hug)", "🎵" to "(music)",
            "🔥" to "(fire)", "👍" to "(y)", "👎" to "(n)", "🌹" to "(rose)"
        )
        "emo_animal" -> listOf(
            "🐱" to "(cat)", "🐶" to "(dog)", "🐼" to "(panda)", "🐵" to "(monkey)",
            "🐯" to "(tiger)", "🦁" to "(lion)", "🐰" to "(rabbit)", "🐻" to "(bear)",
            "🦊" to "(fox)", "🐨" to "(koala)", "🐺" to "(wolf)", "🐧" to "(penguin)",
            "🐸" to "(frog)", "🐥" to "(chick)", "🐮" to "(cow)", "🐷" to "(pig)"
        )
        "emo_ace" -> listOf(
            "♠️" to "(ace1)", "♥️" to "(ace2)", "♦️" to "(ace3)", "♣️" to "(ace4)",
            "🃏" to "(joker)", "🎲" to "(acedice)", "🎰" to "(aceslots)", "🪙" to "(acechip)",
            "🕶️" to "(acecool)", "🚬" to "(acesmoke)", "👑" to "(aceking)", "👸" to "(acequeen)"
        )
        "emo_adro" -> listOf(
            "👾" to "(adro1)", "🤖" to "(adrobot)", "🕺" to "(adrodance)", "🥰" to "(adrolove)",
            "😂" to "(adrolaugh)", "😭" to "(adrosad)", "😴" to "(adrosleep)", "⚡" to "(adropow)",
            "🎸" to "(adrorock)", "⭐" to "(adrostar)", "🕶️" to "(adrocool)", "👋" to "(adrohi)"
        )
        "emo_alpha" -> listOf(
            "🅰️" to "(a)", "🅱️" to "(b)", "🅲" to "(c)", "🅳" to "(d)",
            "🅴" to "(e)", "🅵" to "(f)", "🅶" to "(g)", "🅷" to "(h)",
            "🅸" to "(i)", "🅹" to "(j)", "🅺" to "(k)", "🅻" to "(l)",
            "🅼" to "(mig)", "3️⃣" to "(3)", "3️⃣" to "(33)", "❗" to "(!)"
        )
        "emo_alter" -> listOf(
            "🎭" to "(alt_mask)", "😏" to "(alt_smirk)", "🙄" to "(alt_roll)", "🤐" to "(alt_mute)",
            "🤤" to "(alt_drool)", "🤯" to "(alt_boom)", "🥳" to "(alt_party)", "🤪" to "(alt_crazy)",
            "🥴" to "(alt_woozy)", "🤠" to "(alt_cowboy)", "😎" to "(alt_boss)", "🥶" to "(alt_cold)"
        )
        "emo_anime", "emo_anime2" -> listOf(
            "🥺" to "(anime_cry)", "✨" to "(anime_star)", "😳" to "(anime_blush)", "😅" to "(anime_sweat)",
            "😱" to "(anime_shock)", "✌️" to "(anime_peace)", "🌸" to "(anime_blossom)", "💖" to "(anime_heart)",
            "💢" to "(anime_angry)", "🙇" to "(anime_bow)", "🐱" to "(anime_neko)", "🍙" to "(anime_onigiri)"
        )
        "emo_big" -> listOf(
            "😀" to "(bighappy)", "😆" to "(biglaugh)", "😍" to "(biglove)", "😭" to "(bigcry)",
            "😮" to "(bigshock)", "😡" to "(bigangry)", "😋" to "(bigyum)", "😜" to "(bigwink)",
            "😱" to "(bigfear)", "🥱" to "(bigyawn)", "🥳" to "(bigcheer)", "🤩" to "(bigstar)"
        )
        "emo_devil" -> listOf(
            "😈" to "(devil_flame)", "😇" to "(angel_halo)", "👿" to "(devil_angry)", "🪽" to "(angel_wings)",
            "🔱" to "(devil_fork)", "🙏" to "(angel_pray)", "🔥" to "(hell_fire)", "☁️" to "(heaven_cloud)",
            "⚡" to "(devil_bolt)", "✨" to "(angel_glow)", "🪄" to "(magic_wand)", "🕯️" to "(holy_candle)"
        )
        "emo_love" -> listOf(
            "💋" to "(kiss_red)", "🌹" to "(love_rose)", "💌" to "(love_letter)", "💘" to "(cupid_bow)",
            "💍" to "(heart_ring)", "💔" to "(broken_heart)", "💕" to "(two_hearts)", "💖" to "(sparkle_heart)",
            "🧸" to "(teddy_love)", "💐" to "(bouquet)", "🍫" to "(choco_love)", "🕊️" to "(love_dove)"
        )
        "emo_gangsta" -> listOf(
            "🕶️" to "(shades)", "🧢" to "(swag_cap)", "📻" to "(boombox)", "💰" to "(money_bag)",
            "⛓️" to "(gold_chain)", "🎤" to "(rap_mic)", "🎨" to "(graffiti)", "😎" to "(gangsta)",
            "💵" to "(dollar)", "🛹" to "(skate)", "👟" to "(kicks)", "💎" to "(ice_bling)"
        )
        "emo_sports" -> listOf(
            "⚽" to "(soccer_ball)", "🟥" to "(red_card)", "🟨" to "(yellow_card)", "🥅" to "(goal_fire)",
            "🏆" to "(trophy_gold)", "🏀" to "(basketball)", "🏏" to "(cricket)", "🥊" to "(boxing)",
            "🥇" to "(gold_medal)", "🎾" to "(tennis)", "⛳" to "(golf)", "🏁" to "(finish_flag)"
        )
        "emo_horror" -> listOf(
            "💀" to "(skull_cross)", "👻" to "(ghost_boo)", "🎃" to "(pumpkin_glow)", "🧛" to "(vampire)",
            "🦇" to "(bat_night)", "🕸️" to "(spider_web)", "🧟" to "(zombie)", "⚰️" to "(coffin)",
            "🔮" to "(crystal_orb)", "🔪" to "(slasher)", "🌙" to "(dark_moon)", "🧙" to "(witch)"
        )
        "emo_party" -> listOf(
            "🍕" to "(pizza)", "🍔" to "(burger)", "🍻" to "(beer_cheers)", "🍸" to "(cocktail)",
            "🪩" to "(disco_ball)", "🎂" to "(birthday_cake)", "🍿" to "(popcorn)", "🍩" to "(donut)",
            "🍾" to "(champagne)", "🍦" to "(ice_cream)", "🎸" to "(party_guitar)", "🎊" to "(confetti)"
        )
        "emo_meme" -> listOf(
            "🤪" to "(troll_face)", "🤬" to "(rage_face)", "😐" to "(poker_face)", "🤣" to "(lol_laugh)",
            "🤦" to "(facepalm)", "🕶️" to "(deal_with_it)", "🤡" to "(derp_derp)", "🐸" to "(pepe_frog)",
            "👀" to "(side_eye)", "🗿" to "(moai_chad)", "💪" to "(flex_meme)", "🤫" to "(shh_mewing)"
        )
        "emo_neon" -> listOf(
            "⚡" to "(neon_bolt)", "💜" to "(neon_heart)", "🌟" to "(neon_star)", "🪐" to "(cyber_planet)",
            "🔮" to "(neon_orb)", "🎆" to "(neon_burst)", "🛸" to "(ufo_beam)", "👾" to "(neon_alien)"
        )
        "emo_festive" -> listOf(
            "🌙" to "(crescent_moon)", "🏮" to "(fanous_lantern)", "⭐" to "(festive_star)", "🍯" to "(eid_sweets)",
            "🎆" to "(celebration_fire)", "🪔" to "(oud_perfume)", "🕌" to "(masjid_dome)", "🎁" to "(eid_gift)"
        )
        "emo_arrow" -> listOf(
            "⬆️" to "(arrow_up)", "⬇️" to "(arrow_down)", "⬅️" to "(arrow_left)", "➡️" to "(arrow_right)",
            "🔄" to "(arrow_cycle)", "⚡" to "(arrow_lightning)", "🎯" to "(target_bullseye)", "🚩" to "(red_flag)"
        )
        "emo_zodiac" -> listOf(
            "♈" to "(aries)", "♉" to "(taurus)", "♊" to "(gemini)", "♋" to "(cancer)",
            "♌" to "(leo)", "♍" to "(virgo)", "♎" to "(libra)", "♏" to "(scorpio)",
            "♐" to "(sagittarius)", "♑" to "(capricorn)", "♒" to "(aquarius)", "♓" to "(pisces)"
        )
        else -> listOf(
            "😊" to ":)", "❤️" to "(h)", "🎉" to "(party)", "🔥" to "(fire)",
            "👍" to "(y)", "⭐" to "(star)", "✨" to "(sparkle)", "💬" to "(chat)"
        )
    }
}

@Composable
fun StoreItemCard(
    item: MigStoreItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("store_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Icon on left, Badge on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon or Preview
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imageRes != null) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(text = item.emoji, fontSize = 22.sp)
                    }
                }

                // Price / Status Badge (Orange Pill)
                val badgeText = when (item.status) {
                    StoreItemStatus.OWNED -> "Owned"
                    StoreItemStatus.SOON -> "Soon"
                    StoreItemStatus.EQUIPPED -> "Active"
                    StoreItemStatus.AVAILABLE -> "${item.priceCredits} cr"
                }

                val badgeBg = when (item.status) {
                    StoreItemStatus.OWNED -> Color(0xFFFF6D00)
                    StoreItemStatus.SOON -> Color(0xFFFF8F00)
                    StoreItemStatus.EQUIPPED -> Color(0xFF2E7D32)
                    StoreItemStatus.AVAILABLE -> Color(0xFFFF6D00)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.name,
                color = Color(0xFF1E293B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Description / Subtitle
            Text(
                text = item.description,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Category / Tag text (Green / Teal / Blue / Orange)
            val tagText = item.tagText ?: item.category.label
            val tagColor = when {
                tagText.contains("equip", ignoreCase = true) || tagText.contains("extend", ignoreCase = true) -> Color(0xFF00897B)
                tagText.contains("slots", ignoreCase = true) -> Color(0xFFE65100)
                tagText.contains("Command", ignoreCase = true) -> Color(0xFF0288D1)
                tagText.contains("Soon", ignoreCase = true) -> Color(0xFFE65100)
                else -> Color(0xFF00897B)
            }

            Text(
                text = tagText,
                color = tagColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

