package com.example.chatsample.chatlist.store.paging

import androidx.paging.PagingSource
import com.example.chatsample.chatlist.store.ChatListStateChanges
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.chatlist.store.repository.ChatNetworkRepository
import com.example.chatsample.model.ChatType
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ChatListDataSource(
    private val dispatchStateChangesCallback: (changes: ChatListStateChanges) -> Unit,
    private val chatNetworkRepository: ChatNetworkRepository,
    override val coroutineContext: CoroutineContext,
    private val retryChannel: Channel<Any>
) : PagingSource<NextChatListInfo, ChatListItem>(), CoroutineScope {

    init {
        launch {
            while (coroutineContext.isActive) {
                retryChannel.receive()
                retryRunnable?.run()
            }
        }
    }

    private var retryRunnable: Runnable? = null

    override suspend fun load(params: LoadParams<NextChatListInfo>): LoadResult<NextChatListInfo, ChatListItem> {
        try {
            dispatchStateChangesCallback(ChatListStateChanges.LoadingStarted())

            val requestChatResult: RequestChatListResult = chatNetworkRepository.requestChatList(params.key, 10)

            return if (requestChatResult is RequestChatListResult.Ok) {
                LoadResult.Page(
                    data = requestChatResult.chatMap.entries.map { entry ->
                        when (entry.value.chatType) {
                            ChatType.DIRECT -> ChatListItem.Direct(entry.key, entry.value.chatName)
                            ChatType.GROUP -> ChatListItem.Group(entry.key, entry.value.chatName)
                        }
                    },
                    prevKey = null, // Only paging forward.
                    nextKey = requestChatResult.nextChatListInfo
                )
            } else {
                LoadResult.Error(RuntimeException("Unknown error"))
            }
        } catch (e: Exception) {
            dispatchStateChangesCallback(ChatListStateChanges.ErrorOccurred(e))
            return LoadResult.Error(e)
        } finally {
            dispatchStateChangesCallback(ChatListStateChanges.LoadingCompleted())
        }
    }
}

