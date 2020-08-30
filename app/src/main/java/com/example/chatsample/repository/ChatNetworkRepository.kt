package com.example.chatsample.repository

import com.example.chatsample.model.AuthResult
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.channels.Channel

interface ChatNetworkRepository {
    fun init()

    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun continueWithCode(code: String): AuthResult

    suspend fun requestChatList(nextInfo: NextChatListInfo?, limit: Int): RequestChatListResult

    suspend fun subscribeChatListUpdates(): Channel<UpdateChatListEvent>
}