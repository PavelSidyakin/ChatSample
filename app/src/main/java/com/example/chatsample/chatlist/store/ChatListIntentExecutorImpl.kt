package com.example.chatsample.chatlist.store

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.chatlist.store.recycler.ChatDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatListIntentExecutorImpl @Inject constructor(
    private val chatDataSource: ChatDataSource
) : SuspendExecutor<ChatListStore.Intent, ChatListBootstrapper.Action, ChatListStore.State, ChatListStateChanges, ChatListStore.Label>(
    mainContext = Dispatchers.Main
), ChatListIntentExecutor {

    override suspend fun executeAction(action: ChatListBootstrapper.Action, getState: () -> ChatListStore.State) {
        when (action) {
            is ChatListBootstrapper.Action.LoadList -> handleActionLoadList()
        }
    }

    override suspend fun executeIntent(intent: ChatListStore.Intent, getState: () -> ChatListStore.State) {
        when (intent) {
            is ChatListStore.Intent.Refresh -> dispatch(ChatListStateChanges.RefreshStateChanged(true))
        }
    }

    private suspend fun handleActionLoadList() = coroutineScope {
        dispatch(ChatListStateChanges.ListChanged(PagingData.from(listOf(ChatListItem.Loading()))))

        chatDataSource.observeChatList()
            .map { pagingData -> pagingData.map { convertChatInfo2ChatListItem(it) } }
            .cachedIn(this)
            .collectLatest { pagingData ->

                dispatch(ChatListStateChanges.ListChanged(pagingData))
                dispatch(ChatListStateChanges.RefreshStateChanged(false))
            }
    }

    private fun convertChatInfo2ChatListItem(chatInfo: ChatInfo): ChatListItem {
        return when (chatInfo.chatType) {
            ChatType.DIRECT -> ChatListItem.Chat.Direct(chatInfo.chatId, chatInfo.chatName)
            ChatType.GROUP -> ChatListItem.Chat.Group(chatInfo.chatId, chatInfo.chatName)
        }
    }

}