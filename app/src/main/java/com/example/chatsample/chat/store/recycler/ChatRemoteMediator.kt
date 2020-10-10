package com.example.chatsample.chat.store.recycler

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.model.NextMessageListInfo
import com.example.chatsample.chat.model.RequestMessageListResult
import com.example.chatsample.chat.model.UpdateMessageListEvent
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.chat.store.data.ChatRemoteRepository
import com.example.chatsample.utils.parseString
import com.example.chatsample.utils.serializeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
class ChatRemoteMediator(
    private val chatId: Long,
    private val chatDbRepository: ChatDbRepository,
    private val chatRemoteRepository: ChatRemoteRepository,
    private val pagingConfig: PagingConfig,
) : RemoteMediator<Int, MessageInfo>() {

    private val monitorJob = Job() + Dispatchers.IO

    override suspend fun initialize(): InitializeAction  = coroutineScope {

        async(monitorJob) {
            chatRemoteRepository.subscribeMessageListUpdates(chatId)
                .collectLatest { updateMessageListEvent: UpdateMessageListEvent ->
                    // Next page tokens will be invalid. Refresh the list
                    updateFromNetwork(LoadType.REFRESH, null)
                }
        }

        return@coroutineScope super.initialize()
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MessageInfo>): MediatorResult {
        try {
            var loadKey: NextMessageListInfo? = null

            when (loadType) {
                LoadType.REFRESH -> {
                } // Do nothing
                LoadType.PREPEND ->
                    // We don't need pagination at the top of the list
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = chatDbRepository.getMessageListNextRemoteKey(chatId)

                    if (remoteKey == null || remoteKey.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    loadKey = parseString(remoteKey) as NextMessageListInfo?
                }
            }

            return updateFromNetwork(loadType, loadKey)
        } catch (e: Throwable) {
            Log.w(TAG, "", e)

            return MediatorResult.Error(e)
        }
    }

    private suspend fun updateFromNetwork(loadType: LoadType, loadKey: NextMessageListInfo?): MediatorResult {
        val networkMessageListResult = chatRemoteRepository.requestMessageList(
            chatId = chatId,
            nextInfo = loadKey,
            limit = when (loadType) {
                LoadType.REFRESH -> pagingConfig.initialLoadSize
                else -> pagingConfig.pageSize
            }
        )

        if (networkMessageListResult is RequestMessageListResult.Ok) {
            chatDbRepository.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatDbRepository.deleteAllMessages(chatId)
                }

                val nextStringToken = serializeToString(networkMessageListResult.nextMessageListInfo)

                chatDbRepository.deleteMessageListRemoteKey(chatId)
                chatDbRepository.setMessageListNextRemoteKey(chatId,nextStringToken ?: "")

                chatDbRepository.insertAllMessages(chatId, networkMessageListResult.messages)
            }

            return MediatorResult.Success(endOfPaginationReached = networkMessageListResult.messages.isEmpty())

        } else {
            Log.e(TAG, "Unexpected message list request result", )
            return MediatorResult.Error(RuntimeException("Unexpected message list request result"))
        }

    }

    companion object {
        private const val TAG = "MessageListMediator"
    }

}