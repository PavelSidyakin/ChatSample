package com.example.chatsample.repository

import androidx.paging.PagingData
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    fun observeChatList(): Flow<PagingData<ChatInfo>>
}