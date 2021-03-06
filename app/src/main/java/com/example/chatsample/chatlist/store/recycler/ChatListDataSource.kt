package com.example.chatsample.chatlist.store.recycler

import androidx.paging.PagingData
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow

interface ChatListDataSource {
    fun observeChatList(): Flow<PagingData<ChatInfo>>
}