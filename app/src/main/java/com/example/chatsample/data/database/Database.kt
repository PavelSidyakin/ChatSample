package com.example.chatsample.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ------------------------- Database ---------------------------------
@Database(
    entities = [
        // Common
        DbUserItem::class,
        // Chat list
        DbChatListItem::class, DbSubChatListRemoteKey::class,
        // Message list
        DbMessageItemTable::class, DbMessageSubListRemoteKeyTable::class, DbMessageWithUserItemQuery::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class ChatDb : RoomDatabase() {

    companion object {
        fun create(context: Context): ChatDb {
            val databaseBuilder =
                Room.databaseBuilder(context, ChatDb::class.java, "ChatDb.db")
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun common(): CommonDao

    abstract fun chats(): ChatListDao
    abstract fun subChatListRemoteKey(): SubChatListRemoteKeyDao

    abstract fun messages(): MessageListDao
    abstract fun subMessageListRemoteKey(): SubMessageListRemoteKeyDao
}
