package com.example.chatsample.chatlist.store

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.data.DbChatListItem
import com.example.chatsample.data.DbSubChatListRemoteKey
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import kotlinx.coroutines.flow.Flow

interface ChatListDbRepository {
    suspend fun insertAll(chats: List<ChatInfo>)

    suspend fun insertChat(chat: ChatInfo)

    fun getAllChats(): PagingSource<Int, ChatInfo>

    suspend fun deleteAllChats()

    suspend fun setNextRemoteKey(key: String)

    suspend fun getNextRemoteKey(): String?

    suspend fun deleteRemoteKey()

    suspend fun withTransaction(block: suspend () -> Unit)

}