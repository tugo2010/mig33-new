package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.R
import com.example.model.AvatarConfig
import com.example.model.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
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
    val credits: Int = 5480,
    val points: Int = 118450,
    val friendsCount: Int = 417,
    val postsCount: Int = 79,
    val followersCount: Int = 551,
    val followingCount: Int = 54,
    val badgesCount: Int = 6,
    val giftsCount: Int = 326,
    val watchlistCount: Int = 9,
    val equippedBadgeNumbers: String = "001,002,010",
    val securityPin: String = "1234",
    // 2D Avatar Properties
    val avatarPresetId: String = "mig_shadow_tuxedo",
    val avatarCustomImageRes: Int? = R.drawable.img_avatar_shadow_tuxedo,
    val avatarBg: String = "bg_nightcity",
    val avatarPet: String = "pet_shiba_inu",
    val avatarBase: String = "base_fair",
    val avatarClothes: String = "cloth_denim_roses",
    val avatarHair: String = "hair_spiky_brown",
    val avatarAccessory: String = "acc_blue_aviators",
    // Legacy avatar properties
    val hairstyle: String = "Spiky Anime",
    val hairColorHex: Long = 0xFF5D4037,
    val outfit: String = "Blue Denim Jacket",
    val accessory: String = "Rose Bouquet",
    val eyewear: String = "Dark Sunglasses",
    val pet: String = "none",
    val backgroundTheme: String = "Sunny Garden & Fence",
    val mood: String = "Charming"
) {
    fun toDomain(): UserProfile {
        val calculatedLevel = com.example.model.MigLevelCalculator.calculateProgression(points.toLong()).currentLevel
        return UserProfile(
            username = username,
            displayName = displayName,
            statusMessage = statusMessage,
            bio = bio,
            migLevel = calculatedLevel,
            gender = gender,
            birthday = birthday,
            country = country,
            city = city,
            relationshipStatus = relationshipStatus,
            hobbies = hobbies,
            isOnline = isOnline,
            isInvisible = isInvisible,
            avatarConfig = AvatarConfig(
                presetId = avatarPresetId,
                customImageRes = avatarCustomImageRes,
                background = avatarBg,
                pet = avatarPet,
                base = avatarBase,
                clothes = avatarClothes,
                hair = avatarHair,
                accessory = avatarAccessory,
                hairstyle = hairstyle,
                hairColorHex = hairColorHex,
                outfit = outfit,
                eyewear = eyewear,
                backgroundTheme = backgroundTheme,
                mood = mood
            ),
            credits = credits,
            points = points,
            friendsCount = friendsCount,
            postsCount = postsCount,
            followersCount = followersCount,
            followingCount = followingCount,
            badgesCount = badgesCount,
            giftsCount = giftsCount,
            watchlistCount = watchlistCount,
            badges = listOf("A", "M"),
            equippedBadgeNumbers = equippedBadgeNumbers.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            securityPin = securityPin
        )
    }

    companion object {
        fun fromDomain(profile: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                id = "current_user",
                username = profile.username,
                displayName = profile.displayName,
                statusMessage = profile.statusMessage,
                bio = profile.bio,
                migLevel = profile.migLevel,
                gender = profile.gender,
                birthday = profile.birthday,
                country = profile.country,
                city = profile.city,
                relationshipStatus = profile.relationshipStatus,
                hobbies = profile.hobbies,
                isOnline = profile.isOnline,
                isInvisible = profile.isInvisible,
                credits = profile.credits,
                points = profile.points,
                friendsCount = profile.friendsCount,
                postsCount = profile.postsCount,
                followersCount = profile.followersCount,
                followingCount = profile.followingCount,
                badgesCount = profile.badgesCount,
                giftsCount = profile.giftsCount,
                watchlistCount = profile.watchlistCount,
                equippedBadgeNumbers = profile.equippedBadgeNumbers.joinToString(","),
                securityPin = profile.securityPin,
                avatarPresetId = profile.avatarConfig.presetId,
                avatarCustomImageRes = profile.avatarConfig.customImageRes,
                avatarBg = profile.avatarConfig.background,
                avatarPet = profile.avatarConfig.pet,
                avatarBase = profile.avatarConfig.base,
                avatarClothes = profile.avatarConfig.clothes,
                avatarHair = profile.avatarConfig.hair,
                avatarAccessory = profile.avatarConfig.accessory,
                hairstyle = profile.avatarConfig.hairstyle,
                hairColorHex = profile.avatarConfig.hairColorHex,
                outfit = profile.avatarConfig.outfit,
                accessory = profile.avatarConfig.accessory,
                eyewear = profile.avatarConfig.eyewear,
                pet = profile.avatarConfig.pet,
                backgroundTheme = profile.avatarConfig.backgroundTheme,
                mood = profile.avatarConfig.mood
            )
        }
    }
}

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val usersCount: Int,
    val maxUsers: Int,
    val isHot: Boolean = false,
    val isFavorite: Boolean = false,
    val topic: String,
    val isGameActive: Boolean = false,
    val gameTitle: String = "Trivia Showdown",
    val gameRoundMessage: String = "CONGRATS! f1yingkit3 wins this round. Round ends. Next round in 10 seconds."
)

@Entity(tableName = "room_messages")
data class RoomMessageEntity(
    @PrimaryKey val id: String,
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
    val nameColorHex: Long? = null,
    val createdMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "direct_messages")
data class DirectMessageEntity(
    @PrimaryKey val id: String,
    val conversationUser: String,
    val senderUsername: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val avatarRes: Int,
    val createdMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
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
    val createdMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "post_replies")
data class PostReplyEntity(
    @PrimaryKey val id: String,
    val postId: String,
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
    val createdMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val username: String,
    val isFriend: Boolean = true,
    val isFollower: Boolean = false,
    val isFollowing: Boolean = false
)

@Entity(tableName = "credit_transfers")
data class CreditTransferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderUsername: String,
    val receiverUsername: String,
    val amount: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS"
) {
    fun toDomain(): com.example.model.CreditTransfer {
        return com.example.model.CreditTransfer(
            id = id,
            senderUsername = senderUsername,
            receiverUsername = receiverUsername,
            amount = amount,
            note = note,
            timestamp = timestamp,
            status = status
        )
    }
}
