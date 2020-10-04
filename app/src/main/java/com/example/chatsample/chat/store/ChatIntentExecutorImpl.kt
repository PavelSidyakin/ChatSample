package com.example.chatsample.chat.store

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.store.recycler.ChatDataSource
import com.example.chatsample.chat.view.recycler.MessageListItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

class ChatIntentExecutorImpl @Inject constructor(
    private val chatDataSource: ChatDataSource
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
        chatDataSource.observeMessageList(chatId)
            .map { pagingData -> pagingData.map { convertMessageInfo2MessageListItem(it) } }
            .cachedIn(this)
            .collectLatest { pagingData: PagingData<MessageListItem> ->
                dispatch(ChatStateChanges.ListChanged(pagingData))
                dispatch(ChatStateChanges.RefreshStateChanged(false))
            }
    }

    private fun convertMessageInfo2MessageListItem(messageInfo: MessageInfo): MessageListItem {
        return when (messageInfo) {
            is MessageInfo.OutgoingMessage -> MessageListItem.Message.OutgoingMessage(messageInfo.chatId, messageInfo.messageId, messageInfo.messageText, messageInfo.messageStatus)
            is MessageInfo.IncomingMessage -> MessageListItem.Message.IncomingMessage(messageInfo.chatId, messageInfo.messageId, messageInfo.messageText, messageInfo.messageSenderName)
        }
    }

}