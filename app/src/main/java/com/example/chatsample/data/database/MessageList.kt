package com.example.chatsample.data.database

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

// ------------------------- Entities ---------------------------------
@Entity(
    tableName = "messages",
    primaryKeys = ["f_chat_id", "f_message_id", "f_message_tmp_id"],
)
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
    val messageType: Int,

    @ColumnInfo(name = "f_message_tmp_id")
    val messageTemporaryId: Long, // Used for an outgoing message
)

@Entity(tableName = "message_list_remote_key")
data class DbMessageSubListRemoteKeyTable(
    @PrimaryKey
    @ColumnInfo(name = "f_chat_id")
    val chatId: Long,

    @ColumnInfo(name = "f_next_message_token")
    val nextMessageToken: String,
)
// ------------------------- Queries ---------------------------------

@Entity(primaryKeys = ["f_chat_id", "f_message_id", "f_message_tmp_id"])
data class DbMessageWithUserItemQuery(
    @ColumnInfo(name = "f_chat_id")
    val chatId: Long,

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
    val messageType: Int,

    @ColumnInfo(name = "f_message_tmp_id")
    val messageTemporaryId: Long, // Used for an outgoing message
)

// ------------------------- Dao ---------------------------------
@Dao
interface MessageListDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllMessages(messages: List<DbMessageItemTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DbMessageItemTable)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(message: DbMessageItemTable)

    @Query(
        """
                SELECT 
                    messages.f_chat_id as f_chat_id,
                    messages.f_message_id as f_message_id,
                    messages.f_message_text as f_message_text,
                    messages.f_message_sender_id as f_message_sender_id,
                    CASE WHEN users.f_user_name IS NULL THEN "" ELSE users.f_user_name END as f_message_sender_name,
                    messages.f_message_status as f_message_status,
                    messages.f_message_type as f_message_type,
                    messages.f_message_tmp_id as f_message_tmp_id
                FROM messages
                    LEFT JOIN users ON f_message_sender_id == users.f_user_id
                WHERE 
                    :chatId == messages.f_chat_id 
                ORDER BY f_message_id DESC
            """
    )
    fun selectAllMessages(chatId: Long): PagingSource<Int, DbMessageWithUserItemQuery>

    @Query("DELETE FROM messages WHERE :chatId == messages.f_chat_id")
    suspend fun deleteAllMessages(chatId: Long)

    @Query("SELECT * FROM messages WHERE :chatId == messages.f_chat_id")
    suspend fun selectAllMessagesList(chatId: Long): List<DbMessageItemTable>

    @Query("DELETE FROM messages WHERE :chatId == messages.f_chat_id AND f_message_tmp_id == :temporaryId")
    suspend fun deleteMessageWithTemporaryId(chatId: Long, temporaryId: Long)
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
