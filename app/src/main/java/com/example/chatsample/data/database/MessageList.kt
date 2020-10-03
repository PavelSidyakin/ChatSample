package com.example.chatsample.data.database

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

// ------------------------- Entities ---------------------------------
@Entity(tableName = "messages", primaryKeys = ["f_chat_id", "f_message_id"])
data class DbMessageItemTable(
    @ColumnInfo(name = "f_chat_id")
    val chatId: Long,

    @ColumnInfo(name = "f_message_id")
    val messageId: Long,

    @ColumnInfo(name = "f_message_text")
    val messageText: String,

    @ColumnInfo(name = "f_message_sender_id")
    val messageSenderId: Int,

    @ColumnInfo(name = "f_message_status")
    val messageStatus: Int,

    @ColumnInfo(name = "f_message_type")
    val messageType: Int
)

@Entity(tableName = "message_list_remote_key")
data class DbMessageSubListRemoteKeyTable(
    @PrimaryKey
    @ColumnInfo(name = "f_chat_id")
    val chatId: Long,

    @ColumnInfo(name = "f_next_message_token")
    val nextMessageToken: String
)
// ------------------------- Queries ---------------------------------

data class DbMessageWithUserItemQuery(
    @ColumnInfo(name = "f_message_id")
    val messageId: Long,

    @ColumnInfo(name = "f_message_text")
    val messageText: String,

    @ColumnInfo(name = "f_message_sender_id")
    val messageSenderId: Int,

    @ColumnInfo(name = "f_message_sender_name")
    val messageSenderName: String,

    @ColumnInfo(name = "f_message_status")
    val messageStatus: Int,

    @ColumnInfo(name = "f_message_type")
    val messageType: Int
)

// ------------------------- Dao ---------------------------------
@Dao
interface MessageListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<DbMessageItemTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DbMessageItemTable)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(message: DbMessageItemTable)

    @Query(
        """
                SELECT 
                    messages.f_message_id as f_message_id,
                    messages.f_message_text as f_message_text,
                    messages.f_message_sender_id as f_message_sender_id,
                    users.f_user_name as f_message_sender_name,
                    messages.f_message_status as f_message_status,
                    messages.f_message_type as f_message_type
                FROM messages, users
                WHERE 
                    :chatId == messages.f_message_id AND 
                    f_message_sender_id == users.f_user_id
                ORDER BY f_message_id DESC
            """
    )
    fun selectAllMessages(chatId: Long): PagingSource<Int, DbMessageWithUserItemQuery>

    @Query("DELETE FROM messages WHERE :chatId == messages.f_chat_id")
    suspend fun deleteAllMessages(chatId: Long)
}

@Dao
interface SubMessageListRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: DbMessageSubListRemoteKeyTable)

    @Query("SELECT * FROM message_list_remote_key WHERE :chatId == message_list_remote_key.f_chat_id")
    suspend fun remoteKey(chatId: Long): DbMessageSubListRemoteKeyTable?

    @Query("DELETE FROM message_list_remote_key WHERE :chatId == message_list_remote_key.f_chat_id")
    suspend fun deleteRemoteKey(chatId: Long)
}
