package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        UserProfileEntity::class,
        ChatRoomEntity::class,
        RoomMessageEntity::class,
        DirectMessageEntity::class,
        FeedPostEntity::class,
        PostReplyEntity::class,
        ContactEntity::class,
        CreditTransferEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class MigDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun roomMessageDao(): RoomMessageDao
    abstract fun directMessageDao(): DirectMessageDao
    abstract fun feedDao(): FeedDao
    abstract fun contactDao(): ContactDao
    abstract fun creditTransferDao(): CreditTransferDao

    companion object {
        @Volatile
        private var INSTANCE: MigDatabase? = null

        fun getDatabase(context: Context): MigDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MigDatabase::class.java,
                    "mig33_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
