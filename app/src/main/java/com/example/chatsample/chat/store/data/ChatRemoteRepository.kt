package com.example.chatsample.chat.store.data

import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.flow.Flow

interface ChatRemoteRepository {
    suspend fun requestChatList(nextInfo: NextChatListInfo?, limit: Int): RequestChatListResult
    fun subscribeChatListUpdates(): Flow<UpdateChatListEvent>
}