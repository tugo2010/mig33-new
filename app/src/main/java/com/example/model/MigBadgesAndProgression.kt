package com.example.model

data class MigBadge(
    val id: String,
    val name: String,
    val icon: String,
    val category: String, // "Levels", "Admin & Staff", "Verification", "Merchants & Recharging", "VIP & Crowns", "Gifts & Romance", "Games & Trivia", "Rooms & Hosts", "Special & Events"
    val description: String,
    val unlockCriteria: String,
    val isOwned: Boolean,
    val unlockedDate: String? = null,
    val rarity: String = "Common", // "Common", "Rare", "Epic", "Legendary"
    val requiredLevel: Int? = null,
    val grantType: String = "CRITERIA", // "ADMIN_ONLY", "LEVEL_LOCK", "RECHARGE", "SPECIAL_EVENT", "CRITERIA"
    val badgeNumber: String? = null,
    val levelNumber: Int? = null,
    val imagePath: String? = null
)

data class MigLevelProgression(
    val currentLevel: Int,
    val currentXP: Long,
    val xpForCurrentLevel: Long,
    val xpForNextLevel: Long,
    val progressPercent: Float,
    val rankTitle: String,
    val rankIcon: String,
    val perksUnlocked: List<String>
)

object MigLevelCalculator {
    /**
     * mig33 Hardcore Leveling Formula:
     * In original mig33, reaching higher levels was a true badge of status, requiring
     * continuous active time, chat participation, and dedication.
     *
     * Base XP curve: XP(L) = 25.0 * L^2.65 + 120.0 * (L - 1)
     */
    fun xpRequiredForLevel(level: Int): Long {
        if (level <= 1) return 0L
        return (25.0 * Math.pow(level.toDouble(), 2.65) + 120.0 * (level - 1)).toLong()
    }

    fun calculateProgression(totalXP: Long): MigLevelProgression {
        var level = 1
        while (xpRequiredForLevel(level + 1) <= totalXP && level < 150) {
            level++
        }

        val baseXP = xpRequiredForLevel(level)
        val nextXP = xpRequiredForLevel(level + 1)
        val diff = (nextXP - baseXP).coerceAtLeast(1L)
        val currentProgress = (totalXP - baseXP).coerceAtLeast(0L)
        val percent = (currentProgress.toFloat() / diff.toFloat()).coerceIn(0f, 1f)

        val rankTitle = getRankTitle(level)
        val rankIcon = getRankIcon(level)

        val perks = mutableListOf<String>()
        if (level >= 2) perks.add("Basic chat participation & emoticons")
        if (level >= 5) perks.add("Unlocked colored status bubbles & animated stickers")
        if (level >= 10) perks.add("Can create and moderate Custom Chat Rooms")
        if (level >= 15) perks.add("Access to VIP Lounge and High-Stakes Games")
        if (level >= 20) perks.add("Special Robot & Star badges beside nickname")
        if (level >= 24) perks.add("Expanded 500+ friends list capacity & Gold badge slot")
        if (level >= 30) perks.add("Exclusive animated avatar aura & custom font styling")
        if (level >= 40) perks.add("Global megaphone broadcasts & Hall of Fame entry")
        if (level >= 50) perks.add("Royal Crown aura, immunity from room kicks & Master Moderator")

        return MigLevelProgression(
            currentLevel = level,
            currentXP = totalXP,
            xpForCurrentLevel = baseXP,
            xpForNextLevel = nextXP,
            progressPercent = percent,
            rankTitle = rankTitle,
            rankIcon = rankIcon,
            perksUnlocked = perks
        )
    }

    fun getRankTitle(level: Int): String {
        return when {
            level >= 150 -> "Supreme Demiurge (إمبراطور الأعلى)"
            level >= 140 -> "Universal Sovereign (حاكم العالم)"
            level >= 130 -> "Celestial Overlord (أمير الفضاء)"
            level >= 120 -> "Cosmic Archon (سيد الكون)"
            level >= 110 -> "Eternal Ascendant (الخالد)"
            level >= 100 -> "migDemigod (شبه إله)"
            level >= 90  -> "Imperial Monarch (إمبراطور مهيب)"
            level >= 80  -> "Grand Sovereign (سيد الرتب)"
            level >= 70  -> "Crown Titan (فارس التاج الذهبي)"
            level >= 60  -> "Mythic Elder (عميد الأساطير)"
            level >= 50  -> "Royal Emperor (إمبراطور ملكي)"
            level >= 45  -> "Imperial Lord (لورد إمبراطوري)"
            level >= 40  -> "mig33 Legend (أسطورة الرومات)"
            level >= 35  -> "Warlord Champion (بطل الرومات)"
            level >= 30  -> "Grand Master (الأستاذ الأكبر)"
            level >= 28  -> "High Sentinel (حارس الدرجة الأولى)"
            level >= 24  -> "Elite migMaster (الروبوت الذهبي)"
            level >= 20  -> "Senior Citizen (روبوت كلاسيكي 🤖)"
            level >= 18  -> "Room Guardian (حارس الشات)"
            level >= 15  -> "Star Regular (نجم الرومات ⭐)"
            level >= 12  -> "Chat Explorer (مستكشف متمرس)"
            level >= 10  -> "Active Chatter (شاتر نشط ⚡)"
            level >= 7   -> "Room Regular (عضو دائم)"
            level >= 5   -> "Rising Star (نجم صاعد)"
            level >= 3   -> "Chat Enthusiast (متحمس)"
            level >= 2   -> "Novice Regular (عضو مبتدئ)"
            else -> "Newbie (مستجد)"
        }
    }

    fun getRankIcon(level: Int): String {
        return when {
            level >= 140 -> "🌌"
            level >= 120 -> "🪐"
            level >= 100 -> "☀️"
            level >= 80  -> "👑"
            level >= 60  -> "⚜️"
            level >= 50  -> "🏆"
            level >= 40  -> "💎"
            level >= 30  -> "🌟"
            level >= 24  -> "🤖"
            level >= 20  -> "⚡"
            level >= 15  -> "⭐"
            level >= 10  -> "💬"
            level >= 5   -> "🌱"
            level >= 2   -> "✨"
            else -> "🐣"
        }
    }

    fun generate150LevelBadges(): List<MigBadge> {
        return (1..150).map { lvl ->
            val rankTitle = getRankTitle(lvl)
            val rankIcon = getRankIcon(lvl)
            val xpReq = xpRequiredForLevel(lvl)
            val rarity = when {
                lvl >= 100 -> "Legendary"
                lvl >= 50  -> "Epic"
                lvl >= 20  -> "Rare"
                else       -> "Common"
            }
            val desc = when {
                lvl == 1 -> "Welcome to mig33! First step into the world of global mobile chatrooms (المستوى 1 في عالم mig33)."
                lvl == 24 -> "Proved ultimate loyalty by reaching Level 24 on the hardcore mig33 XP curve (روبوت migMaster الكلاسيكي 🤖)."
                lvl == 50 -> "Royal Emperor status. Official room moderation perks, kick immunity & royal crown (إمبراطور ملكي)."
                lvl == 100 -> "migDemigod standing. Reached the legendary triple-digit tier (رتبة شبه إله mig33 الأسطورية)."
                lvl == 150 -> "The supreme summit of mig33 existence. Level 150 perfection (قمة المجد وأعلى مستوى في mig33)."
                lvl in 2..4 -> "Level $lvl regular chatter. Unlocked classic mig33 emoticons (عضو منتظم في غرف الشات)."
                lvl in 5..9 -> "Level $lvl rising star with custom status colors and room engagement (نجم صاعد مع ألوان حالة مخصصة)."
                lvl in 10..19 -> "Level $lvl migCitizen. Can create rooms, host events and send animated gifts (مواطن mig33 معتمد)."
                lvl in 20..29 -> "Level $lvl veteran chatter. Respected status across all regional rooms (رتبة متقدمة وروبوت كلاسيكي)."
                lvl in 30..49 -> "Level $lvl grand master. Renowned personality with high-tier privileges (أستاذ الشات مع صلاحيات كبرى)."
                lvl in 50..79 -> "Level $lvl elite sovereign. Revered community leader and game champion (قائد مجتمعي وبطل ألعاب)."
                lvl in 80..99 -> "Level $lvl imperial monarch nearing triple-digit demigod status (إمبراطور مهيب يقترب من المستوى 100)."
                lvl in 101..124 -> "Level $lvl celestial master with unmatched prestige in migWorld (مكانة استثنائية في عالم mig33)."
                lvl in 125..149 -> "Level $lvl ascendant Titan nearing the ultimate level 150 threshold (عملاق أسطوري يقترب من المستوى النهائي)."
                else -> "Official mig33 Level $lvl badge ($rankTitle)."
            }
            MigBadge(
                id = "b_level_$lvl",
                name = "Level $lvl: $rankTitle",
                icon = rankIcon,
                category = "Levels",
                description = desc,
                unlockCriteria = "Reach migLevel $lvl (%,d XP)".format(xpReq),
                isOwned = (lvl <= 24),
                requiredLevel = lvl,
                rarity = rarity,
                grantType = "LEVEL_LOCK",
                badgeNumber = "%03d".format(lvl),
                levelNumber = lvl,
                imagePath = "mig_levels/$lvl.png"
            )
        }
    }

    val ALL_SPECIAL_BADGES: List<MigBadge> = listOf(
        // ==========================================
        // 1. ADMIN, DEVELOPERS & OFFICIAL STAFF (تمنح من خلال الأدمن فقط)
        // ==========================================
        MigBadge(
            id = "b_001_admin",
            badgeNumber = "001",
            name = "Official System Administrator",
            icon = "⚡",
            category = "Admin & Staff",
            description = "Super administrator with server root privileges, global broadcast control, security and platform management.",
            unlockCriteria = "Official mig33 Administration Grant (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "01 Jan 2011",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_002_super_mod",
            badgeNumber = "002",
            name = "Global Super Moderator (مشرف عام)",
            icon = "🚨",
            category = "Admin & Staff",
            description = "Senior global supervisor overseeing international chat security, anti-spam shields and ban appeals.",
            unlockCriteria = "mig33 Security Directorate Appointment (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "10 Jul 2011",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_003_moderator",
            badgeNumber = "003",
            name = "Official Moderator",
            icon = "🛡️",
            category = "Admin & Staff",
            description = "Official appointed moderator maintaining community safety and harmony in public rooms.",
            unlockCriteria = "Official Admin Grant (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "15 Sep 2011",
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_004_security",
            badgeNumber = "004",
            name = "mig33 Security & Anti-Cheat",
            icon = "🔒",
            category = "Admin & Staff",
            description = "Security engineering personnel safeguarding servers, encryption layers, and counter-exploit protocols.",
            unlockCriteria = "Admin Security Directorate (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "05 May 2012",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_005_bot_master",
            badgeNumber = "005",
            name = "Official Game Bot Master",
            icon = "🤖",
            category = "Admin & Staff",
            description = "Developer and operator of automated room game bots, Danger1 quiz engines, and dice servers.",
            unlockCriteria = "Official Bot Integration License (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_006_system_bot",
            badgeNumber = "006",
            name = "System Service Daemon",
            icon = "⚙️",
            category = "Admin & Staff",
            description = "Automated core system daemon managing room notifications, credit transactions, and background queues.",
            unlockCriteria = "System Infrastructure Grant (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_007_official_staff",
            badgeNumber = "007",
            name = "Official mig33 Staff",
            icon = "🏛️",
            category = "Admin & Staff",
            description = "Certified headquarters staff member in Melbourne, Jakarta and Singapore HQ.",
            unlockCriteria = "Official mig33 Staff Employment (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "01 Jan 2011",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_008_auditor",
            badgeNumber = "008",
            name = "System & Financial Auditor",
            icon = "📋",
            category = "Admin & Staff",
            description = "Authorized auditor overseeing merchant logs, credit transfers, and escrow reconciliation.",
            unlockCriteria = "Admin Governance Grant (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_009_lead_dev",
            badgeNumber = "009",
            name = "Core Engine Lead Developer",
            icon = "💻",
            category = "Admin & Staff",
            description = "Lead engineer of the real-time socket protocol, packet compression algorithms, and client binaries.",
            unlockCriteria = "mig33 Core Engineering Team (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "15 Mar 2011",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_010_support_care",
            badgeNumber = "010",
            name = "Customer Care Specialist",
            icon = "🎧",
            category = "Admin & Staff",
            description = "Official helpdesk and user support agent resolving lost account credentials and billing inquiries.",
            unlockCriteria = "Official Customer Support Grant (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Rare",
            grantType = "ADMIN_ONLY"
        ),

        // ==========================================
        // 2. OFFICIAL VERIFICATION BADGES (شارات التفعيل والتوثيق - تمنح من الأدمن فقط)
        // ==========================================
        MigBadge(
            id = "b_011_verified_blue",
            badgeNumber = "011",
            name = "Official Verified Blue Star",
            icon = "🔷",
            category = "Verification",
            description = "Official blue checkmark star badge for verified public figures, top community stars, and notable accounts.",
            unlockCriteria = "Official Admin Verification (تمنح من خلال الأدمن فقط بعد التحقق)",
            isOwned = true,
            unlockedDate = "20 Apr 2012",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_012_verified_gold",
            badgeNumber = "012",
            name = "Official Gold Entity Badge",
            icon = "⭐",
            category = "Verification",
            description = "Golden verification badge for officially recognized organizations, brands, and public entities.",
            unlockCriteria = "Official Admin Brand Verification (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "10 May 2012",
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_013_verified_green",
            badgeNumber = "013",
            name = "Verified Merchant Partner",
            icon = "🌿",
            category = "Verification",
            description = "Green shield verification badge for approved commercial partners and telecom distributors.",
            unlockCriteria = "Official Admin Partnership Agreement (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_014_celebrity_star",
            badgeNumber = "014",
            name = "Celebrity & Artist Star",
            icon = "🌟",
            category = "Verification",
            description = "Exclusive badge for verified music artists, actors, creators, and public celebrities.",
            unlockCriteria = "Celebrity Verification Department (تمنح من خلال الأدمن فقط)",
            isOwned = false,
            rarity = "Legendary",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_015_protected_id",
            badgeNumber = "015",
            name = "Protected High-Security ID",
            icon = "🔐",
            category = "Verification",
            description = "Security badge confirming two-factor hardware lock and immune to unauthorized account transfer.",
            unlockCriteria = "Admin Security Certification (تمنح من خلال الأدمن فقط)",
            isOwned = true,
            unlockedDate = "12 Jun 2012",
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),

        // ==========================================
        // 3. MERCHANTS, RECHARGING & CREDIT ECONOMY (شحن وتجارة الرصيد)
        // ==========================================
        MigBadge(
            id = "b_016_merchant",
            badgeNumber = "016",
            name = "Certified migMerchant",
            icon = "💼",
            category = "Merchants & Recharging",
            description = "Authorized merchant with official credit redistribution quota and local banking channels.",
            unlockCriteria = "Maintain active Merchant status & 2,500+ transfers",
            isOwned = true,
            unlockedDate = "15 Jan 2012",
            rarity = "Epic",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_017_master_merch",
            badgeNumber = "017",
            name = "Master Merchant of migWorld",
            icon = "💎",
            category = "Merchants & Recharging",
            description = "Top echelon merchant with 50,000+ completed client recharge orders.",
            unlockCriteria = "Recharge/Distribute 50,000+ migCredits across network",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_018_global_reseller",
            badgeNumber = "018",
            name = "Global Credit Reseller",
            icon = "🌍",
            category = "Merchants & Recharging",
            description = "Cross-border credit distributor handling multi-currency mobile prepaid recharges.",
            unlockCriteria = "Distribute credits in 3+ international countries",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_019_top_merchant",
            badgeNumber = "019",
            name = "Top Merchant of the Month",
            icon = "🏆",
            category = "Merchants & Recharging",
            description = "Recognized #1 top selling credit merchant in the regional leaderboard.",
            unlockCriteria = "Rank #1 in monthly merchant sales volume",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_020_credit_dealer",
            badgeNumber = "020",
            name = "Authorized Credit Dealer",
            icon = "💳",
            category = "Merchants & Recharging",
            description = "Official dealer badge with discounted bulk reload privileges.",
            unlockCriteria = "Recharge 10,000+ migCredits dealer bundle",
            isOwned = true,
            unlockedDate = "05 Mar 2012",
            rarity = "Rare",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_021_fast_pay",
            badgeNumber = "021",
            name = "Instant Fast Pay Dealer",
            icon = "⚡",
            category = "Merchants & Recharging",
            description = "Celebrated for lightning fast credit delivery within 60 seconds of order.",
            unlockCriteria = "Complete 100+ instantaneous fast pay deliveries",
            isOwned = true,
            unlockedDate = "19 Mar 2012",
            rarity = "Rare",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_022_official_bank",
            badgeNumber = "022",
            name = "Central migBank Member",
            icon = "🏦",
            category = "Merchants & Recharging",
            description = "Trusted banking liquidity pool operator with secured reserves.",
            unlockCriteria = "Maintain 100,000+ credit reserve in central vault",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_023_escrow_agent",
            badgeNumber = "023",
            name = "Certified Escrow Agent",
            icon = "⚖️",
            category = "Merchants & Recharging",
            description = "Neutral escrow mediator ensuring safe trade of high-value rare IDs and items.",
            unlockCriteria = "Conduct 50+ fraud-free escrow trade settlements",
            isOwned = false,
            rarity = "Epic",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_024_shop_owner",
            badgeNumber = "024",
            name = "Virtual Goods Shop Owner",
            icon = "🛍️",
            category = "Merchants & Recharging",
            description = "Operates a boutique avatar fashion & sticker shop in the migMarket.",
            unlockCriteria = "Sell 200+ virtual shop items in Avatar Studio",
            isOwned = false,
            rarity = "Rare",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_025_prime_seller",
            badgeNumber = "025",
            name = "Prime Seller Honor",
            icon = "👑",
            category = "Merchants & Recharging",
            description = "Maintained 100% positive 5-star customer review rating for 6+ months.",
            unlockCriteria = "Achieve 500+ verified 5-star merchant ratings",
            isOwned = false,
            rarity = "Epic",
            grantType = "RECHARGE"
        ),

        // ==========================================
        // 4. VIP TIERS, RECHARGING & ROYAL CROWNS (عضويات VIP والتيجان عبر الشحن والرصيد)
        // ==========================================
        MigBadge(
            id = "b_026_bronze_vip",
            badgeNumber = "026",
            name = "Bronze VIP Member",
            icon = "🥉",
            category = "VIP & Crowns",
            description = "Entry-level VIP membership with bronze name tag and priority room entrance.",
            unlockCriteria = "Recharge 500+ migCredits",
            isOwned = true,
            unlockedDate = "02 Jan 2012",
            rarity = "Common",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_027_silver_vip",
            badgeNumber = "027",
            name = "Silver VIP Member",
            icon = "🥈",
            category = "VIP & Crowns",
            description = "Silver VIP tier with expanded buddy list and custom colored nickname text.",
            unlockCriteria = "Recharge 1,500+ migCredits",
            isOwned = true,
            unlockedDate = "14 Jan 2012",
            rarity = "Rare",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_028_gold_vip",
            badgeNumber = "028",
            name = "Gold VIP Member",
            icon = "🥇",
            category = "VIP & Crowns",
            description = "Prestigious Gold VIP with glowing golden avatar aura and permanent room reservations.",
            unlockCriteria = "Recharge 3,500+ migCredits",
            isOwned = true,
            unlockedDate = "10 Feb 2012",
            rarity = "Epic",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_029_platinum_vip",
            badgeNumber = "029",
            name = "Platinum VIP Elite",
            icon = "💎",
            category = "VIP & Crowns",
            description = "Platinum elite status with kick immunity in standard rooms and unlimited whisper tabs.",
            unlockCriteria = "Recharge 8,000+ migCredits",
            isOwned = true,
            unlockedDate = "01 Mar 2012",
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_030_ruby_vvip",
            badgeNumber = "030",
            name = "Ruby VVIP Member",
            icon = "🔻",
            category = "VIP & Crowns",
            description = "Ultra luxury Ruby tier with flaming red nickname aura and megaphone discounts.",
            unlockCriteria = "Recharge 15,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_031_sapphire_vip",
            badgeNumber = "031",
            name = "Sapphire VIP Royal",
            icon = "🔷",
            category = "VIP & Crowns",
            description = "Royal Sapphire membership with custom animated entrance banner in all rooms.",
            unlockCriteria = "Recharge 30,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_032_emerald_vip",
            badgeNumber = "032",
            name = "Emerald VIP Legend",
            icon = "❇️",
            category = "VIP & Crowns",
            description = "Rare Emerald rank with custom sound effects when joining partner rooms.",
            unlockCriteria = "Recharge 50,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_033_diamond_vip",
            badgeNumber = "033",
            name = "Diamond VIP Supreme",
            icon = "💠",
            category = "VIP & Crowns",
            description = "Diamond tier with personalized customer concierge and customized animated gift.",
            unlockCriteria = "Recharge 100,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_034_black_elite",
            badgeNumber = "034",
            name = "Black Elite Sovereign",
            icon = "🖤",
            category = "VIP & Crowns",
            description = "Ultra high-roller Black Elite tier with full immunity from room moderator mutes.",
            unlockCriteria = "Recharge 250,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_035_vip_supreme",
            badgeNumber = "035",
            name = "Supreme Master VIP",
            icon = "👑",
            category = "VIP & Crowns",
            description = "The pinnacle of VIP power with imperial golden crown and highest server priority.",
            unlockCriteria = "Recharge 500,000+ migCredits",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),

        // Room & Community Leadership
        MigBadge(
            id = "b_046_room_vip",
            badgeNumber = "046",
            name = "Room VIP Star (نجم الغرفة)",
            icon = "⭐",
            category = "Rooms & Hosts",
            description = "High-ranking VIP participant with guaranteed slot entry in full chatrooms.",
            unlockCriteria = "Earned in official room activities or VIP grant",
            isOwned = true,
            unlockedDate = "15 Jan 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_047_chat_king",
            badgeNumber = "047",
            name = "Chatroom Sovereign (سلطان الروم)",
            icon = "🏰",
            category = "Rooms & Hosts",
            description = "Sovereign leader managing top regional chat hubs with broadcast privileges.",
            unlockCriteria = "Maintain top 3 popular room for 30 consecutive days",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_048_quiz_master",
            badgeNumber = "048",
            name = "Danger1 Quiz Master (بطل المسابقات)",
            icon = "❓",
            category = "Games & Contests",
            description = "Master mind behind Danger1, Trivia & Quiz bot championship victories.",
            unlockCriteria = "Win 100+ trivia bot games",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_049_bot_commander",
            badgeNumber = "049",
            name = "Bot Fleet Commander (قائد البوتات)",
            icon = "⚙️",
            category = "Admin & Staff",
            description = "Operator of custom room game bots and automated entertainment services.",
            unlockCriteria = "Official Bot Operator License",
            isOwned = false,
            rarity = "Epic",
            grantType = "ADMIN_ONLY"
        ),
        MigBadge(
            id = "b_050_legacy_member",
            badgeNumber = "050",
            name = "mig33 Legacy Member (عضو شرف تاريخي)",
            icon = "🎖️",
            category = "History & Community",
            description = "Pioneer member who joined during the golden J2ME era of mig33 mobile chat.",
            unlockCriteria = "Historic account creation (J2ME / WAP Era)",
            isOwned = true,
            unlockedDate = "2006",
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_051_golden_voice",
            badgeNumber = "051",
            name = "Golden Voice Performer (صوت الشات الذهبي)",
            icon = "🎤",
            category = "Rooms & Hosts",
            description = "Celebrated voice talent and singer in live audio & radio rooms.",
            unlockCriteria = "Perform in 50+ official room radio shows",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_052_event_host",
            badgeNumber = "052",
            name = "Official Tournament Host (منظم المسابقات)",
            icon = "🎟️",
            category = "Games & Contests",
            description = "Certified host organizing official mig33 regional gaming and chat events.",
            unlockCriteria = "Host 10+ official community tournaments",
            isOwned = false,
            rarity = "Epic",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_053_top_donator",
            badgeNumber = "053",
            name = "Grand Benefactor (داعم الشات الأول)",
            icon = "💰",
            category = "Merchant & VIP",
            description = "Generous supporter gifting premium avatars and virtual gifts to community members.",
            unlockCriteria = "Send 100,000+ credits in gifts to friends",
            isOwned = true,
            unlockedDate = "10 May 2013",
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_054_guild_leader",
            badgeNumber = "054",
            name = "migWorld Guild Leader (قائد الكلان)",
            icon = "🛡️",
            category = "History & Community",
            description = "Leader of top-tier mig33 clan or guild competing in global room rankings.",
            unlockCriteria = "Lead a Level 5 Guild with 50+ active members",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_055_royal_shield",
            badgeNumber = "055",
            name = "Imperial Royal Shield (الدرع الملكي)",
            icon = "🔱",
            category = "VIP & Crowns",
            description = "Exclusive royal protection shield granting kick immunity and VIP status.",
            unlockCriteria = "Active Royal VIP subscription or admin grant",
            isOwned = true,
            unlockedDate = "01 Dec 2012",
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),

        // Royal Crowns
        MigBadge(
            id = "b_056_bronze_crown",
            badgeNumber = "056",
            name = "Bronze Crown of Honor",
            icon = "🪖",
            category = "VIP & Crowns",
            description = "Awarded to distinguished room leaders who completed 100+ active hosting hours.",
            unlockCriteria = "Host 100+ hours in partner rooms",
            isOwned = true,
            unlockedDate = "11 Feb 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_057_silver_crown",
            badgeNumber = "057",
            name = "Silver Royal Crown",
            icon = "🥈",
            category = "VIP & Crowns",
            description = "Noble Silver Crown for chat leaders with large room followings.",
            unlockCriteria = "Accumulate 1,000+ room fan votes",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_058_gold_crown",
            badgeNumber = "058",
            name = "Golden Imperial Crown",
            icon = "👑",
            category = "VIP & Crowns",
            description = "Gleaming Golden Crown recognized across all international chat networks.",
            unlockCriteria = "Win Top Room Host Annual Championship",
            isOwned = false,
            rarity = "Legendary",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_059_platinum_crown",
            badgeNumber = "059",
            name = "Platinum Emperor Crown",
            icon = "💎",
            category = "VIP & Crowns",
            description = "Emperor rank crown reserved for legends of the migWorld universe.",
            unlockCriteria = "Attain Platinum VIP & Level 40+",
            isOwned = false,
            rarity = "Legendary",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_060_royal_crown",
            badgeNumber = "060",
            name = "Supreme Imperial Crown",
            icon = "⚜️",
            category = "VIP & Crowns",
            description = "The supreme imperial insignia of supreme honor and royalty.",
            unlockCriteria = "Supreme Council of migWorld Grand Award",
            isOwned = false,
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),

        // Rare Jewels & Diamonds
        MigBadge(
            id = "b_061_crystal_gem",
            badgeNumber = "061",
            name = "Crystal Star Gem",
            icon = "🔮",
            category = "VIP & Crowns",
            description = "Collected during the legendary Crystal Hunt game events.",
            unlockCriteria = "Collect 50 Crystal Gems in game events",
            isOwned = true,
            unlockedDate = "14 Mar 2012",
            rarity = "Rare",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_062_cyan_diamond",
            badgeNumber = "062",
            name = "Cyan Blue Diamond",
            icon = "💎",
            category = "VIP & Crowns",
            description = "Rare blue gemstone found in high-stakes treasure boxes.",
            unlockCriteria = "Open 25+ Mystery Gem Chests",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_063_deep_diamond",
            badgeNumber = "063",
            name = "Deep Ocean Diamond",
            icon = "🌊",
            category = "VIP & Crowns",
            description = "Mystic jewel from the underwater fantasy avatar series.",
            unlockCriteria = "Collect full Ocean Jewel collection",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_064_pink_diamond",
            badgeNumber = "064",
            name = "Rare Pink Diamond",
            icon = "🌸",
            category = "VIP & Crowns",
            description = "Extremely rare romantic pink diamond awarded to high tier gifters.",
            unlockCriteria = "Send 100+ luxury diamond gifts",
            isOwned = false,
            rarity = "Legendary",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_065_black_diamond",
            badgeNumber = "065",
            name = "Mythic Black Diamond",
            icon = "🖤",
            category = "VIP & Crowns",
            description = "The rarest gem in mig33 history with pitch-black shimmering aura.",
            unlockCriteria = "Hold Black Elite status & 500,000+ gifting XP",
            isOwned = false,
            rarity = "Legendary",
            grantType = "CRITERIA"
        ),

        // ==========================================
        // 5. VIRTUAL GIFTS & SOCIAL PHILANTHROPY (الهدايا الافتراضية والرومانسية)
        // ==========================================
        MigBadge(
            id = "b_066_gift_star",
            badgeNumber = "066",
            name = "Gift Star",
            icon = "⭐",
            category = "Gifts & Romance",
            description = "Warm-hearted chatter who loves making friends smile with virtual gifts.",
            unlockCriteria = "Send 25+ Virtual Gifts in rooms",
            isOwned = true,
            unlockedDate = "05 Jan 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_067_gift_hero",
            badgeNumber = "067",
            name = "Gift Hero",
            icon = "🦸",
            category = "Gifts & Romance",
            description = "Generous friend known for sending gifts during parties and birthdays.",
            unlockCriteria = "Send 100+ Virtual Gifts",
            isOwned = true,
            unlockedDate = "18 Feb 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_068_gift_king",
            badgeNumber = "068",
            name = "Gift King of the Room",
            icon = "👑",
            category = "Gifts & Romance",
            description = "The room explodes with flowers and gifts whenever this member arrives!",
            unlockCriteria = "Send 500+ Virtual Gifts",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_069_gift_angel",
            badgeNumber = "069",
            name = "Gift Angel",
            icon = "👼",
            category = "Gifts & Romance",
            description = "Beloved chatter adored by hundreds who shower them with gifts.",
            unlockCriteria = "Receive 200+ Virtual Gifts from friends",
            isOwned = true,
            unlockedDate = "22 Mar 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_070_mega_gifter",
            badgeNumber = "070",
            name = "Mega Gifter Titan",
            icon = "🎁",
            category = "Gifts & Romance",
            description = "One of the top philanthropic titans in mig33 history.",
            unlockCriteria = "Send 1,000+ Virtual Gifts across all rooms",
            isOwned = false,
            rarity = "Legendary",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_081_top_fan_heart",
            badgeNumber = "081",
            name = "Top Fan Heart",
            icon = "💖",
            category = "Gifts & Romance",
            description = "Ranked #1 Top Fan in your favorite creator's room for 3 consecutive weeks.",
            unlockCriteria = "Hold #1 Top Fan ranking in room",
            isOwned = true,
            unlockedDate = "14 Feb 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_082_true_love",
            badgeNumber = "082",
            name = "True Love Couple",
            icon = "💕",
            category = "Gifts & Romance",
            description = "Celebrated official mig33 couple married in a virtual wedding chapel.",
            unlockCriteria = "Hold active migCouple status for 90+ days",
            isOwned = true,
            unlockedDate = "14 Feb 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_083_romantic_soul",
            badgeNumber = "083",
            name = "Romantic Soul",
            icon = "🌹",
            category = "Gifts & Romance",
            description = "Sent hundreds of virtual red roses and love notes across chatrooms.",
            unlockCriteria = "Send 100+ Red Roses gifts",
            isOwned = true,
            unlockedDate = "28 Feb 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_084_heart_collector",
            badgeNumber = "084",
            name = "Heart Collector",
            icon = "💝",
            category = "Gifts & Romance",
            description = "Accumulated over 500 heart emojis and tokens on user profile wall.",
            unlockCriteria = "Collect 500+ hearts on profile wall",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_085_famous_icon",
            badgeNumber = "085",
            name = "Mini-Blog Famous Icon",
            icon = "🔥",
            category = "Gifts & Romance",
            description = "Published viral mini-blog status posts that achieved hundreds of stars.",
            unlockCriteria = "Reach 500+ stars on a single feed post",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_086_charm_master",
            badgeNumber = "086",
            name = "Charm & Charisma Master",
            icon = "✨",
            category = "Gifts & Romance",
            description = "High charisma rating voted by fellow room chatters.",
            unlockCriteria = "Achieve 1,000+ profile charm points",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_087_top_footprint",
            badgeNumber = "087",
            name = "Top Profile Footprints",
            icon = "👣",
            category = "Gifts & Romance",
            description = "One of the most visited and searched profiles in the mig33 directory.",
            unlockCriteria = "Profile visited 10,000+ times",
            isOwned = true,
            unlockedDate = "19 May 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_088_daily_active",
            badgeNumber = "088",
            name = "30-Day Active Streak",
            icon = "📅",
            category = "Gifts & Romance",
            description = "Dedicated member who logged in and chatted 30 consecutive days.",
            unlockCriteria = "Log in 30 consecutive days",
            isOwned = true,
            unlockedDate = "30 Jan 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),

        // ==========================================
        // 6. ROOM MASTERS & CHAT HOSTS (الغرف، المضيفين والمشرفين)
        // ==========================================
        MigBadge(
            id = "b_036_room_host",
            badgeNumber = "036",
            name = "Official Room Host",
            icon = "🏠",
            category = "Rooms & Hosts",
            description = "Created and manages an active public custom chatroom.",
            unlockCriteria = "Create a custom room with 50+ members",
            isOwned = true,
            unlockedDate = "10 Jan 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_037_co_host",
            badgeNumber = "037",
            name = "Chatroom Co-Host",
            icon = "🤝",
            category = "Rooms & Hosts",
            description = "Trusted right-hand co-host managing games and moderation.",
            unlockCriteria = "Appointed Co-Host in 3+ active rooms",
            isOwned = true,
            unlockedDate = "22 Jan 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_038_room_kicker",
            badgeNumber = "038",
            name = "Room Enforcer (Kicker)",
            icon = "⚡",
            category = "Rooms & Hosts",
            description = "Fast hands against spam bots and room flooders.",
            unlockCriteria = "Successfully kick/ban 50+ abusive spam bots",
            isOwned = true,
            unlockedDate = "15 Feb 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_039_room_dj",
            badgeNumber = "039",
            name = "migRadio Live DJ",
            icon = "🎧",
            category = "Rooms & Hosts",
            description = "Streamed live internet radio tunes and song requests for the room.",
            unlockCriteria = "Host 10+ live audio radio stream sessions",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_040_room_guard",
            badgeNumber = "040",
            name = "Room Security Guard",
            icon = "🛡️",
            category = "Rooms & Hosts",
            description = "Awarded for keeping room chat peaceful, polite, and free of insults.",
            unlockCriteria = "Active moderation in top room for 60+ days",
            isOwned = true,
            unlockedDate = "01 Mar 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_041_room_master",
            badgeNumber = "041",
            name = "Legendary Room Master",
            icon = "🏰",
            category = "Rooms & Hosts",
            description = "Founded a flagship room that reached 500+ maximum concurrent capacity.",
            unlockCriteria = "Chatroom reaches 500+ concurrent chatters",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_042_host_vip",
            badgeNumber = "042",
            name = "VIP Room Host",
            icon = "🌟",
            category = "Rooms & Hosts",
            description = "Hosts an exclusive VIP partner lounge with premium bot integrations.",
            unlockCriteria = "Host an official Level 25+ VIP Lounge",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_043_room_singer",
            badgeNumber = "043",
            name = "Voice & Talent Singer",
            icon = "🎤",
            category = "Rooms & Hosts",
            description = "Performed live singing and voice talents in voice audition rooms.",
            unlockCriteria = "Win Room Voice Talent Competition",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_044_event_judge",
            badgeNumber = "044",
            name = "Official Event Judge",
            icon = "⚖️",
            category = "Rooms & Hosts",
            description = "Judged official room trivia, poetry contests, and gaming tournaments.",
            unlockCriteria = "Judge 5+ official community room events",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_045_room_veteran",
            badgeNumber = "045",
            name = "Room Veteran Regular",
            icon = "🎖️",
            category = "Rooms & Hosts",
            description = "Spent over 500 hours participating in friendly room conversations.",
            unlockCriteria = "Accumulate 500+ hours in chatrooms",
            isOwned = true,
            unlockedDate = "18 Apr 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),

        // ==========================================
        // 7. GAMES & TRIVIA TOURNAMENTS (الألعاب والمسابقات)
        // ==========================================
        MigBadge(
            id = "b_071_gamer_bronze",
            badgeNumber = "071",
            name = "Bronze Gamer",
            icon = "🥉",
            category = "Games & Trivia",
            description = "Won initial 10 competitive game matches in the Game Lobby.",
            unlockCriteria = "Win 10 Game Matches",
            isOwned = true,
            unlockedDate = "08 Jan 2012",
            rarity = "Common",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_072_gamer_silver",
            badgeNumber = "072",
            name = "Silver Gamer",
            icon = "🥈",
            category = "Games & Trivia",
            description = "Won 50 matches across Danger 1, Dice, Cricket and Poker.",
            unlockCriteria = "Win 50 Game Matches",
            isOwned = true,
            unlockedDate = "15 Feb 2012",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_073_gamer_gold",
            badgeNumber = "073",
            name = "Gold Gamer",
            icon = "🥇",
            category = "Games & Trivia",
            description = "Master of mobile games with 150+ tournament victories.",
            unlockCriteria = "Win 150 Game Matches",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_074_arcade_pro",
            badgeNumber = "074",
            name = "Arcade Master Pro",
            icon = "🕹️",
            category = "Games & Trivia",
            description = "Top leaderboard scorer in Arcade mobile mini-games.",
            unlockCriteria = "Reach #1 weekly high-score in Arcade",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_075_trivia_master",
            badgeNumber = "075",
            name = "Danger 1 Trivia Showdown Ace",
            icon = "⚡",
            category = "Games & Trivia",
            description = "Answered quiz questions in under 3 seconds and survived 25+ rounds.",
            unlockCriteria = "Win 25+ Danger 1 Trivia rounds",
            isOwned = true,
            unlockedDate = "12 Aug 2012",
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_076_dice_legend",
            badgeNumber = "076",
            name = "Dice & High Roller Champion",
            icon = "🎲",
            category = "Games & Trivia",
            description = "Won 10 consecutive dice showdowns against top room rollers.",
            unlockCriteria = "Win 10 consecutive dice games",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_077_poker_ace",
            badgeNumber = "077",
            name = "migPoker Card Shark",
            icon = "♠️",
            category = "Games & Trivia",
            description = "Champion of the high-stakes migTexas Hold'em poker tables.",
            unlockCriteria = "Win 1st place in official migPoker Tournament",
            isOwned = false,
            rarity = "Epic",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_078_chess_master",
            badgeNumber = "078",
            name = "Chess & Strategy Master",
            icon = "♟️",
            category = "Games & Trivia",
            description = "Defeated 50+ opponents in 1v1 turn-based chess battles.",
            unlockCriteria = "Win 50+ Chess and Checkers matches",
            isOwned = false,
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_079_high_roller",
            badgeNumber = "079",
            name = "Casino High Roller",
            icon = "🎰",
            category = "Games & Trivia",
            description = "Wagered and won in high stakes 10,000+ credit tournaments.",
            unlockCriteria = "Win 10,000+ credits in a single high-stakes game",
            isOwned = false,
            rarity = "Legendary",
            grantType = "RECHARGE"
        ),
        MigBadge(
            id = "b_080_game_champion",
            badgeNumber = "080",
            name = "Global migGaming Champion",
            icon = "🏆",
            category = "Games & Trivia",
            description = "Crowned Champion of the official global mig33 Gaming Olympics.",
            unlockCriteria = "Win 1st Place in Global mig33 Gaming Olympics",
            isOwned = false,
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),

        // ==========================================
        // 8. SPECIAL EVENTS, CREATIVITY & HISTORICAL LEGACY (مناسبات خاصة وإرث كلاسيكي)
        // ==========================================
        MigBadge(
            id = "b_089_pioneer_2005",
            badgeNumber = "089",
            name = "2005-2006 J2ME Pioneer",
            icon = "📟",
            category = "Special & Events",
            description = "Original pioneer who used mig33 on Nokia 6600, N70, N73 & Sony Ericsson K750i.",
            unlockCriteria = "Registered account in 2005-2006 J2ME era (مناسبة خاصة)",
            isOwned = true,
            unlockedDate = "01 Jan 2006",
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_090_veteran_10y",
            badgeNumber = "090",
            name = "10-Year mig33 Loyalty Veteran",
            icon = "🏛️",
            category = "Special & Events",
            description = "Proved extraordinary loyalty across a decade of mobile chat history.",
            unlockCriteria = "10+ Years continuous active account anniversary (مناسبة خاصة)",
            isOwned = true,
            unlockedDate = "01 Jan 2016",
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_091_creator",
            badgeNumber = "091",
            name = "Official Content Creator",
            icon = "🎨",
            category = "Special & Events",
            description = "Created viral memes, blogs, room guides and creative media for the community.",
            unlockCriteria = "Selected as Official mig33 Community Creator (مناسبة خاصة)",
            isOwned = false,
            rarity = "Rare",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_092_theme_artist",
            badgeNumber = "092",
            name = "UI & Theme Artist",
            icon = "🖌️",
            category = "Special & Events",
            description = "Designed custom color themes and visual styles adopted by thousands of chatters.",
            unlockCriteria = "Win official Theme Design Contest (مناسبة خاصة)",
            isOwned = false,
            rarity = "Rare",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_093_emoji_maker",
            badgeNumber = "093",
            name = "Emoticon Pack Designer",
            icon = "😊",
            category = "Special & Events",
            description = "Designed iconic animated emoticons and sticker packs featured in the store.",
            unlockCriteria = "Have sticker pack published in mig33 Store (مناسبة خاصة)",
            isOwned = false,
            rarity = "Epic",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_094_avatar_stylist",
            badgeNumber = "094",
            name = "Avatar Fashion Stylist",
            icon = "👗",
            category = "Special & Events",
            description = "Equipped and showcased 20+ legendary avatar clothing sets and hairstyles.",
            unlockCriteria = "Own 20+ rare Avatar Studio sets (مناسبة خاصة)",
            isOwned = true,
            unlockedDate = "22 Nov 2011",
            rarity = "Rare",
            grantType = "CRITERIA"
        ),
        MigBadge(
            id = "b_095_radio_host",
            badgeNumber = "095",
            name = "Official migRadio Broadcaster",
            icon = "🎙️",
            category = "Special & Events",
            description = "Official radio show broadcaster hosting weekly live music and talk programs.",
            unlockCriteria = "Official migRadio Program Host License (مناسبة خاصة)",
            isOwned = false,
            rarity = "Epic",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_096_poet_author",
            badgeNumber = "096",
            name = "migPoet & Literature Author",
            icon = "📜",
            category = "Special & Events",
            description = "Authored heartfelt poetry and stories shared across community channels.",
            unlockCriteria = "Win migPoetry Monthly Literature Award (مناسبة خاصة)",
            isOwned = false,
            rarity = "Rare",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_097_ambassador",
            badgeNumber = "097",
            name = "Global Community Ambassador",
            icon = "🎖️",
            category = "Special & Events",
            description = "Regional community leader organizing real-world mig33 community meetups and events.",
            unlockCriteria = "Appointed Regional Ambassador (مناسبة ومؤتمرات رسمية)",
            isOwned = true,
            unlockedDate = "14 Feb 2012",
            rarity = "Epic",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_098_hall_of_fame",
            badgeNumber = "098",
            name = "mig33 Hall of Fame Immortal",
            icon = "🏛️",
            category = "Special & Events",
            description = "Permanently inducted into the mig33 Hall of Fame for legendary community impact.",
            unlockCriteria = "Official Hall of Fame Induction (مناسبات سنوية كبرى)",
            isOwned = false,
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_099_community_star",
            badgeNumber = "099",
            name = "Community Star of the Year",
            icon = "🌟",
            category = "Special & Events",
            description = "Voted by thousands of global chatters as the most inspiring community star of the year.",
            unlockCriteria = "Win Annual Global Community Choice Vote (مناسبة سنوية)",
            isOwned = false,
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        ),
        MigBadge(
            id = "b_100_mig_legend",
            badgeNumber = "100",
            name = "Absolute mig33 Living Legend",
            icon = "💫",
            category = "Special & Events",
            description = "The supreme historic badge denoting ultimate standing across all of migWorld.",
            unlockCriteria = "Supreme Historical Honor (أعلى وسام شرف في تاريخ mig33)",
            isOwned = false,
            rarity = "Legendary",
            grantType = "SPECIAL_EVENT"
        )
    )

    val ALL_MIG33_BADGES: List<MigBadge> = ALL_SPECIAL_BADGES + generate150LevelBadges()

    /**
     * Evaluates badges dynamically for the user.
     * All Level badges strictly require that the user's actual migLevel >= badge.requiredLevel.
     * Locked level badges cannot be owned by anyone without reaching the required level.
     */
    fun getBadgesForUser(currentLevel: Int, currentXP: Long = 0L): List<MigBadge> {
        return ALL_MIG33_BADGES.map { badge ->
            if (badge.requiredLevel != null) {
                val req = badge.requiredLevel
                val isUnlocked = currentLevel >= req
                val xpReq = xpRequiredForLevel(req)
                badge.copy(
                    isOwned = isUnlocked,
                    unlockCriteria = "Reach migLevel $req (%,d XP)".format(xpReq),
                    unlockedDate = if (isUnlocked) "Achieved (migLevel $req)" else null
                )
            } else {
                badge
            }
        }
    }
}
