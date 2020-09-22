package com.example.chatsample.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.example.chatsample.chatlist.store.ChatListDbRepository
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.utils.mapValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatListDbRepositoryImpl @Inject constructor(
    private val chatDb: ChatDb
): ChatListDbRepository {

    private val chatsDao by lazy { chatDb.chats() }
    private val subChatListRemoteKeyDao by lazy { chatDb.subChatListRemoteKey() }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        chatDb.withTransaction {
            block()
        }
    }

    override suspend fun insertAll(chats: List<ChatInfo>) {
        chatsDao.insertAll(chats.map { it.toDbChatListItem() })
    }

    override suspend fun insertChat(chat: ChatInfo) {
        chatsDao.insertChat(chat.toDbChatListItem())
    }

    override fun getAllChats(): PagingSource<Int, ChatInfo> {
        return chatsDao.selectAllChats().mapValue(
                { dbChatListItem ->
                    ChatInfo(dbChatListItem.chatId, dbChatListItem.chatName,
                        ChatType.byId(dbChatListItem.chatType),
                        dbChatListItem.chatOrder)
                },
                { chatInfo ->
                    chatInfo.toDbChatListItem()
                }
            )
//        return object : PagingSource<Int, ChatInfo>() {
//            override suspend fun load(
//                params: LoadParams<Int>
//            ): LoadResult<Int, ChatInfo> {
//                return try {
//                    // Start refresh at page 1 if undefined.
//                    val nextPageNumber = params.key ?: 1
//                    val response = chatsDao.selectAllChatsList()
//
//                    LoadResult.Page(
//                        data = response.map { it.toChatInfo() },
//                        prevKey = null,
//                        nextKey = nextPageNumber + 1 //subChatListRemoteKeyDao.remoteKey().nextChatToken.isBlank()
//                    )
//                } catch (e: Exception) {
//                    // Handle errors in this block and return LoadResult.Error if it is an
//                    // expected error (such as a network failure).
//                    LoadResult.Error(e)
//                }
//            }
//        }
    }

    override suspend fun deleteAllChats() {
        chatsDao.deleteAllChats()
    }

    override suspend fun setNextRemoteKey(key: String) {
        subChatListRemoteKeyDao.insert(DbSubChatListRemoteKey(key))
    }

    override suspend fun getNextRemoteKey(): String? {
        return subChatListRemoteKeyDao.remoteKey()?.nextChatToken
    }

    override suspend fun deleteRemoteKey() {
        subChatListRemoteKeyDao.deleteRemoteKey()
    }

    private fun ChatInfo.toDbChatListItem(): DbChatListItem {
        return DbChatListItem(
            chatId = this.chatId,
            chatOrder = this.chatOrder,
            chatName = this.chatName,
            chatType = this.chatType.id
        )
    }

    private fun DbChatListItem.toChatInfo(): ChatInfo {
        return ChatInfo(
            chatId = this.chatId,
            chatOrder = this.chatOrder,
            chatName = this.chatName,
            chatType = ChatType.byId(this.chatType)
        )
    }

}