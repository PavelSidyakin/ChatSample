package com.example.chatsample.chat.store.recycler

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.chat.store.data.ChatRemoteRepository
import com.example.chatsample.chatlist.store.recycler.ChatListDataSource
import com.example.chatsample.model.ChatInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatDataSourceImpl @Inject constructor(
    private val chatRemoteRepository: ChatRemoteRepository,
    private val chatDbRepository: ChatDbRepository,
) : ChatDataSource {

    private val pageListConfig =
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 30
        )

    @ExperimentalPagingApi
    override fun observeMessageList(): Flow<PagingData<MessageInfo>> {
        return Pager(
            config = pageListConfig,
            pagingSourceFactory = { chatDbRepository.getAllMessages() },
            remoteMediator = ChatRemoteMediator(chatDbRepository, chatRemoteRepository, pageListConfig)
        )
            .flow
    }
}