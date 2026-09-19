package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserProfileEntity)

    @Query("UPDATE user_profile SET credits = :credits, points = :points WHERE id = 'current_user'")
    suspend fun updateCreditsAndPoints(credits: Int, points: Int)

    @Query("UPDATE user_profile SET displayName = :displayName, statusMessage = :status, gender = :gender, country = :country WHERE id = 'current_user'")
    suspend fun updateProfile(displayName: String, status: String, gender: String, country: String)

    @Query("UPDATE user_profile SET displayName = :displayName, statusMessage = :status, gender = :gender, birthday = :birthday, country = :country, isOnline = :isOnline, isInvisible = :isInvisible WHERE id = 'current_user'")
    suspend fun updateFullProfile(displayName: String, status: String, gender: String, birthday: String, country: String, isOnline: Boolean, isInvisible: Boolean)

    @Query("UPDATE user_profile SET migLevel = :level, points = :points WHERE id = 'current_user'")
    suspend fun updateLevelAndXP(level: Int, points: Int)

    @Query("UPDATE user_profile SET securityPin = :newPin WHERE id = 'current_user'")
    suspend fun updateSecurityPin(newPin: String)

    @Query("UPDATE user_profile SET avatarPresetId = :presetId, avatarCustomImageRes = :customImageRes, avatarBg = :bg, avatarPet = :pet, avatarBase = :base, avatarClothes = :clothes, avatarHair = :hair, avatarAccessory = :accessory, pet = :pet, accessory = :accessory WHERE id = 'current_user'")
    suspend fun updateAvatar(presetId: String, customImageRes: Int?, bg: String, pet: String, base: String, clothes: String, hair: String, accessory: String)
}

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms")
    fun getAllRooms(): Flow<List<ChatRoomEntity>>

    @Query("SELECT COUNT(*) FROM chat_rooms")
    suspend fun getRoomsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<ChatRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: ChatRoomEntity)

    @Query("UPDATE chat_rooms SET isFavorite = NOT isFavorite WHERE id = :roomId")
    suspend fun toggleFavorite(roomId: String)

    @Query("UPDATE chat_rooms SET usersCount = :newCount WHERE id = :roomId")
    suspend fun updateUsersCount(roomId: String, newCount: Int)
}

@Dao
interface RoomMessageDao {
    @Query("SELECT * FROM room_messages WHERE roomId = :roomId ORDER BY createdMillis ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<RoomMessageEntity>>

    @Query("SELECT * FROM room_messages ORDER BY createdMillis ASC")
    fun getAllMessages(): Flow<List<RoomMessageEntity>>

    @Query("SELECT COUNT(*) FROM room_messages")
    suspend fun getMessageCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<RoomMessageEntity>)
}

@Dao
interface DirectMessageDao {
    @Query("SELECT * FROM direct_messages WHERE conversationUser = :conversationUser ORDER BY createdMillis ASC")
    fun getMessagesForUser(conversationUser: String): Flow<List<DirectMessageEntity>>

    @Query("SELECT * FROM direct_messages ORDER BY createdMillis ASC")
    fun getAllDirectMessages(): Flow<List<DirectMessageEntity>>

    @Query("SELECT COUNT(*) FROM direct_messages")
    suspend fun getDirectMessageCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectMessage(message: DirectMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectMessages(messages: List<DirectMessageEntity>)
}

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_posts ORDER BY createdMillis DESC")
    fun getAllPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT COUNT(*) FROM feed_posts")
    suspend fun getPostCount(): Int

    @Query("SELECT * FROM post_replies ORDER BY createdMillis ASC")
    fun getAllReplies(): Flow<List<PostReplyEntity>>

    @Query("SELECT * FROM post_replies WHERE postId = :postId ORDER BY createdMillis ASC")
    fun getRepliesForPost(postId: String): Flow<List<PostReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FeedPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<FeedPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: PostReplyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<PostReplyEntity>)

    @Query("UPDATE feed_posts SET isStarred = :isStarred, starCount = :newCount WHERE id = :postId")
    suspend fun updatePostStar(postId: String, isStarred: Boolean, newCount: Int)

    @Query("UPDATE feed_posts SET shareCount = shareCount + 1 WHERE id = :postId")
    suspend fun incrementShare(postId: String)

    @Query("UPDATE feed_posts SET repliesCount = repliesCount + 1 WHERE id = :postId")
    suspend fun incrementRepliesCount(postId: String)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isFriend = 1")
    fun getFriends(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isFollower = 1")
    fun getFollowers(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE isFollowing = 1")
    fun getFollowing(): Flow<List<ContactEntity>>

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("DELETE FROM contacts WHERE username = :username")
    suspend fun deleteContact(username: String)
}

@Dao
interface CreditTransferDao {
    @Query("SELECT * FROM credit_transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<CreditTransferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: CreditTransferEntity): Long

    @Query("SELECT COUNT(*) FROM credit_transfers")
    suspend fun getTransfersCount(): Int
}
