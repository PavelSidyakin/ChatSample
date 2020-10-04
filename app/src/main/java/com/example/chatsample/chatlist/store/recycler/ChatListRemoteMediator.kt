package com.example.chatsample.chatlist.store.recycler

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.chatsample.chatlist.store.data.ChatListDbRepository
import com.example.chatsample.chatlist.store.data.ChatListRemoteRepository
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import com.example.chatsample.utils.parseString
import com.example.chatsample.utils.serializeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
class ChatListRemoteMediator(
    private val chatListDbRepository: ChatListDbRepository,
    private val chatListRemoteRepository: ChatListRemoteRepository,
    private val pageListConfig: PagingConfig,
) : RemoteMediator<Int, ChatInfo>() {
    private val monitorJob = Job() + Dispatchers.IO

    override suspend fun initialize(): InitializeAction  = coroutineScope {

        async(monitorJob) {
            chatListRemoteRepository.subscribeChatListUpdates()
                .collectLatest { updateChatListEvent: UpdateChatListEvent ->
                    // To refresh the list immediately
                    chatListDbRepository.insertChat(updateChatListEvent.chatInfo)

                    // Next page tokens will be invalid. Refresh the list
                    updateFromNetwork(LoadType.REFRESH, null)
                }
        }

        return@coroutineScope super.initialize()
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, ChatInfo>): MediatorResult {
        try {
            var loadKey: NextChatListInfo? = null

            when (loadType) {
                LoadType.REFRESH -> {
                } // Do nothing
                LoadType.PREPEND ->
                    // We don't need pagination at the top of the list
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = chatListDbRepository.getNextRemoteKey()

                    if (remoteKey == null || remoteKey.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    loadKey = parseString(remoteKey) as NextChatListInfo?
                }
            }

            return updateFromNetwork(loadType, loadKey)
        } catch (e: Throwable) {
            Log.w(TAG, "", e)

            return MediatorResult.Error(e)
        }
    }

    private suspend fun updateFromNetwork(loadType: LoadType, loadKey: NextChatListInfo?): MediatorResult {
        val networkChatListResult = chatListRemoteRepository.requestChatList(
            nextInfo = loadKey,
            limit = when (loadType) {
                LoadType.REFRESH -> pageListConfig.initialLoadSize
                else -> pageListConfig.pageSize
            }
        )

        if (networkChatListResult is RequestChatListResult.Ok) {
            chatListDbRepository.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatListDbRepository.deleteAllChats()
                }

                val nextStringToken = serializeToString(networkChatListResult.nextChatListInfo)

                chatListDbRepository.deleteRemoteKey()
                chatListDbRepository.setNextRemoteKey(nextStringToken ?: "")

                chatListDbRepository.insertAll(networkChatListResult.chats)
            }

            return MediatorResult.Success(endOfPaginationReached = networkChatListResult.chats.isEmpty())

        } else {
            Log.e(TAG, "Unexpected chat list request result", )
            return MediatorResult.Error(RuntimeException("Unexpected chat list request result"))
        }

    }

    companion object {
        private const val TAG = "ChatListMediator"
    }

}