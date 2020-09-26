package com.example.chatsample.chatlist.store.recycler

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatsample.chatlist.store.data.ChatListDbRepository
import com.example.chatsample.chatlist.store.data.ChatListRemoteRepository
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatListDataSourceImpl @Inject constructor(
    private val chatListRemoteRepository: ChatListRemoteRepository,
    private val chatListDbRepository: ChatListDbRepository,
) : ChatListDataSource {

    private val pageListConfig =
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 30
        )

    @ExperimentalPagingApi
    override fun observeChatList(): Flow<PagingData<ChatInfo>> {
        return Pager(
            config = pageListConfig,
            pagingSourceFactory = { chatListDbRepository.getAllChats() },
            remoteMediator = ChatListRemoteMediator(chatListDbRepository, chatListRemoteRepository, pageListConfig)
        )
            .flow
    }
}