package com.example.chatsample.chatlist.store.recycler

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.chatsample.chatlist.store.ChatNetworkRepository
import com.example.chatsample.data.ChatDb
import com.example.chatsample.data.DbChatListItem
import com.example.chatsample.data.DbSubChatListRemoteKey
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
    private val db: ChatDb,
    private val chatNetworkRepository: ChatNetworkRepository,
    private val pageListConfig: PagingConfig
) : RemoteMediator<Int, DbChatListItem>() {
    private val chatsDao = db.chats()
    private val subChatListRemoteKeyDao = db.subChatListRemoteKey()

    private val monitorJob = Job() + Dispatchers.IO

    override suspend fun initialize(): InitializeAction  = coroutineScope {

        async(monitorJob) {
            chatNetworkRepository.subscribeChatListUpdates()
                .collectLatest { updateChatListEvent: UpdateChatListEvent ->
                    // To refresh the list immediately
                    chatsDao.insertChat(updateChatListEvent.chatInfo.toDbChatListItem())

                    // Next page tokens will be invalid. Refresh the list
                    updateFromNetwork(LoadType.REFRESH, null)
                }
        }

        return@coroutineScope super.initialize()
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, DbChatListItem>): MediatorResult {
        try {
            var loadKey: NextChatListInfo? = null

            when (loadType) {
                LoadType.REFRESH -> {
                } // Do nothing
                LoadType.PREPEND ->
                    // We don't need pagination at the top of the list
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        subChatListRemoteKeyDao.remoteKey()
                    }

                    if (remoteKey == null || remoteKey.nextChatToken.isEmpty()) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    loadKey = parseString(remoteKey.nextChatToken) as NextChatListInfo?
                }
            }

            return updateFromNetwork(loadType, loadKey)
        } catch (e: Throwable) {
            Log.w(TAG, "", e)

            return MediatorResult.Error(e)
        }
    }

    private suspend fun updateFromNetwork(loadType: LoadType, loadKey: NextChatListInfo?): MediatorResult {
        val networkChatListResult = chatNetworkRepository.requestChatList(
            nextInfo = loadKey,
            limit = when (loadType) {
                LoadType.REFRESH -> pageListConfig.initialLoadSize
                else -> pageListConfig.pageSize
            }
        )
        delay(1000)

        if (networkChatListResult is RequestChatListResult.Ok) {
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatsDao.deleteAllChats()
                }

                val nextStringToken = serializeToString(networkChatListResult.nextChatListInfo)

                subChatListRemoteKeyDao.deleteRemoteKey()
                subChatListRemoteKeyDao.insert(DbSubChatListRemoteKey(nextStringToken ?: ""))

                chatsDao.insertAll(networkChatListResult.chats.map { it.toDbChatListItem() })
            }

            return MediatorResult.Success(endOfPaginationReached = networkChatListResult.chats.isEmpty())

        } else {
            Log.e(TAG, "Unexpected chat list request result", )
            return MediatorResult.Error(RuntimeException("Unexpected chat list request result"))
        }

    }

    private fun ChatInfo.toDbChatListItem(): DbChatListItem {
        return DbChatListItem(
            chatId = this.chatId,
            chatOrder = this.chatOrder,
            chatName = this.chatName,
            chatType = this.chatType.id
        )
    }

    companion object {
        private const val TAG = "Mediator"
    }

}