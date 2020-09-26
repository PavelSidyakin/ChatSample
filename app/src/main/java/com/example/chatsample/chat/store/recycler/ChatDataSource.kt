package com.example.chatsample.chat.store.recycler

import androidx.paging.PagingData
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    fun observeChat(): Flow<PagingData<ChatInfo>>
}