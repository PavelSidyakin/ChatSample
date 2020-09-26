package com.example.chatsample.chat.store.data

import androidx.paging.PagingSource
import com.example.chatsample.model.ChatInfo

interface ChatDbRepository {
    suspend fun insertAll(chats: List<ChatInfo>)

    suspend fun insertChat(chat: ChatInfo)

    fun getAllChats(): PagingSource<Int, ChatInfo>

    suspend fun deleteAllChats()

    suspend fun setNextRemoteKey(key: String)

    suspend fun getNextRemoteKey(): String?

    suspend fun deleteRemoteKey()

    suspend fun withTransaction(block: suspend () -> Unit)

}