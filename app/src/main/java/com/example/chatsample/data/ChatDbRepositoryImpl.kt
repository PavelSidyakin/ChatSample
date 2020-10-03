package com.example.chatsample.data

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.model.MessageStatus
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.data.database.ChatDb
import com.example.chatsample.data.database.DbChatListItem
import com.example.chatsample.data.database.DbMessageItemTable
import com.example.chatsample.data.database.DbMessageSubListRemoteKeyTable
import com.example.chatsample.data.database.DbMessageWithUserItemQuery
import com.example.chatsample.data.database.DbSubChatListRemoteKey
import com.example.chatsample.data.database.DbUserItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.utils.mapValue
import javax.inject.Inject
import kotlin.IllegalStateException

class ChatDbRepositoryImpl @Inject constructor(
    private val chatDb: ChatDb
) : ChatDbRepository {

    private val commonDao by lazy { chatDb.common() }

    private val chatsDao by lazy { chatDb.chats() }
    private val subChatListRemoteKeyDao by lazy { chatDb.subChatListRemoteKey() }

    private val messagesDao by lazy { chatDb.messages() }
    private val subMessageListRemoteKeyDao by lazy { chatDb.subMessageListRemoteKey() }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        chatDb.withTransaction {
            block()
        }
    }

    override suspend fun insertAllChats(chats: List<ChatInfo>) {
        chatsDao.insertAll(chats.map { it.toDbChatListItem() })
    }

    override suspend fun insertChat(chat: ChatInfo) {
        chatsDao.insertChat(chat.toDbChatListItem())
    }

    override fun getAllChats(): PagingSource<Int, ChatInfo> {
        return chatsDao.selectAllChats().mapValue(
            { dbChatListItem -> dbChatListItem.toChatInfo() },
            { chatInfo -> chatInfo.toDbChatListItem() }
        )
    }

    override suspend fun deleteAllChats() {
        chatsDao.deleteAllChats()
    }

    override suspend fun setChatListNextRemoteKey(key: String) {
        subChatListRemoteKeyDao.insert(DbSubChatListRemoteKey(key))
    }

    override suspend fun getChatListNextRemoteKey(): String? {
        return subChatListRemoteKeyDao.remoteKey()?.nextChatToken
    }

    override suspend fun deleteChatListRemoteKey() {
        subChatListRemoteKeyDao.deleteRemoteKey()
    }

    override fun getAllMessages(chatId: Long): PagingSource<Int, MessageInfo> {
        return messagesDao.selectAllMessages(chatId).mapValue(
            { messageWithUserItemQuery -> messageWithUserItemQuery.toMessageInfo() },
            { messageInfo -> messageInfo.toDbMessageWithUserItemQuery() }
        )
    }

    override suspend fun insertAllMessages(chatId: Long, messages: List<MessageInfo>) {
        commonDao.insertUsers(messages.map { it.messageSenderId to it.messageSenderName }.map { DbUserItem(it.first, it.second) })
        messagesDao.insertAllMessages(messages.map { it.toDbMessageItemTable(chatId) })
    }

    override suspend fun deleteAllMessages(chatId: Long) {
        messagesDao.deleteAllMessages(chatId)
    }

    override suspend fun setMessageListNextRemoteKey(chatId: Long, key: String) {
        subMessageListRemoteKeyDao.insert(DbMessageSubListRemoteKeyTable(chatId, key))
    }

    override suspend fun getMessageListNextRemoteKey(chatId: Long): String? {
        return subMessageListRemoteKeyDao.remoteKey(chatId)?.nextMessageToken
    }

    override suspend fun deleteMessageListRemoteKey(chatId: Long) {
        subMessageListRemoteKeyDao.deleteRemoteKey(chatId)
    }

    private fun DbMessageWithUserItemQuery.toMessageInfo(): MessageInfo {
        return when (messageType) {
            MessageInfo.OutgoingMessage.MESSAGE_TYPE_ID -> MessageInfo.OutgoingMessage(
                messageId, messageText, messageSenderId, messageSenderName, MessageStatus.byIntValue(messageStatus)
            )
            MessageInfo.IncomingMessage.MESSAGE_TYPE_ID -> MessageInfo.IncomingMessage(
                messageId, messageText, messageSenderId, messageSenderName
            )
            else -> throw IllegalStateException("Wrong message type: $messageType")
        }
    }

    private fun MessageInfo.toDbMessageWithUserItemQuery(): DbMessageWithUserItemQuery {
        return when (this) {
            is MessageInfo.OutgoingMessage -> DbMessageWithUserItemQuery(
                messageId, messageText, messageSenderId, messageSenderName, messageStatus.intValue, MessageInfo.OutgoingMessage.MESSAGE_TYPE_ID
            )
            is MessageInfo.IncomingMessage -> DbMessageWithUserItemQuery(
                messageId, messageText, messageSenderId, messageSenderName, 0, MessageInfo.IncomingMessage.MESSAGE_TYPE_ID
            )
        }
    }

    private fun MessageInfo.toDbMessageItemTable(chatId: Long): DbMessageItemTable {
        return when (this) {
            is MessageInfo.OutgoingMessage -> DbMessageItemTable(
                chatId, messageId, messageText, messageSenderId, messageStatus.intValue, MessageInfo.OutgoingMessage.MESSAGE_TYPE_ID
            )
            is MessageInfo.IncomingMessage -> DbMessageItemTable(
                chatId, messageId, messageText, messageSenderId, 0, MessageInfo.IncomingMessage.MESSAGE_TYPE_ID
            )
        }
    }

    private fun ChatInfo.toDbChatListItem(): DbChatListItem {
        return DbChatListItem(
            chatId = this.chatId,
            chatOrder = this.chatOrder,
            chatName = this.chatName,
            chatType = this.chatType.id
        )
    }

    private fun DbChatListItem.toChatInfo(): ChatInfo {
        return ChatInfo(
            chatId = this.chatId,
            chatOrder = this.chatOrder,
            chatName = this.chatName,
            chatType = ChatType.byId(this.chatType)
        )
    }
}