package com.example.chatsample.chat.store.data

import com.example.chatsample.chat.model.NextMessageListInfo
import com.example.chatsample.chat.model.RequestMessageListResult
import com.example.chatsample.chat.model.UpdateMessageListEvent
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.flow.Flow

interface ChatRemoteRepository {
    suspend fun requestChatList(nextInfo: NextChatListInfo?, limit: Int): RequestChatListResult
    fun subscribeChatListUpdates(): Flow<UpdateChatListEvent>

    suspend fun requestMessageList(chatId: Long, nextInfo: NextMessageListInfo?, limit: Int): RequestMessageListResult
    fun subscribeMessageListUpdates(chatId: Long): Flow<UpdateMessageListEvent>

}