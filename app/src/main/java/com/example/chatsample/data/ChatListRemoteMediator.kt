package com.example.chatsample.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.repository.ChatNetworkRepository
import com.example.chatsample.utils.parseString
import com.example.chatsample.utils.serializeToString
import kotlinx.coroutines.delay

@ExperimentalPagingApi
class ChatListRemoteMediator(
    private val db: ChatDb,
    private val chatNetworkRepository: ChatNetworkRepository
) : RemoteMediator<Int, DbChatListItem>() {
    private val chatsDao = db.chats()
    private val subChatListRemoteKeyDao = db.subChatListRemoteKey()

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

            val networkChatListResult = chatNetworkRepository.requestChatList(
                nextInfo = loadKey,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
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

                    chatsDao.insertAll(networkChatListResult.chats.map {
                        DbChatListItem(
                            chatId = it.chatId,
                            chatOrder = it.chatOrder,
                            chatName = it.chatName,
                            chatType = it.chatType.id
                        )
                    })
                }

                return MediatorResult.Success(endOfPaginationReached = networkChatListResult.chats.isEmpty())

            } else {
                return MediatorResult.Error(RuntimeException("Unexpected chat list request result"))
            }
        } catch (e: Throwable) {
            return MediatorResult.Error(e)
        }
    }


}