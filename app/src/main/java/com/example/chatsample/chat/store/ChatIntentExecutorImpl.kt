package com.example.chatsample.chat.store

import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chat.store.recycler.ChatDataSource
import com.example.chatsample.chatlist.store.recycler.ChatListDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ChatIntentExecutorImpl @Inject constructor(
    private val chatListDataSource: ChatDataSource
) : SuspendExecutor<ChatStore.Intent, ChatBootstrapper.Action, ChatStore.State, ChatStateChanges, ChatStore.Label>(
    mainContext = Dispatchers.Main
), ChatIntentExecutor {

    override suspend fun executeAction(action: ChatBootstrapper.Action, getState: () -> ChatStore.State) {
        when (action) {
            is ChatBootstrapper.Action.LoadMessageList -> handleActionLoadMessageList(action.chatId)
        }
    }

    override suspend fun executeIntent(intent: ChatStore.Intent, getState: () -> ChatStore.State) {
        when (intent) {
            is ChatStore.Intent.Refresh -> dispatch(ChatStateChanges.RefreshStateChanged(true))
            is ChatStore.Intent.Retry -> dispatch(ChatStateChanges.RefreshStateChanged(true))
        }
    }

    private suspend fun handleActionLoadMessageList(chatId: Long) = coroutineScope {
//        chatListDataSource.observeChatList()
//            .map { pagingData -> pagingData.map { convertChatInfo2ChatListItem(it) } }
//            .cachedIn(this)
//            .collectLatest { pagingData: PagingData<MessageItem> ->
//                dispatch(ChatStateChanges.ListChanged(pagingData))
//                dispatch(ChatStateChanges.RefreshStateChanged(false))
//            }
    }

}