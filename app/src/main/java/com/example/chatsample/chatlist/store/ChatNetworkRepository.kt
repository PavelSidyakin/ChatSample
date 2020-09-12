package com.example.chatsample.chatlist.store

import com.example.chatsample.model.AuthResult
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.flow.Flow

interface ChatNetworkRepository {
    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun continueWithCode(code: String): AuthResult

    suspend fun requestChatList(nextInfo: NextChatListInfo?, limit: Int): RequestChatListResult

    fun subscribeChatListUpdates(): Flow<UpdateChatListEvent>
}