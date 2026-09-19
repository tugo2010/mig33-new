package com.example.data

import android.content.Context
import com.example.R
import com.example.data.local.MigDatabase
import com.example.data.local.entities.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MigRepository(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val database = MigDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val chatRoomDao = database.chatRoomDao()
    private val roomMessageDao = database.roomMessageDao()
    private val directMessageDao = database.directMessageDao()
    private val feedDao = database.feedDao()
    private val contactDao = database.contactDao()
    private val creditTransferDao = database.creditTransferDao()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val prefs = context.getSharedPreferences("mig_daily_spin", Context.MODE_PRIVATE)

    private fun checkCanSpinDaily(): Boolean {
        val lastDateStr = prefs.getString("last_spin_date", "") ?: ""
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        return lastDateStr != todayStr
    }

    private val _canSpinDaily = MutableStateFlow(checkCanSpinDaily())
    val canSpinDaily: StateFlow<Boolean> = _canSpinDaily.asStateFlow()

    private val _lastSpinPoints = MutableStateFlow(prefs.getInt("last_spin_points", 0))
    val lastSpinPoints: StateFlow<Int> = _lastSpinPoints.asStateFlow()

    fun refreshDailySpinState() {
        _canSpinDaily.value = checkCanSpinDaily()
        _lastSpinPoints.value = prefs.getInt("last_spin_points", 0)
    }

    fun performDailySpin(pointsWon: Int): Boolean {
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val lastDateStr = prefs.getString("last_spin_date", "") ?: ""
        if (lastDateStr == todayStr) {
            return false
        }
        prefs.edit()
            .putString("last_spin_date", todayStr)
            .putLong("last_spin_timestamp", System.currentTimeMillis())
            .putInt("last_spin_points", pointsWon)
            .apply()
        _canSpinDaily.value = false
        _lastSpinPoints.value = pointsWon
        addCredits(pointsWon)
        return true
    }

    // Current User Profile State backed by Room
    val userProfile: StateFlow<UserProfile> = userDao.getUserProfile()
        .map { entity -> entity?.toDomain() ?: UserProfile() }
        .stateIn(scope, SharingStarted.Eagerly, UserProfile())

    // Chat Rooms State backed by Room
    val chatRooms: StateFlow<List<ChatRoom>> = chatRoomDao.getAllRooms()
        .map { list ->
            list.map { entity ->
                ChatRoom(
                    id = entity.id,
                    name = entity.name,
                    category = entity.category,
                    usersCount = entity.usersCount,
                    maxUsers = entity.maxUsers,
                    isHot = entity.isHot,
                    isFavorite = entity.isFavorite,
                    topic = entity.topic,
                    isGameActive = entity.isGameActive,
                    gameTitle = entity.gameTitle,
                    gameRoundMessage = entity.gameRoundMessage
                )
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Room Messages State backed by Room (Map of roomId -> List<RoomMessage>)
    val roomMessages: StateFlow<Map<String, List<RoomMessage>>> = roomMessageDao.getAllMessages()
        .map { list ->
            list.map { entity ->
                RoomMessage(
                    id = entity.id,
                    roomId = entity.roomId,
                    senderUsername = entity.senderUsername,
                    senderDisplayName = entity.senderDisplayName,
                    avatarRes = entity.avatarRes,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    isSystem = entity.isSystem,
                    isGameAlert = entity.isGameAlert,
                    isGiftAlert = entity.isGiftAlert,
                    giftName = entity.giftName,
                    giftIcon = entity.giftIcon,
                    adminBadgeId = entity.adminBadgeId,
                    nameColorHex = entity.nameColorHex
                )
            }.groupBy { it.roomId }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    // Direct Messages State backed by Room (Map of username -> List<DirectMessage>)
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = directMessageDao.getAllDirectMessages()
        .map { list ->
            list.map { entity ->
                DirectMessage(
                    id = entity.id,
                    conversationUser = entity.conversationUser,
                    senderUsername = entity.senderUsername,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    isFromMe = entity.isFromMe,
                    avatarRes = entity.avatarRes
                )
            }.groupBy { it.conversationUser }
        }
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    // Feed Posts State backed by Room
    val feedPosts: StateFlow<List<FeedPost>> = combine(
        feedDao.getAllPosts(),
        feedDao.getAllReplies()
    ) { posts, allReplies ->
        val repliesByPostId = allReplies.groupBy { it.postId }
        posts.map { postEntity ->
            val replies = (repliesByPostId[postEntity.id] ?: emptyList()).map { replyEntity ->
                PostReply(
                    id = replyEntity.id,
                    authorUsername = replyEntity.authorUsername,
                    authorDisplayName = replyEntity.authorDisplayName,
                    authorAvatarRes = replyEntity.authorAvatarRes,
                    replyToUser = replyEntity.replyToUser,
                    content = replyEntity.content,
                    timeAgo = replyEntity.timeAgo,
                    repliesCount = replyEntity.repliesCount,
                    shareCount = replyEntity.shareCount,
                    starCount = replyEntity.starCount,
                    isStarred = replyEntity.isStarred,
                    badges = listOf("A", "M")
                )
            }
            FeedPost(
                id = postEntity.id,
                authorUsername = postEntity.authorUsername,
                authorDisplayName = postEntity.authorDisplayName,
                authorAvatarRes = postEntity.authorAvatarRes,
                isVerified = postEntity.isVerified,
                tag = postEntity.tag,
                content = postEntity.content,
                imageRes = postEntity.imageRes,
                timeAgo = postEntity.timeAgo,
                repliesCount = maxOf(postEntity.repliesCount, replies.size),
                shareCount = postEntity.shareCount,
                starCount = postEntity.starCount,
                isStarred = postEntity.isStarred,
                badges = listOf("A", "M"),
                replies = replies
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Friends & Social Lists backed by Room
    val friends: StateFlow<List<String>> = contactDao.getFriends()
        .map { list -> list.map { it.username } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val followers: StateFlow<List<String>> = contactDao.getFollowers()
        .map { list -> list.map { it.username } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val following: StateFlow<List<String>> = contactDao.getFollowing()
        .map { list -> list.map { it.username } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Static badge and watchlist metadata
    val badgesList = listOf(
        Pair("🤖 Level 24 Master", "Achieved by active chatting and game rounds"),
        Pair("👑 VIP migGold Member", "Exclusive avatar outfits & badges unlocked"),
        Pair("⭐ Trivia Champion", "Won 50+ trivia rounds in Danger 1"),
        Pair("🎁 Gift Benefactor", "Sent over 20 virtual gifts to friends"),
        Pair("🔥 Top Influencer", "Published posts with over 100 stars"),
        Pair("🛡️ Room Guardian", "Verified active room moderator")
    )

    val watchList = listOf(
        "f1yingkit3", "b4sejump", "Danger 1 Room", "migCute Official"
    )

    // Virtual Gifts Catalog
    val availableGifts = listOf(
        VirtualGift("g1", "Red Rose", "🌹", 10, "Flowers", "Sweet fragrant rose"),
        VirtualGift("g2", "Teddy Bear", "🧸", 50, "Romantic", "Cute huggable teddy"),
        VirtualGift("g3", "mig33 Crown", "👑", 500, "Royalty", "VIP status crown on avatar"),
        VirtualGift("g4", "Love Heart", "💖", 25, "Romantic", "Glittering animated heart"),
        VirtualGift("g5", "Sports Car", "🏎️", 1000, "Luxury", "Fast luxury red speedster"),
        VirtualGift("g6", "Diamond Ring", "💍", 300, "Luxury", "Sparkling shiny gemstone"),
        VirtualGift("g7", "Birthday Cake", "🎂", 40, "Party", "Sweet triple-layer cake"),
        VirtualGift("g8", "Champagne", "🍾", 80, "Party", "Celebration toast"),
        VirtualGift("g9", "Super Hero Mask", "🎭", 150, "Fun", "Cool mysterious disguise"),
        VirtualGift("g10", "Golden Trophy", "🏆", 250, "Royalty", "Winner's golden cup")
    )

    // Classic Emoticons List
    val emoticonsList = listOf(
        EmoticonItem(":-)", "Smile", "😊"),
        EmoticonItem(";-)", "Wink", "😉"),
        EmoticonItem(":-D", "Laugh", "😃"),
        EmoticonItem(":-P", "Tongue", "😛"),
        EmoticonItem(":-*", "Kiss", "😘"),
        EmoticonItem(":-O", "Surprised", "😮"),
        EmoticonItem(":-S", "Worried", "😟"),
        EmoticonItem(":-(", "Sad", "😢"),
        EmoticonItem(":'-(", "Crying", "😭"),
        EmoticonItem(":-@", "Angry", "😡"),
        EmoticonItem("(h)", "Cool Heart", "❤️"),
        EmoticonItem("(k)", "Kiss lips", "💋"),
        EmoticonItem("(y)", "Thumbs Up", "👍"),
        EmoticonItem("(n)", "Thumbs Down", "👎"),
        EmoticonItem("(g)", "Gift", "🎁"),
        EmoticonItem("(c)", "Coffee", "☕"),
        EmoticonItem("(m)", "mig33", "🔷"),
        EmoticonItem("(star)", "Star", "⭐")
    )

    // Credit Transfers State backed by Room
    val creditTransfers: StateFlow<List<CreditTransfer>> = creditTransferDao.getAllTransfers()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Active Trivia Games in Rooms
    private val _activeTriviaRounds = MutableStateFlow<Map<String, ActiveTriviaRound>>(emptyMap())
    val activeTriviaRounds: StateFlow<Map<String, ActiveTriviaRound>> = _activeTriviaRounds.asStateFlow()

    init {
        scope.launch {
            seedDatabaseIfEmpty()
            startRoomSimulation()
            startAppUsageXPTracker()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        // 1. Seed User Profile if not present
        val existingUser = userDao.getUserProfileOnce()
        if (existingUser == null) {
            userDao.insertOrUpdate(UserProfileEntity.fromDomain(UserProfile()))
        } else if (existingUser.points < 10000 && existingUser.migLevel >= 20) {
            // Upgrade XP to match the new hardcore mig33 level curve
            val updated = existingUser.copy(points = 118450, migLevel = 24)
            userDao.insertOrUpdate(updated)
        }

        // 2. Seed Chat Rooms
        if (chatRoomDao.getRoomsCount() == 0) {
            val initialRooms = listOf(
                ChatRoomEntity("danger_1", "Danger 1", "What's Hot", 48, 50, true, true, "Danger 1 - Official Trivia & ChitChat! No spamming allowed.", true),
                ChatRoomEntity("game_warteg", "Game-warteg", "What's Hot", 2, 60, true, false, "Game warteg Indonesia seru & asyik 24 jam!"),
                ChatRoomEntity("kalidawirx", "kalidawirx", "What's Hot", 12, 50, true, false, "Salam santun kalidawir community"),
                ChatRoomEntity("bantul_seru", "BaNtuL..sErU", "What's Hot", 6, 50, true, false, "Bantul Jogja Istimewa Chatroom"),
                ChatRoomEntity("butwal_guz", "Butwal.guz", "What's Hot", 2, 25, true, false, "Welcome to Nepal Butwal friends"),
                ChatRoomEntity("dhivehin_queens", "dhivehin queens", "What's Hot", 3, 25, true, false, "Dhivehi Raajje Chatting zone"),
                ChatRoomEntity("fashion_show", "Fashion Show", "Play Games", 34, 50, false, true, "Show your best mig33 avatars & win gift awards!"),
                ChatRoomEntity("game_lobby", "Game Lobby", "Play Games", 28, 100, false, false, "[PVT] Play now: !start to enter dice & quiz!"),
                ChatRoomEntity("trivia_world", "Trivia Lounge", "Play Games", 19, 40, false, false, "Answer fast & climb the migLeaderboard!"),
                ChatRoomEntity("singapore_chill", "Singapore Cafe", "Your Favorites", 15, 30, false, true, "Chill out, coffee talk & weekend hangouts.")
            )
            chatRoomDao.insertRooms(initialRooms)
        }

        // 3. Seed Room Messages
        if (roomMessageDao.getMessageCount() == 0) {
            val initialRoomMessages = listOf(
                RoomMessageEntity("m1", "danger_1", "mig33_System", "", R.drawable.avatar_b4sejump, "CONGRATS! f1yingkit3 wins this round. Round ends. Next round in 10 seconds.", "11:32", isSystem = true, isGameAlert = true, createdMillis = 1000),
                RoomMessageEntity("m2", "danger_1", "f1yingkit3", "f1yingkit3", R.drawable.avatar_f1yingkit, "Yay! Got that question right in 3 seconds! 🏆", "11:32", createdMillis = 2000),
                RoomMessageEntity("m3", "danger_1", "b4sejump", "Axton Hale", R.drawable.avatar_b4sejump, "Haha good game! I was looking for my spectacles!", "11:33", createdMillis = 3000),
                RoomMessageEntity("m4", "danger_1", "t00fi3", "t00fi3", R.drawable.avatar_hipst4r, "Ready for the next round! Sending good vibes :-)", "11:33", createdMillis = 4000),
                RoomMessageEntity("gw1", "game_warteg", "kalidawirx", "kalidawirx", R.drawable.avatar_b4sejump, "Halo semuanya, selamat datang di warteg!", "11:20", createdMillis = 5000)
            )
            roomMessageDao.insertMessages(initialRoomMessages)
        }

        // 4. Seed Direct Messages
        if (directMessageDao.getDirectMessageCount() == 0) {
            val initialDMs = listOf(
                DirectMessageEntity("dm1", "f1yingkit3", "f1yingkit3", "Hi there! Want to play some games together?", "Oct-16-2012 11:32", false, R.drawable.avatar_f1yingkit, createdMillis = 1000),
                DirectMessageEntity("dm2", "f1yingkit3", "b4sejump", "Hi! Sure!", "11:33", true, R.drawable.avatar_b4sejump, createdMillis = 2000)
            )
            directMessageDao.insertDirectMessages(initialDMs)
        }

        // 5. Seed Feed Posts and Replies
        if (feedDao.getPostCount() == 0) {
            val initialPosts = listOf(
                FeedPostEntity("p1", "migCute", "migCute", R.drawable.mig33_app_icon, true, "#Cute", "#Cute - Where are my spectacles?! MEOW~", R.drawable.cat_glasses_meme, "3 hours ago via Web", 4, 6, 28, true, createdMillis = 3000),
                FeedPostEntity("p2", "hipst4r", "Fernando Tan", R.drawable.avatar_hipst4r, false, null, "Thinking of what to do this weekend...chillout at that new popular coffee joint?", null, "2 hours ago via Web", 0, 0, 12, false, createdMillis = 2000),
                FeedPostEntity("p3", "hipst4r", "Fernando Tan", R.drawable.avatar_hipst4r, false, null, "Am I trendy or am I trendy?", null, "4 hours ago via Web", 0, 2, 19, false, createdMillis = 1000)
            )
            feedDao.insertPosts(initialPosts)

            val initialReplies = listOf(
                PostReplyEntity("r1", "p1", "t00fi3", "t00fi3", R.drawable.avatar_f1yingkit, "f1yingkit3", "Haha! Yup!\n“Flight of the Valkyries” is always used as a generic background song during scenes of epic things happening...", "3 hours ago via Web", 1, 0, 0, false, createdMillis = 1000),
                PostReplyEntity("r2", "p1", "f1yingkit3", "f1yingkit3", R.drawable.avatar_f1yingkit, null, "“Flight of the Valkyries” is always used as a generic background song during scenes of epic things happening...", "3 hours ago via Web", 1, 0, 0, false, createdMillis = 2000),
                PostReplyEntity("r3", "p1", "b4sejump", "Axton Hale", R.drawable.avatar_b4sejump, null, "LOL! Silly kitty doesn’t know that it’s looking right at it...but then again, it’s usually that way isn’t it?", "2 hours ago via Web", 0, 0, 0, false, createdMillis = 3000),
                PostReplyEntity("r4", "p1", "b4sejump", "Axton Hale", R.drawable.avatar_b4sejump, null, "Definitely! 100%!", "2 hours ago via Web", 0, 0, 0, false, createdMillis = 4000)
            )
            feedDao.insertReplies(initialReplies)
        }

        // 6. Seed Contacts
        if (contactDao.getContactCount() == 0) {
            val initialContacts = listOf(
                ContactEntity("f1yingkit3", isFriend = true, isFollower = true, isFollowing = true),
                ContactEntity("b4sejump", isFriend = true, isFollower = true, isFollowing = false),
                ContactEntity("t00fi3", isFriend = true, isFollower = true, isFollowing = false),
                ContactEntity("kalidawirx", isFriend = true, isFollower = true, isFollowing = true),
                ContactEntity("migCute", isFriend = true, isFollower = true, isFollowing = true),
                ContactEntity("dhivehi_fan", isFriend = false, isFollower = true, isFollowing = false),
                ContactEntity("seru_boy", isFriend = false, isFollower = true, isFollowing = false)
            )
            contactDao.insertContacts(initialContacts)
        }

        // 7. Seed Credit Transfers
        if (creditTransferDao.getTransfersCount() == 0) {
            val initialTransfers = listOf(
                CreditTransferEntity(
                    senderUsername = "f1yingkit3",
                    receiverUsername = "hipst4r",
                    amount = 150,
                    note = "Thanks for the room trivia game! 🎉",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    status = "SUCCESS"
                ),
                CreditTransferEntity(
                    senderUsername = "hipst4r",
                    receiverUsername = "b4sejump",
                    amount = 50,
                    note = "Welcome to mig33!",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    status = "SUCCESS"
                )
            )
            initialTransfers.forEach { creditTransferDao.insertTransfer(it) }
        }
    }

    private fun startRoomSimulation() {
        scope.launch {
            val botMessages = listOf(
                Pair("f1yingkit3", "Anybody up for next trivia round?"),
                Pair("kalidawirx", "Game-warteg room is also rocking!"),
                Pair("t00fi3", "Who is sending the next gift shower? 🎁"),
                Pair("migCute", "Welcome newcomers to mig33 Danger 1 room!"),
                Pair("b4sejump", "Check out my new avatar haircut from Avatar Shop!")
            )
            var index = 0
            while (true) {
                delay(12000)
                val rooms = chatRooms.value
                val activeRoom = rooms.find { it.id == "danger_1" } ?: rooms.firstOrNull()
                if (activeRoom != null) {
                    val bot = botMessages[index % botMessages.size]
                    index++
                    val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    val msgEntity = RoomMessageEntity(
                        id = UUID.randomUUID().toString(),
                        roomId = activeRoom.id,
                        senderUsername = bot.first,
                        senderDisplayName = bot.first,
                        avatarRes = if (bot.first == "f1yingkit3") R.drawable.avatar_f1yingkit else R.drawable.avatar_b4sejump,
                        text = bot.second,
                        timestamp = timeString,
                        createdMillis = System.currentTimeMillis()
                    )
                    roomMessageDao.insertMessage(msgEntity)
                }
            }
        }
    }

    private fun startAppUsageXPTracker() {
        scope.launch {
            while (true) {
                delay(300000L) // Every 5 minutes of active app usage (+1 XP -> 12 XP per hour)
                if (_isLoggedIn.value) {
                    val current = userDao.getUserProfileOnce()
                    if (current != null && current.isOnline) {
                        val newPoints = current.points + 1 // +12 XP per hour of active online time
                        val calculatedLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
                        userDao.insertOrUpdate(current.copy(points = newPoints, migLevel = calculatedLevel))
                    }
                }
            }
        }
    }

    fun setUserProfile(username: String, invisible: Boolean) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val updated = current.copy(
                username = username,
                displayName = if (username == "hipst4r") "Fernando Tan" else if (username == "b4sejump") "Axton Hale" else username,
                isInvisible = invisible,
                isOnline = !invisible
            )
            userDao.insertOrUpdate(updated)
            _isLoggedIn.value = true
        }
    }

    fun addCredits(amount: Int) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newCredits = current.credits + amount
            val newPoints = current.points + (amount / 2)
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(credits = newCredits, points = newPoints, migLevel = newLevel))
        }
    }

    fun deductCredits(amount: Int): Boolean {
        val current = userProfile.value
        if (current.credits < amount) return false
        scope.launch {
            val user = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (user.credits >= amount) {
                userDao.insertOrUpdate(user.copy(credits = user.credits - amount))
            }
        }
        return true
    }

    suspend fun transferCredits(
        recipientUsername: String,
        amount: Int,
        pin: String,
        note: String = ""
    ): com.example.model.TransferResult {
        val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
        val cleanRecipient = recipientUsername.trim().removePrefix("@")
        
        if (cleanRecipient.isEmpty()) {
            return com.example.model.TransferResult.Error("يرجى إدخال اسم مستخدم المستلم!")
        }
        if (cleanRecipient.equals(current.username, ignoreCase = true)) {
            return com.example.model.TransferResult.Error("لا يمكنك تحويل الكريدت إلى حسابك الخاص!")
        }
        if (amount <= 0) {
            return com.example.model.TransferResult.Error("يرجى إدخال مبلغ كريدت صحيح أكبر من 0!")
        }
        if (current.credits < amount) {
            return com.example.model.TransferResult.Error("رصيد الكريدت لديك غير كافٍ! (رصيدك الحالي: ${current.credits} cr)")
        }
        if (current.securityPin.isNotEmpty() && current.securityPin != pin) {
            return com.example.model.TransferResult.Error("رمز الأمان السري (PIN) غير صحيح!")
        }

        // Deduct from current user and add bonus XP points for social activity
        val newCredits = current.credits - amount
        val bonusPoints = current.points + (amount / 5) // XP for sending credits
        val newLevel = com.example.model.MigLevelCalculator.calculateProgression(bonusPoints.toLong()).currentLevel
        userDao.insertOrUpdate(current.copy(credits = newCredits, points = bonusPoints, migLevel = newLevel))

        // Record the transaction
        val transferEntity = CreditTransferEntity(
            senderUsername = current.username,
            receiverUsername = cleanRecipient,
            amount = amount,
            note = note.ifEmpty { "Credit transfer" },
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS"
        )
        val id = creditTransferDao.insertTransfer(transferEntity)
        val domainTransfer = transferEntity.copy(id = id).toDomain()

        // Send a direct message confirmation to recipient if desired
        sendDirectMessage(
            recipientUsername = cleanRecipient,
            text = "💰 [mig33 Bank] لقد قمت بتحويل $amount كريدت إليك! ${if (note.isNotEmpty()) "الرسالة: \"$note\"" else ""}"
        )

        return com.example.model.TransferResult.Success(domainTransfer, newCredits)
    }

    suspend fun updateSecurityPin(oldPin: String, newPin: String): Boolean {
        val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
        if (current.securityPin.isNotEmpty() && current.securityPin != oldPin) {
            return false
        }
        if (newPin.length < 4) {
            return false
        }
        userDao.updateSecurityPin(newPin)
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateAvatarConfig(newConfig: AvatarConfig) {
        scope.launch {
            userDao.updateAvatar(
                presetId = newConfig.presetId,
                customImageRes = newConfig.customImageRes,
                bg = newConfig.background,
                pet = newConfig.pet,
                base = newConfig.base,
                clothes = newConfig.clothes,
                hair = newConfig.hair,
                accessory = newConfig.accessory
            )
        }
    }

    fun updateAvatar(
        background: String,
        pet: String,
        base: String,
        clothes: String,
        hair: String,
        accessory: String
    ) {
        scope.launch {
            userDao.updateAvatar(
                presetId = "custom",
                customImageRes = null,
                bg = background,
                pet = pet,
                base = base,
                clothes = clothes,
                hair = hair,
                accessory = accessory
            )
        }
    }

    fun updateProfile(displayName: String, status: String, gender: String, country: String) {
        scope.launch {
            userDao.updateProfile(displayName, status, gender, country)
        }
    }

    fun toggleEquipBadge(badgeKey: String) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val currentList = current.equippedBadgeNumbers
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toMutableList()

            if (currentList.contains(badgeKey)) {
                currentList.remove(badgeKey)
            } else {
                currentList.clear()
                currentList.add(badgeKey)
            }
            val updated = current.copy(
                equippedBadgeNumbers = currentList.joinToString(","),
                badgesCount = currentList.size
            )
            userDao.insertOrUpdate(updated)
        }
    }

    fun sendRoomMessage(roomId: String, text: String) {
        if (text.isBlank()) return
        scope.launch {
            val user = userProfile.value
            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val userAvatar = when (user.avatarConfig.presetId) {
                "mig_shadow_tuxedo" -> R.drawable.img_avatar_shadow_tuxedo
                "mig_blood_moon" -> R.drawable.img_avatar_blood_moon
                else -> user.avatarConfig.customImageRes ?: R.drawable.img_avatar_shadow_tuxedo
            }
            val newMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = user.username,
                senderDisplayName = user.displayName,
                avatarRes = userAvatar,
                text = text,
                timestamp = timeString,
                adminBadgeId = user.equippedBadgeNumbers.firstOrNull { it != "000" && !it.startsWith("lvl_") }, // attach equipped admin badge
                nameColorHex = AdminSettings.getNameColorForBadge(user.equippedBadgeNumbers.firstOrNull { it != "000" && !it.startsWith("lvl_") }),
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(newMsg)

            // Award XP for chatting
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newPoints = current.points + 8
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(points = newPoints, migLevel = newLevel))

            // Handle Bot Commands if text starts with '!'
            if (text.startsWith("!")) {
                handleBotCommand(roomId, text.trim(), user)
            }
        }
    }

    private suspend fun handleBotCommand(roomId: String, commandText: String, user: UserProfile) {
        val parts = commandText.split("\\s+".toRegex())
        val cmd = parts[0].lowercase()
        val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        when (cmd) {
            "!help", "!games", "!commands" -> {
                delay(400)
                val botHelpMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 migBot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "🎮 **mig33 Room Games Bot**:\n" +
                            "• `!dice [bet]` - Roll 2 dice vs migBot (e.g. `!dice 50`)\n" +
                            "• `!trivia` - Start a Trivia Quiz round with credit prize!\n" +
                            "• `!ans [1-4]` - Answer active Trivia question\n" +
                            "• `!flip [heads/tails] [bet]` - Coin flip duel\n" +
                            "• `!lucky7 [low/7/high] [bet]` - Bet on dice roll sum\n" +
                            "• `!credits` - Check your wallet balance\n" +
                            "• `!rules` - Game payout rules",
                    timestamp = timeString,
                    isGameAlert = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(botHelpMsg)
            }

            "!rules" -> {
                delay(400)
                val rulesMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 migBot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "📜 **Game Payout Rules**:\n" +
                            "🎲 Dice: Win = 2x Bet + 15 XP | Tie = Bet Refunded\n" +
                            "🪙 Coin Flip: Win = 2x Bet + 20 XP\n" +
                            "🎰 Lucky 7: Low/High = 2x Bet | Lucky 7 = 4x Bet! 🔥\n" +
                            "🧠 Trivia: First correct answer wins the round jackpot!",
                    timestamp = timeString,
                    isGameAlert = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(rulesMsg)
            }

            "!credits", "!balance" -> {
                delay(400)
                val cur = userDao.getUserProfileOnce() ?: UserProfileEntity()
                val balMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 migBot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "🪙 @${user.username}, your current balance is **${cur.credits} credits** (Level ${cur.migLevel} • ${cur.points} XP).",
                    timestamp = timeString,
                    isSystem = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(balMsg)
            }

            "!dice", "!roll" -> {
                val bet = parts.getOrNull(1)?.toIntOrNull() ?: 0
                playDiceGame(roomId, bet)
            }

            "!trivia", "!quiz" -> {
                startTriviaGame(roomId)
            }

            "!ans", "!answer" -> {
                val optionNum = parts.getOrNull(1)?.toIntOrNull()
                if (optionNum != null && optionNum in 1..4) {
                    submitTriviaAnswer(roomId, optionNum - 1)
                } else {
                    val errMsg = RoomMessageEntity(
                        id = UUID.randomUUID().toString(),
                        roomId = roomId,
                        senderUsername = "migBot",
                        senderDisplayName = "🤖 migBot",
                        avatarRes = R.drawable.avatar_b4sejump,
                        text = "⚠️ Usage: `!ans [1-4]` (e.g. `!ans 1` to choose Option 1)",
                        timestamp = timeString,
                        isSystem = true,
                        createdMillis = System.currentTimeMillis()
                    )
                    roomMessageDao.insertMessage(errMsg)
                }
            }

            "!flip", "!coin" -> {
                val choice = parts.getOrNull(1)?.uppercase() ?: "HEADS"
                val bet = parts.getOrNull(2)?.toIntOrNull() ?: 10
                val validChoice = if (choice.startsWith("T") || choice == "كتابة") "TAILS" else "HEADS"
                playCoinFlip(roomId, validChoice, bet)
            }

            "!lucky7" -> {
                val choice = parts.getOrNull(1)?.uppercase() ?: "7"
                val bet = parts.getOrNull(2)?.toIntOrNull() ?: 20
                playLucky7(roomId, choice, bet)
            }
        }
    }

    // --- Interactive Room Games Engine ---

    fun startTriviaGame(roomId: String) {
        scope.launch {
            val existing = _activeTriviaRounds.value[roomId]
            if (existing != null && !existing.isAnswered && System.currentTimeMillis() < existing.endMillis) {
                // Already active
                val msg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Trivia Bot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "⏳ A Trivia round is already in progress! Answer before timer expires!",
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    isGameAlert = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(msg)
                return@launch
            }

            val question = TriviaQuestionsBank.getRandomQuestion()
            val newRound = ActiveTriviaRound(
                roomId = roomId,
                question = question,
                startMillis = System.currentTimeMillis(),
                endMillis = System.currentTimeMillis() + (question.timeLimitSeconds * 1000L)
            )

            val updatedMap = _activeTriviaRounds.value.toMutableMap()
            updatedMap[roomId] = newRound
            _activeTriviaRounds.value = updatedMap

            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val optionsFormatted = question.options.mapIndexed { idx, opt -> "  [${idx + 1}] $opt" }.joinToString("\n")
            val questionMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = "migBot",
                senderDisplayName = "🤖 Trivia Master",
                avatarRes = R.drawable.avatar_b4sejump,
                text = "🧠 **[TRIVIA ROUND STARTED]** (Prize: ${question.prizeCredits} cr)\n" +
                        "Category: ${question.category}\n\n" +
                        "❓ **${question.question}**\n\n" +
                        optionsFormatted + "\n\n" +
                        "👉 Type `!ans [1-4]` or tap the buttons below! Time: ${question.timeLimitSeconds}s",
                timestamp = timeString,
                isGameAlert = true,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(questionMsg)

            // Timer for round expiry
            delay(question.timeLimitSeconds * 1000L)
            val currentRound = _activeTriviaRounds.value[roomId]
            if (currentRound != null && !currentRound.isAnswered && currentRound.question.id == question.id) {
                val correctText = question.options[question.correctOptionIndex]
                val timeoutMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Trivia Master",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "⏰ **Time's Up!** No one answered in time.\n" +
                            "✅ Correct Answer was: [${question.correctOptionIndex + 1}] $correctText\n" +
                            "Next round starting soon! Type `!trivia` to play again.",
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    isGameAlert = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(timeoutMsg)

                val mapCopy = _activeTriviaRounds.value.toMutableMap()
                mapCopy.remove(roomId)
                _activeTriviaRounds.value = mapCopy
            }
        }
    }

    fun submitTriviaAnswer(roomId: String, optionIndex: Int): Boolean {
        var isWinner = false
        val round = _activeTriviaRounds.value[roomId] ?: return false
        if (round.isAnswered || System.currentTimeMillis() > round.endMillis) return false

        scope.launch {
            val user = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            if (optionIndex == round.question.correctOptionIndex) {
                isWinner = true
                val prize = round.question.prizeCredits
                val newCredits = user.credits + prize
                val newPoints = user.points + 25
                val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
                userDao.insertOrUpdate(user.copy(credits = newCredits, points = newPoints, migLevel = newLevel))

                val updatedRound = round.copy(isAnswered = true, winnerUsername = user.username)
                val mapCopy = _activeTriviaRounds.value.toMutableMap()
                mapCopy[roomId] = updatedRound
                _activeTriviaRounds.value = mapCopy

                val winMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Trivia Master",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "🎉 **BINGO! WINNER!** 🏆\n" +
                            "@${user.username} answered correctly: \"${round.question.options[optionIndex]}\"!\n" +
                            "💰 Awarded: **+$prize Credits** & **+25 XP**! Current credits: $newCredits cr",
                    timestamp = timeString,
                    isGameAlert = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(winMsg)
            } else {
                val selectedOption = round.question.options.getOrNull(optionIndex) ?: "Choice ${optionIndex + 1}"
                val wrongMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Trivia Master",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "❌ @${user.username} chose \"$selectedOption\" - Wrong answer! Keep trying!",
                    timestamp = timeString,
                    isSystem = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(wrongMsg)
            }
        }
        return isWinner
    }

    fun playDiceGame(roomId: String, betAmount: Int = 0): DiceGameResult {
        val user = userProfile.value
        val diceIcons = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

        val p1 = (1..6).random()
        val p2 = (1..6).random()
        val pTotal = p1 + p2

        val b1 = (1..6).random()
        val b2 = (1..6).random()
        val bTotal = b1 + b2

        val outcome = when {
            pTotal > bTotal -> DiceOutcome.WIN
            pTotal < bTotal -> DiceOutcome.LOSE
            else -> DiceOutcome.TIE
        }

        val wonAmount = when (outcome) {
            DiceOutcome.WIN -> if (betAmount > 0) betAmount else 15
            DiceOutcome.LOSE -> if (betAmount > 0) -betAmount else 0
            DiceOutcome.TIE -> 0
        }

        val result = DiceGameResult(
            playerUsername = user.username,
            playerDie1 = p1,
            playerDie2 = p2,
            playerTotal = pTotal,
            botDie1 = b1,
            botDie2 = b2,
            botTotal = bTotal,
            betAmount = betAmount,
            wonAmount = wonAmount,
            outcome = outcome
        )

        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (betAmount > 0 && current.credits < betAmount) {
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val errorMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Dice Bot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "⚠️ @${user.username} you don't have enough credits to bet $betAmount cr! (Your balance: ${current.credits} cr)",
                    timestamp = timeString,
                    isSystem = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(errorMsg)
                return@launch
            }

            val newCredits = current.credits + wonAmount
            val bonusXP = when (outcome) {
                DiceOutcome.WIN -> 20 + (betAmount / 10)
                DiceOutcome.TIE -> 8
                DiceOutcome.LOSE -> 5
            }
            val newPoints = current.points + bonusXP
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(credits = newCredits, points = newPoints, migLevel = newLevel))

            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val pVisual = "${diceIcons[p1 - 1]} ${diceIcons[p2 - 1]} ($pTotal)"
            val bVisual = "${diceIcons[b1 - 1]} ${diceIcons[b2 - 1]} ($bTotal)"

            val resultText = when (outcome) {
                DiceOutcome.WIN -> "🎉 **YOU WON!** 🎲\n" +
                        "@${user.username}: $pVisual vs 🤖 migBot: $bVisual\n" +
                        "💰 Payout: **+$wonAmount Credits** & **+$bonusXP XP**! (Balance: $newCredits cr)"
                DiceOutcome.LOSE -> "💥 **migBot Won!** 🎲\n" +
                        "@${user.username}: $pVisual vs 🤖 migBot: $bVisual\n" +
                        "${if (betAmount > 0) "💸 Lost: -$betAmount Credits" else "Better luck next roll!"} (Balance: $newCredits cr)"
                DiceOutcome.TIE -> "🤝 **It's a TIE!** 🎲\n" +
                        "@${user.username}: $pVisual vs 🤖 migBot: $bVisual\n" +
                        "Bet refunded +8 XP bonus."
            }

            val diceMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = "migBot",
                senderDisplayName = "🤖 Dice Master",
                avatarRes = R.drawable.avatar_b4sejump,
                text = resultText,
                timestamp = timeString,
                isGameAlert = outcome == DiceOutcome.WIN,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(diceMsg)
        }

        return result
    }

    fun playCoinFlip(roomId: String, choice: String, betAmount: Int = 10): CoinFlipResult {
        val user = userProfile.value
        val outcome = if ((0..1).random() == 0) "HEADS" else "TAILS"
        val isWin = choice.equals(outcome, ignoreCase = true)
        val wonAmount = if (isWin) betAmount else -betAmount

        val result = CoinFlipResult(
            playerUsername = user.username,
            playerChoice = choice,
            outcome = outcome,
            betAmount = betAmount,
            wonAmount = wonAmount,
            isWin = isWin
        )

        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (current.credits < betAmount) {
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val errorMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Flip Bot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "⚠️ @${user.username} insufficient credits to bet $betAmount cr!",
                    timestamp = timeString,
                    isSystem = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(errorMsg)
                return@launch
            }

            val newCredits = current.credits + wonAmount
            val bonusXP = if (isWin) 20 else 5
            val newPoints = current.points + bonusXP
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(credits = newCredits, points = newPoints, migLevel = newLevel))

            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val coinEmoji = if (outcome == "HEADS") "👑 (HEADS/ملك)" else "🦅 (TAILS/كتابة)"

            val text = if (isWin) {
                "🪙 **COIN FLIP WIN!** 🎉\n" +
                        "Coin landed on: $coinEmoji!\n" +
                        "@${user.username} picked $choice and WON **+$betAmount Credits**! (Balance: $newCredits cr)"
            } else {
                "🪙 **COIN FLIP LOST!** 💥\n" +
                        "Coin landed on: $coinEmoji!\n" +
                        "@${user.username} picked $choice and lost -$betAmount cr. (Balance: $newCredits cr)"
            }

            val flipMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = "migBot",
                senderDisplayName = "🤖 Coin Master",
                avatarRes = R.drawable.avatar_b4sejump,
                text = text,
                timestamp = timeString,
                isGameAlert = isWin,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(flipMsg)
        }

        return result
    }

    fun playLucky7(roomId: String, choice: String, betAmount: Int = 20): Lucky7Result {
        val user = userProfile.value
        val d1 = (1..6).random()
        val d2 = (1..6).random()
        val total = d1 + d2

        val isWin = when (choice.uppercase()) {
            "LOW", "<7" -> total < 7
            "7", "LUCKY7", "=7" -> total == 7
            "HIGH", ">7" -> total > 7
            else -> false
        }

        val multiplier = if (choice.uppercase() in listOf("7", "LUCKY7", "=7")) 4 else 2
        val wonAmount = if (isWin) betAmount * (multiplier - 1) else -betAmount

        val result = Lucky7Result(
            playerUsername = user.username,
            choice = choice,
            die1 = d1,
            die2 = d2,
            total = total,
            betAmount = betAmount,
            wonAmount = wonAmount,
            isWin = isWin
        )

        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (current.credits < betAmount) {
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val errorMsg = RoomMessageEntity(
                    id = UUID.randomUUID().toString(),
                    roomId = roomId,
                    senderUsername = "migBot",
                    senderDisplayName = "🤖 Lucky 7 Bot",
                    avatarRes = R.drawable.avatar_b4sejump,
                    text = "⚠️ @${user.username} you need at least $betAmount cr to play Lucky 7!",
                    timestamp = timeString,
                    isSystem = true,
                    createdMillis = System.currentTimeMillis()
                )
                roomMessageDao.insertMessage(errorMsg)
                return@launch
            }

            val newCredits = current.credits + wonAmount
            val bonusXP = if (isWin) 30 else 5
            val newPoints = current.points + bonusXP
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(credits = newCredits, points = newPoints, migLevel = newLevel))

            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val text = if (isWin) {
                "🎰 **LUCKY 7 JACKPOT WIN!** 🔥\n" +
                        "Dice rolled: [$d1 + $d2 = **$total**]\n" +
                        "@${user.username} predicted $choice ($multiplier X payout) and WON **+$wonAmount Credits**! 🎉 (Balance: $newCredits cr)"
            } else {
                "🎰 **LUCKY 7 LOST!** 💥\n" +
                        "Dice rolled: [$d1 + $d2 = **$total**]\n" +
                        "@${user.username} predicted $choice and lost -$betAmount cr. (Balance: $newCredits cr)"
            }

            val msg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = "migBot",
                senderDisplayName = "🎰 Lucky 7 Bot",
                avatarRes = R.drawable.avatar_b4sejump,
                text = text,
                timestamp = timeString,
                isGameAlert = isWin,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(msg)
        }

        return result
    }

    fun sendVirtualGift(roomId: String, gift: VirtualGift, recipientUsername: String) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (current.credits < gift.priceCredits) return@launch

            val newPoints = current.points + 35
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            val updated = current.copy(
                credits = current.credits - gift.priceCredits,
                points = newPoints,
                migLevel = newLevel,
                giftsCount = current.giftsCount + 1
            )
            userDao.insertOrUpdate(updated)

            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val userAvatar = when (current.avatarPresetId) {
                "mig_shadow_tuxedo" -> R.drawable.img_avatar_shadow_tuxedo
                "mig_blood_moon" -> R.drawable.img_avatar_blood_moon
                else -> current.avatarCustomImageRes ?: R.drawable.img_avatar_shadow_tuxedo
            }
            val giftMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = current.username,
                senderDisplayName = current.displayName,
                avatarRes = userAvatar,
                text = "sent a ${gift.name} ${gift.emoji} to $recipientUsername!",
                timestamp = timeString,
                isGiftAlert = true,
                giftName = gift.name,
                giftIcon = gift.emoji,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(giftMsg)
        }
    }

    fun sendDirectMessage(recipientUsername: String, text: String) {
        if (text.isBlank()) return
        scope.launch {
            val user = userProfile.value
            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val userAvatar = when (user.avatarConfig.presetId) {
                "mig_shadow_tuxedo" -> R.drawable.img_avatar_shadow_tuxedo
                "mig_blood_moon" -> R.drawable.img_avatar_blood_moon
                else -> user.avatarConfig.customImageRes ?: R.drawable.img_avatar_shadow_tuxedo
            }
            val newMsg = DirectMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationUser = recipientUsername,
                senderUsername = user.username,
                text = text,
                timestamp = timeString,
                isFromMe = true,
                avatarRes = userAvatar,
                createdMillis = System.currentTimeMillis()
            )
            directMessageDao.insertDirectMessage(newMsg)

            // Simulate realistic bot reply
            delay(1500)
            val replyText = when (recipientUsername) {
                "f1yingkit3" -> listOf(
                    "Awesome! Let's meet in Danger 1 room now ^_^",
                    "Haha so true! Did you customize your avatar?",
                    "Let's play another round of trivia!"
                ).random()
                "b4sejump" -> listOf(
                    "Hey! Checking out the feeds right now.",
                    "Nice! That avatar looks sharp.",
                    "Catch you later in the chat rooms!"
                ).random()
                "t00fi3" -> listOf(
                    "LOL exactly! :-D",
                    "Thanks for the message! (h)",
                    "See you in Danger 1!"
                ).random()
                "kalidawirx" -> listOf(
                    "Mantap! Salam santun selalu.",
                    "Siap, terima kasih temanku!",
                    "Yuk mampir ke room kalidawirx."
                ).random()
                else -> "Thanks for reaching out on mig33! (y)"
            }

            val reply = DirectMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationUser = recipientUsername,
                senderUsername = recipientUsername,
                text = replyText,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isFromMe = false,
                avatarRes = if (recipientUsername == "f1yingkit3") R.drawable.avatar_f1yingkit else if (recipientUsername == "b4sejump") R.drawable.avatar_b4sejump else R.drawable.avatar_hipst4r,
                createdMillis = System.currentTimeMillis()
            )
            directMessageDao.insertDirectMessage(reply)
        }
    }

    fun addFeedPost(content: String, tag: String? = null, imageRes: Int? = null) {
        if (content.isBlank()) return
        scope.launch {
            val user = userProfile.value
            val formattedContent = if (tag != null && !content.startsWith("#")) "$tag $content" else content
            val newPost = FeedPostEntity(
                id = UUID.randomUUID().toString(),
                authorUsername = user.username,
                authorDisplayName = user.displayName,
                authorAvatarRes = R.drawable.avatar_hipst4r,
                tag = tag,
                content = formattedContent,
                imageRes = imageRes,
                timeAgo = "Just now via Android",
                repliesCount = 0,
                shareCount = 0,
                starCount = 0,
                isStarred = false,
                createdMillis = System.currentTimeMillis()
            )
            feedDao.insertPost(newPost)

            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newPoints = current.points + 25
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            val updated = current.copy(
                postsCount = current.postsCount + 1,
                points = newPoints,
                migLevel = newLevel
            )
            userDao.insertOrUpdate(updated)
        }
    }

    fun sharePost(postId: String) {
        scope.launch {
            feedDao.incrementShare(postId)
        }
    }

    fun kickUserFromRoom(roomId: String, targetUser: String) {
        scope.launch {
            val user = userProfile.value
            val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val kickMsg = RoomMessageEntity(
                id = UUID.randomUUID().toString(),
                roomId = roomId,
                senderUsername = "mig33_Moderator",
                text = "⚡ User '$targetUser' was kicked by Moderator @${user.username} for violating room rules.",
                timestamp = timeString,
                isSystem = true,
                isGameAlert = true,
                createdMillis = System.currentTimeMillis()
            )
            roomMessageDao.insertMessage(kickMsg)
        }
    }

    fun refreshRooms() {
        scope.launch {
            val currentRooms = chatRooms.value
            currentRooms.forEach { room ->
                val delta = (-3..5).random()
                val newCount = (room.usersCount + delta).coerceIn(1, room.maxUsers)
                chatRoomDao.updateUsersCount(room.id, newCount)
            }
        }
    }

    fun addFriend(username: String) {
        scope.launch {
            contactDao.insertContact(
                ContactEntity(
                    username = username,
                    isFriend = true,
                    isFollower = true,
                    isFollowing = true
                )
            )
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            userDao.insertOrUpdate(current.copy(friendsCount = current.friendsCount + 1))
        }
    }

    fun removeFriend(username: String) {
        scope.launch {
            contactDao.deleteContact(username)
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            userDao.insertOrUpdate(current.copy(friendsCount = maxOf(0, current.friendsCount - 1)))
        }
    }

    fun toggleStarPost(postId: String) {
        scope.launch {
            val post = feedPosts.value.find { it.id == postId } ?: return@launch
            val newStarred = !post.isStarred
            val newCount = if (newStarred) post.starCount + 1 else maxOf(0, post.starCount - 1)
            feedDao.updatePostStar(postId, newStarred, newCount)
        }
    }

    fun addPostReply(postId: String, text: String) {
        if (text.isBlank()) return
        scope.launch {
            val user = userProfile.value
            val reply = PostReplyEntity(
                id = UUID.randomUUID().toString(),
                postId = postId,
                authorUsername = user.username,
                authorDisplayName = user.displayName,
                authorAvatarRes = R.drawable.avatar_b4sejump,
                content = text,
                timeAgo = "Just now",
                createdMillis = System.currentTimeMillis()
            )
            feedDao.insertReply(reply)
            feedDao.incrementRepliesCount(postId)

            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newPoints = current.points + 15
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(points = newPoints, migLevel = newLevel))
        }
    }

    fun claimDailyBonus(): Int {
        val bonus = 100
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newPoints = current.points + 50
            val newLevel = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong()).currentLevel
            userDao.insertOrUpdate(current.copy(credits = current.credits + bonus, points = newPoints, migLevel = newLevel))
        }
        return bonus
    }

    fun toggleFavoriteRoom(roomId: String) {
        scope.launch {
            chatRoomDao.toggleFavorite(roomId)
        }
    }

    fun updateFullProfile(
        displayName: String,
        statusMessage: String,
        gender: String,
        birthday: String,
        country: String,
        isOnline: Boolean,
        isInvisible: Boolean
    ) {
        scope.launch {
            userDao.updateFullProfile(
                displayName = displayName,
                status = statusMessage,
                gender = gender,
                birthday = birthday,
                country = country,
                isOnline = isOnline,
                isInvisible = isInvisible
            )
        }
    }

    fun saveUserProfile(
        displayName: String,
        statusMessage: String,
        bio: String,
        gender: String,
        birthday: String,
        country: String,
        city: String,
        relationshipStatus: String,
        hobbies: String,
        isInvisible: Boolean,
        equippedBadgeNumbers: List<String>
    ) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val updated = current.copy(
                displayName = displayName,
                statusMessage = statusMessage,
                bio = bio,
                gender = gender,
                birthday = birthday,
                country = country,
                city = city,
                relationshipStatus = relationshipStatus,
                hobbies = hobbies,
                isInvisible = isInvisible,
                equippedBadgeNumbers = equippedBadgeNumbers.joinToString(",")
            )
            userDao.insertOrUpdate(updated)
        }
    }

    private val _bannedUsers = MutableStateFlow<List<com.example.model.BannedUser>>(
        listOf(
            com.example.model.BannedUser("spammer99", "Repeated room advertisement spam", 24, "Today 10:15 AM"),
            com.example.model.BannedUser("troll_x", "Abusive language in public rooms", 72, "Yesterday 08:30 PM")
        )
    )
    val bannedUsers: StateFlow<List<com.example.model.BannedUser>> = _bannedUsers.asStateFlow()

    private val _adminLogs = MutableStateFlow<List<com.example.model.AdminActionLog>>(
        listOf(
            com.example.model.AdminActionLog("1", "BAN", "spammer99", "Banned for 24h: Spamming", "10:15 AM"),
            com.example.model.AdminActionLog("2", "KICK", "room_intruder", "Kicked from #Danger1 for 15 mins", "09:40 AM")
        )
    )
    val adminLogs: StateFlow<List<com.example.model.AdminActionLog>> = _adminLogs.asStateFlow()

    fun kickUser(targetUsername: String, reason: String, durationMinutes: Int) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "KICK",
            targetUsername = targetUsername,
            details = "Kicked (${durationMinutes}m): $reason",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun increaseUserLevel(targetUsername: String, levelsToAdd: Int) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (targetUsername.equals(current.username, ignoreCase = true) || targetUsername.isBlank()) {
                val targetLevel = current.migLevel + levelsToAdd
                val targetXP = com.example.model.MigLevelCalculator.xpRequiredForLevel(targetLevel)
                userDao.insertOrUpdate(current.copy(migLevel = targetLevel, points = maxOf(current.points, targetXP.toInt())))
            }
        }
        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "LEVEL_UP",
            targetUsername = targetUsername,
            details = "+$levelsToAdd Level(s) granted",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun grantBadgeToUser(targetUsername: String, badgeKey: String) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            if (targetUsername.equals(current.username, ignoreCase = true) || targetUsername.isBlank()) {
                val currentEq = current.equippedBadgeNumbers.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                if (!currentEq.contains(badgeKey)) {
                    currentEq.add(badgeKey)
                    userDao.insertOrUpdate(current.copy(equippedBadgeNumbers = currentEq.joinToString(",")))
                }
            }
        }
        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "GRANT_BADGE",
            targetUsername = targetUsername,
            details = "Granted Badge #$badgeKey",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun banUser(targetUsername: String, reason: String, durationHours: Int) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val banned = com.example.model.BannedUser(
            username = targetUsername,
            reason = reason,
            durationHours = durationHours,
            bannedAt = timeStr
        )
        _bannedUsers.value = listOf(banned) + _bannedUsers.value.filterNot { it.username.equals(targetUsername, ignoreCase = true) }

        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "BAN",
            targetUsername = targetUsername,
            details = "Banned (${durationHours}h): $reason",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun sendNoticeToUser(targetUsername: String, noticeMessage: String) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "NOTICE",
            targetUsername = targetUsername,
            details = "Sent Notice: \"$noticeMessage\"",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun unbanUser(targetUsername: String) {
        val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        _bannedUsers.value = _bannedUsers.value.filterNot { it.username.equals(targetUsername, ignoreCase = true) }

        val log = com.example.model.AdminActionLog(
            id = UUID.randomUUID().toString(),
            actionType = "UNBAN",
            targetUsername = targetUsername,
            details = "Unbanned by Admin",
            timestamp = timeStr
        )
        _adminLogs.value = listOf(log) + _adminLogs.value
    }

    fun addXP(pointsToAdd: Int) {
        scope.launch {
            val current = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newPoints = current.points + pointsToAdd
            val progression = com.example.model.MigLevelCalculator.calculateProgression(newPoints.toLong())
            val calculatedLevel = progression.currentLevel
            val effectiveLevel = maxOf(current.migLevel, calculatedLevel)
            userDao.insertOrUpdate(current.copy(points = newPoints, migLevel = effectiveLevel))
        }
    }

    fun createRoom(name: String, category: String, capacity: Int, ownerUsername: String) {
        scope.launch {
            val roomId = java.util.UUID.randomUUID().toString()
            val newRoom = com.example.data.local.entities.ChatRoomEntity(
                id = roomId,
                name = name,
                category = category,
                usersCount = 1,
                maxUsers = capacity,
                
                isFavorite = false,
                topic = "Created by $ownerUsername"
            )
            chatRoomDao.insertRooms(listOf(newRoom))
        }
    }
}
