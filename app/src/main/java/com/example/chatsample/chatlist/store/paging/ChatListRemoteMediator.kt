package com.example.chatsample.chatlist.store.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.chatsample.data.ChatDb
import com.example.chatsample.data.DbChatListItem
import com.example.chatsample.data.DbSubChatListRemoteKey
import com.example.chatsample.model.ChatType
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.repository.ChatNetworkRepository
import com.example.chatsample.utils.parseString
import com.example.chatsample.utils.serializeToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@ExperimentalPagingApi
class ChatListRemoteMediator(
    private val coroutineScope: CoroutineScope,
    private val db: ChatDb,
    private val chatNetworkRepository: ChatNetworkRepository,
    private val loadingChannel: Channel<Boolean>
) : RemoteMediator<Int, DbChatListItem>() {
    private val chatsDao = db.chats()
    private val subChatListRemoteKeyDao = db.subChatListRemoteKey()

    override suspend fun initialize(): InitializeAction {
        try {
            Log.w("Mediator", "initialize() sending")
            loadingChannel.offer(true)
            Log.w("Mediator", "initialize() inserting")
//            db.withTransaction {
                //chatsDao.deleteAllChats()
                // Workaround to add loading item correctly
//                chatsDao.insertChat(DbChatListItem(
//                    chatId = Long.MAX_VALUE,
//                    chatOrder = Long.MIN_VALUE,
//                    chatName = "Loading...",
//                    chatType = ChatType.LOADING.id)
//                )
//            }
            Log.w("Mediator", "initialize() inserted")

        } catch (th: Throwable) {
            Log.w("Mediator", "initialize()", th)
        }
        return super.initialize()
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, DbChatListItem>): MediatorResult {
        try {
            var loadKey: NextChatListInfo? = null

            when (loadType) {
                LoadType.REFRESH -> {
                }
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

            loadingChannel.offer(true)
            val networkChatListResult = chatNetworkRepository.requestChatList(
                nextInfo = loadKey,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )
            Log.i("Mediator", "network result: $networkChatListResult")
            Log.i("Mediator", "sending loading state...")
            loadingChannel.offer(false)
            Log.i("Mediator", "loading state is sent")

            if (networkChatListResult is RequestChatListResult.Ok) {
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        chatsDao.deleteAllChats()
                    }

                    val nextStringToken = serializeToString(networkChatListResult.nextChatListInfo)

                    subChatListRemoteKeyDao.deleteRemoteKey()
                    subChatListRemoteKeyDao.insert(DbSubChatListRemoteKey(nextStringToken ?: ""))

                    val chats = networkChatListResult.chats.toMutableList()
//                    // Workaround to add loading item correctly
//                    chats.add(ChatInfo(
//                        0,
//                        "Loading...",
//                        ChatType.DIRECT,
//                        chatOrder = Long.MIN_VALUE, // Loading item is always last
//                        isLoadingItem = true
//                    ))
//
                    chatsDao.insertAll(chats.map {
                        DbChatListItem(
                            chatId = it.chatId,
                            chatOrder = it.chatOrder,
                            chatName = it.chatName,
                            chatType = it.chatType.id
                        )
                    })

                    Log.i("Mediator", "inserted in DB: $chats")

                }

                return MediatorResult.Success(endOfPaginationReached = networkChatListResult.chats.isEmpty())

            } else {
                return MediatorResult.Error(RuntimeException("Unexpected chat list request result"))
            }
        } catch (e: Throwable) {
            loadingChannel.offer(false)
            Log.w("Mediator", "load", e)
            return MediatorResult.Error(e)
        }
    }


}