package com.example.chatsample.chatlist.store.paging

import androidx.paging.DataSource
import com.example.chatsample.chatlist.store.ChatListStateChanges
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.data.ChatRepository
import com.example.chatsample.model.NextChatListInfo
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

class ChatListDataSourceFactory(
    private val dispatchStateChangesCallback: (changes: ChatListStateChanges) -> Unit,
    private val chatRepository: ChatRepository,
    private val coroutineContext: CoroutineContext,
    private val retryChannel: Channel<Any>
) : DataSource.Factory<NextChatListInfo, ChatListItem>() {
    override fun create(): DataSource<NextChatListInfo, ChatListItem> {
        return ChatListDataSource(dispatchStateChangesCallback, chatRepository, coroutineContext, retryChannel)
    }
}