package com.example.model

enum class StoreCategory(val label: String, val icon: String) {
    EMOTICONS("Emoticons", "😊"),
    AVATAR("Avatar", "👤"),
    PREMIUM_GIFTS("Premium Gifts", "🏅"),
    GIFTS("Gifts", "🎁"),
    VVIP_GIFTS("VVIP Gifts", "💎"),
    ADDONS("Addons", "⭐")
}

enum class StoreItemStatus {
    AVAILABLE,
    OWNED,
    SOON,
    EQUIPPED
}

data class MigStoreItem(
    val id: String,
    val name: String,
    val category: StoreCategory,
    val description: String,
    val priceCredits: Int,
    val status: StoreItemStatus = StoreItemStatus.AVAILABLE,
    val emoji: String = "🎁",
    val imageRes: Int? = null,
    val commandCode: String? = null,
    val tagText: String? = null,
    val avatarPresetId: String? = null,
    val slotInfo: String? = null
)
