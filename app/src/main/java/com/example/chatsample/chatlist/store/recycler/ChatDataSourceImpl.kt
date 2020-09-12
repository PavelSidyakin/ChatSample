package com.example.chatsample.chatlist.store.recycler

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatsample.chatlist.store.ChatListDbRepository
import com.example.chatsample.chatlist.store.ChatListRemoteRepository
import com.example.chatsample.data.ChatDb
import com.example.chatsample.data.DbChatListItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatDataSourceImpl @Inject constructor(
    private val chatListRemoteRepository: ChatListRemoteRepository,
    private val chatListDbRepository: ChatListDbRepository,
) : ChatDataSource {

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
            .map { pagingData ->
                pagingData.mapSync { chatListItem: DbChatListItem ->
                    ChatInfo(
                        chatId = chatListItem.chatId,
                        chatName = chatListItem.chatName,
                        chatType = ChatType.byId(chatListItem.chatType),
                        chatOrder = chatListItem.chatOrder
                    )
                }
            }
    }
}