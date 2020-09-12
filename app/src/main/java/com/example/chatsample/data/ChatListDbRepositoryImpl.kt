package com.example.chatsample.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.example.chatsample.chatlist.store.ChatListDbRepository
import com.example.chatsample.model.ChatInfo
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

    override fun getAllChats(): PagingSource<Int, DbChatListItem> {
        return chatsDao.selectAllChats()
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
}