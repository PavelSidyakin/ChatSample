package com.example.chatsample.chatlist.store.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.chatsample.data.ChatDb
import com.example.chatsample.data.DbChatListItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.repository.ChatNetworkRepository
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatDataSourceImpl @Inject constructor(
    private val chatNetworkRepository: ChatNetworkRepository,
    private val chatDb: Lazy<ChatDb>
) : ChatDataSource {

    private val pageListConfig =
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 20
        )

    private val loadingChannel = Channel<Boolean>(CONFLATED)

    @ExperimentalPagingApi
    override fun observeChatList(coroutineScope: CoroutineScope): Flow<PagingData<ChatInfo>> {

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
            remoteMediator = ChatListRemoteMediator(coroutineScope, chatDb.get(), chatNetworkRepository, loadingChannel)
        )
            .flow
            .map { pagingData ->
                pagingData.map { chatListItem: DbChatListItem ->
                    ChatInfo(
                        chatId = chatListItem.chatId,
                        chatName = chatListItem.chatName,
                        chatType = ChatType.byId(chatListItem.chatType),
                        chatOrder = chatListItem.chatOrder,
                    )
                }
            }
    }

    override fun observeLoadingState(): Channel<Boolean> {
        return loadingChannel
    }
}