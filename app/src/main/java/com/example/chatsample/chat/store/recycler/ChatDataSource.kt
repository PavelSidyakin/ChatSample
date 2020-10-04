package com.example.chatsample.chat.store.recycler

import androidx.paging.PagingData
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    fun observeMessageList(chatId: Long): Flow<PagingData<MessageInfo>>
}