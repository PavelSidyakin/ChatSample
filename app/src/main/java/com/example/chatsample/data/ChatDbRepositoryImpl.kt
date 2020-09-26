package com.example.chatsample.data

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.chatlist.store.data.ChatListDbRepository
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.utils.mapValue
import javax.inject.Inject

class ChatDbRepositoryImpl @Inject constructor(
    private val chatDb: ChatDb
) : ChatDbRepository {

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
            { dbChatListItem -> dbChatListItem.toChatInfo() },
            { chatInfo -> chatInfo.toDbChatListItem() }
        )
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