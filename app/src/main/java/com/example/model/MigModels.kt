package com.example.model

import com.example.R

data class UserProfile(
    val username: String = "hipst4r",
    val displayName: String = "Fernando Tan",
    val statusMessage: String = "Hello world!",
    val bio: String = "Passionate mig33 chatter, loves games, music & making friends worldwide!",
    val migLevel: Int = 24,
    val gender: String = "Male",
    val birthday: String = "1 Jan 1988",
    val country: String = "Singapore",
    val city: String = "Singapore Central",
    val relationshipStatus: String = "Single",
    val hobbies: String = "Chatting, Gaming, Music, Photography",
    val isOnline: Boolean = true,
    val isInvisible: Boolean = false,
    val avatarConfig: AvatarConfig = AvatarConfig(),
    val credits: Int = 5480,
    val points: Int = 118450,
    val friendsCount: Int = 417,
    val postsCount: Int = 79,
    val followersCount: Int = 551,
    val followingCount: Int = 54,
    val badgesCount: Int = 1,
    val giftsCount: Int = 326,
    val watchlistCount: Int = 9,
    val badges: List<String> = listOf("A", "M"),
    val equippedBadgeNumbers: List<String> = listOf("001", "002", "010"),
    val securityPin: String = "1234"
)

data class CreditTransfer(
    val id: Long = 0,
    val senderUsername: String,
    val receiverUsername: String,
    val amount: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS"
)

sealed class TransferResult {
    data class Success(val transfer: CreditTransfer, val newBalance: Int) : TransferResult()
    data class Error(val message: String) : TransferResult()
}

data class AvatarConfig(
    val presetId: String = "mig_shadow_tuxedo",
    val customImageRes: Int? = R.drawable.img_avatar_shadow_tuxedo,
    val background: String = "bg_nightcity",
    val pet: String = "pet_shiba_inu",
    val base: String = "base_fair",
    val clothes: String = "cloth_denim_roses",
    val hair: String = "hair_spiky_brown",
    val accessory: String = "acc_blue_aviators",
    // Legacy support fields for existing features if needed
    val hairstyle: String = "Spiky Anime",
    val hairColorHex: Long = 0xFF5D4037,
    val outfit: String = "Blue Denim Jacket",
    val eyewear: String = "Dark Sunglasses",
    val backgroundTheme: String = "Sunny Garden & Fence",
    val mood: String = "Charming"
)

data class ChatRoom(
    val id: String,
    val name: String,
    val category: String, // "What's Hot", "Your Favorites", "Recent Rooms", "Play Games"
    val usersCount: Int,
    val maxUsers: Int,
    val isHot: Boolean = false,
    val isFavorite: Boolean = false,
    val topic: String = "Welcome to official mig33 chat! Have fun & be friendly.",
    val isGameActive: Boolean = false,
    val gameTitle: String = "Trivia Showdown",
    val gameRoundMessage: String = "CONGRATS! f1yingkit3 wins this round. Round ends. Next round in 10 seconds."
)

data class RoomMessage(
    val id: String,
    val roomId: String,
    val senderUsername: String,
    val senderDisplayName: String = "",
    val avatarRes: Int = R.drawable.avatar_b4sejump,
    val text: String,
    val timestamp: String,
    val isSystem: Boolean = false,
    val isGameAlert: Boolean = false,
    val isGiftAlert: Boolean = false,
    val giftName: String? = null,
    val giftIcon: String? = null,
    val adminBadgeId: String? = null,
    val nameColorHex: Long? = null
)

data class FeedPost(
    val id: String,
    val authorUsername: String,
    val authorDisplayName: String,
    val authorAvatarRes: Int,
    val isVerified: Boolean = false,
    val tag: String? = null,
    val content: String,
    val imageRes: Int? = null,
    val timeAgo: String = "3 hours ago via Web",
    val repliesCount: Int = 0,
    val shareCount: Int = 0,
    val starCount: Int = 0,
    val isStarred: Boolean = false,
    val badges: List<String> = emptyList(),
    val replies: List<PostReply> = emptyList()
)

data class PostReply(
    val id: String,
    val authorUsername: String,
    val authorDisplayName: String = "",
    val authorAvatarRes: Int = R.drawable.avatar_b4sejump,
    val replyToUser: String? = null,
    val content: String,
    val timeAgo: String = "2 hours ago via Web",
    val repliesCount: Int = 0,
    val shareCount: Int = 0,
    val starCount: Int = 0,
    val isStarred: Boolean = false,
    val badges: List<String> = emptyList()
)

data class DirectMessage(
    val id: String,
    val conversationUser: String,
    val senderUsername: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val avatarRes: Int
)

data class VirtualGift(
    val id: String,
    val name: String,
    val emoji: String,
    val priceCredits: Int,
    val category: String,
    val effectDescription: String
)

data class EmoticonItem(
    val code: String,
    val label: String,
    val symbol: String
)

data class BannedUser(
    val username: String,
    val reason: String,
    val durationHours: Int,
    val bannedAt: String,
    val bannedBy: String = "Admin"
)

data class AdminActionLog(
    val id: String,
    val actionType: String, // "KICK", "LEVEL_UP", "GRANT_BADGE", "BAN", "UNBAN"
    val targetUsername: String,
    val details: String,
    val timestamp: String
)

object AdminSettings {
    // Maps badge number (e.g. "001") to ARGB Long color (e.g. 0xFFFF0000)
    val defaultColors = mapOf(
        "001" to 0xFFFF6D00, // Admin (Orange)
        "002" to 0xFFFFD700, // VIP (Gold)
        "010" to 0xFF00796B, // Moderator (Teal)
        "003" to 0xFFFF9800, // Star Chatter (Orange)
        "004" to 0xFF00ACC1, // Diamond (Cyan)
        "005" to 0xFFFFEA00, // Top Contributor (Yellow)
        "011" to 0xFF00E5FF, // Verified (Blue)
        "024" to 0xFF512DA8  // Shop Owner (Purple)
    )
    val badgeNameColors = kotlinx.coroutines.flow.MutableStateFlow<Map<String, Long>>(defaultColors)
    
    fun getNameColorForBadge(badgeId: String?): Long? {
        return if (badgeId != null) badgeNameColors.value[badgeId] else null
    }
    
    fun setNameColorForBadge(badgeId: String, color: Long) {
        val current = badgeNameColors.value.toMutableMap()
        current[badgeId] = color
        badgeNameColors.value = current
    }
}

fun UserProfile.hasAdminBadge(): Boolean {
    val allBadges = MigLevelCalculator.getBadgesForUser(this.migLevel, this.points.toLong())
    return allBadges.any { badge ->
        badge.isOwned && (
            badge.category == "Admin & Staff" ||
            badge.grantType == "ADMIN_ONLY" ||
            (badge.badgeNumber != null && badge.badgeNumber in listOf("001", "002", "003", "004", "005", "006", "007", "008", "009", "010"))
        )
    }
}
