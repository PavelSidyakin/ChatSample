package com.example.chatsample.chatlist.store

import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.flow.Flow

interface ChatListRemoteRepository {
    suspend fun requestChatList(nextInfo: NextChatListInfo?, limit: Int): RequestChatListResult
    fun subscribeChatListUpdates(): Flow<UpdateChatListEvent>
}