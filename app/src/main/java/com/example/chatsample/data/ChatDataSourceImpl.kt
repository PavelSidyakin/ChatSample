package com.example.chatsample.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.repository.ChatDataSource
import com.example.chatsample.repository.ChatNetworkRepository
import dagger.Lazy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ChatDataSourceImpl @Inject constructor(
    private val chatNetworkRepository: ChatNetworkRepository,
    private val chatDb: Lazy<ChatDb>
) : ChatDataSource {

    private val pageListConfig =
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 15,
            enablePlaceholders = false
        )

    @ExperimentalPagingApi
    override fun observeChatList(): Flow<PagingData<ChatInfo>> {

//        async {
//            val chatListUpdate = chatNetworkRepository.subscribeChatListUpdates()
//                .receive()
//
//            chatDb.get().chats().insertChat(
//                DbChatListItem(
//                    chatId = chatListUpdate.chatInfo.chatId,
//                    chatName = chatListUpdate.chatInfo.chatName,
//                    chatType = chatListUpdate.chatInfo.chatType.id,
//                    chatOrder = chatListUpdate.chatInfo.chatOrder
//                )
//            )
//        }

        return Pager(
            config = pageListConfig,
            pagingSourceFactory = { chatDb.get().chats().selectAllChats() },
            remoteMediator = ChatListRemoteMediator(chatDb.get(), chatNetworkRepository)
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