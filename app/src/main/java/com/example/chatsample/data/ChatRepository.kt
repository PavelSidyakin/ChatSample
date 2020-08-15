package com.example.chatsample.data

import com.example.chatsample.model.AuthResult
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.channels.Channel

interface ChatRepository {
    fun init()

    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun continueWithCode(code: String): AuthResult

    suspend fun requestInitialChatList(limit: Int): RequestChatListResult
    suspend fun requestNextChatList(nextInfo: NextChatListInfo, limit: Int): RequestChatListResult

    suspend fun subscribeChatListUpdates(): Channel<UpdateChatListEvent>
}