package com.example.chatsample.data.database

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.chatsample.model.ChatType
import kotlinx.coroutines.flow.Flow

// ------------------------- Entities ---------------------------------
@Entity(tableName = "chats")
data class DbChatListItem(
    @PrimaryKey
    @ColumnInfo(name = "f_chat_id")
    val chatId: Long,

    @ColumnInfo(name = "f_chat_order")
    val chatOrder: Long,

    @ColumnInfo(name = "f_chat_name")
    val chatName: String,

    @ColumnInfo(name = "f_chat_type")
    val chatType: Int /** Values from [ChatType] */
)

@Entity(tableName = "chats_remote_key")
data class DbSubChatListRemoteKey(
    @PrimaryKey
    @ColumnInfo(name = "f_next_chat_token")
    val nextChatToken: String
)
// ------------------------- Dao ---------------------------------
@Dao
interface ChatListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<DbChatListItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: DbChatListItem)

    @Query("SELECT * FROM chats ORDER BY f_chat_order DESC")
    fun selectAllChats(): PagingSource<Int, DbChatListItem>

    @Query("SELECT * FROM chats ORDER BY f_chat_order DESC")
    fun selectAllChatsFlow(): Flow<List<DbChatListItem>>

    @Query("SELECT * FROM chats ORDER BY f_chat_order DESC")
    suspend fun selectAllChatsList(): List<DbChatListItem>

    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()
}

@Dao
interface SubChatListRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: DbSubChatListRemoteKey)

    @Query("SELECT * FROM chats_remote_key")
    suspend fun remoteKey(): DbSubChatListRemoteKey?

    @Query("DELETE FROM chats_remote_key")
    suspend fun deleteRemoteKey()
}
