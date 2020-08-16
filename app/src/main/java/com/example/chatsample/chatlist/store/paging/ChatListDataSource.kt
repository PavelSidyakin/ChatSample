package com.example.chatsample.chatlist.store.paging

import androidx.paging.PageKeyedDataSource
import com.example.chatsample.chatlist.store.ChatListStateChanges
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.data.ChatRepository
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
    private val chatRepository: ChatRepository,
    override val coroutineContext: CoroutineContext,
    private val retryChannel: Channel<Any>
) : PageKeyedDataSource<NextChatListInfo, ChatListItem>(), CoroutineScope {

    init {
        launch {
            while (coroutineContext.isActive) {
                retryChannel.receive()
                retryRunnable?.run()
            }
        }
    }

    private var retryRunnable: Runnable? = null

    override fun loadInitial(params: LoadInitialParams<NextChatListInfo>, callback: LoadInitialCallback<NextChatListInfo, ChatListItem>) {
        launch {
            dispatchStateChangesCallback(ChatListStateChanges.LoadingStarted())

            try {
                val requestChatResult = chatRepository.requestInitialChatList(20)

                if (requestChatResult is RequestChatListResult.Ok) {
                    callback.onResult(
                        requestChatResult.chatMap.entries.map { entry ->
                            when (entry.value.chatType) {
                                ChatType.DIRECT -> ChatListItem.Direct(entry.key, entry.value.chatName)
                                ChatType.GROUP -> ChatListItem.Group(entry.key, entry.value.chatName)
                            }
                        },
                        null,
                        requestChatResult.nextChatListInfo
                    )
                }
            } catch (throwable: Throwable) {
                retryRunnable = Runnable { loadInitial(params, callback) }
                dispatchStateChangesCallback(ChatListStateChanges.ErrorOccurred(throwable))
            } finally {
                dispatchStateChangesCallback(ChatListStateChanges.LoadingCompleted())
            }
        }
    }

    override fun loadAfter(params: LoadParams<NextChatListInfo>, callback: LoadCallback<NextChatListInfo, ChatListItem>) {
        launch {
            dispatchStateChangesCallback(ChatListStateChanges.LoadingStarted())

            try {
                val requestChatResult = chatRepository.requestNextChatList(params.key, 10)

                if (requestChatResult is RequestChatListResult.Ok) {
                    callback.onResult(
                        requestChatResult.chatMap.entries.map { entry ->
                            when (entry.value.chatType) {
                                ChatType.DIRECT -> ChatListItem.Direct(entry.key, entry.value.chatName)
                                ChatType.GROUP -> ChatListItem.Group(entry.key, entry.value.chatName)
                            }
                        },
                        requestChatResult.nextChatListInfo
                    )
                }
            } catch (throwable: Throwable) {
                retryRunnable = Runnable { loadAfter(params, callback) }
                dispatchStateChangesCallback(ChatListStateChanges.ErrorOccurred(throwable))
            } finally {
                dispatchStateChangesCallback(ChatListStateChanges.LoadingCompleted())
            }
        }
    }

    override fun loadBefore(params: LoadParams<NextChatListInfo>, callback: LoadCallback<NextChatListInfo, ChatListItem>) {
    }
}

