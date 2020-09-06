package com.example.chatsample.chatlist.store.paging

import androidx.paging.PagingData
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {
    fun observeChatList(coroutineScope: CoroutineScope): Flow<PagingData<ChatInfo>>
    fun observeLoadingState(): Channel<Boolean>
}