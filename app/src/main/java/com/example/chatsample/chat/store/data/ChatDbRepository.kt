package com.example.chatsample.chat.store.data

import androidx.paging.PagingSource
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.model.ChatInfo

interface ChatDbRepository {

    suspend fun withTransaction(block: suspend () -> Unit)

    // Chat list
    suspend fun insertAllChats(chats: List<ChatInfo>)

    suspend fun insertChat(chat: ChatInfo)

    fun getAllChats(): PagingSource<Int, ChatInfo>

    suspend fun deleteAllChats()

    suspend fun setChatListNextRemoteKey(key: String)

    suspend fun getChatListNextRemoteKey(): String?

    suspend fun deleteChatListRemoteKey()

    // Message list
    fun getAllMessages(chatId: Long): PagingSource<Int, MessageInfo>

    suspend fun insertAllMessages(chatId: Long, messages: List<MessageInfo>)

    suspend fun insertMessage(chatId: Long, message: MessageInfo)

    suspend fun deleteAllMessages(chatId: Long)

    suspend fun setMessageListNextRemoteKey(chatId: Long, key: String)

    suspend fun getMessageListNextRemoteKey(chatId: Long): String?

    suspend fun deleteMessageListRemoteKey(chatId: Long)

}