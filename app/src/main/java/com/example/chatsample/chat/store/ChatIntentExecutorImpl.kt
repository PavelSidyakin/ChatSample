package com.example.chatsample.chat.store

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.store.recycler.ChatDataManager
import com.example.chatsample.chat.view.recycler.MessageListItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

class ChatIntentExecutorImpl @Inject constructor(
    private val chatDataManager: ChatDataManager
) : SuspendExecutor<ChatStore.Intent, ChatBootstrapper.Action, ChatStore.State, ChatStateChanges, ChatStore.Label>(
    mainContext = Dispatchers.Main
), ChatIntentExecutor {

    private var chatId: Long = 0

    override suspend fun executeAction(action: ChatBootstrapper.Action, getState: () -> ChatStore.State) {
        when (action) {
            is ChatBootstrapper.Action.LoadMessageList -> handleActionLoadMessageList(action.chatId)
        }
    }

    override suspend fun executeIntent(intent: ChatStore.Intent, getState: () -> ChatStore.State) {
        when (intent) {
            is ChatStore.Intent.Refresh -> dispatch(ChatStateChanges.RefreshStateChanged(true))
            is ChatStore.Intent.Retry -> dispatch(ChatStateChanges.RefreshStateChanged(true))
            is ChatStore.Intent.SendMessage -> handleSendMessage(getState())
            is ChatStore.Intent.OutgoingMessageText -> {
                dispatch(ChatStateChanges.CurrentlyEditingTextChanged(intent.message))
            }
        }
    }

    private suspend fun handleSendMessage(state: ChatStore.State) {
        dispatch(ChatStateChanges.CurrentlyEditingTextChanged(""))
        chatDataManager.sendMessage(chatId, state.currentlyEditingMessage)
    }

    private suspend fun handleActionLoadMessageList(chatId: Long) = coroutineScope {
        this@ChatIntentExecutorImpl.chatId = chatId
        chatDataManager.observeMessageList(chatId)
            .map { pagingData -> pagingData.map { convertMessageInfo2MessageListItem(it) } }
            .cachedIn(this)
            .collectLatest { pagingData: PagingData<MessageListItem> ->
                dispatch(ChatStateChanges.ListChanged(pagingData))
                dispatch(ChatStateChanges.RefreshStateChanged(false))
            }
    }

    private fun convertMessageInfo2MessageListItem(messageInfo: MessageInfo): MessageListItem {
        return when (messageInfo) {
            is MessageInfo.OutgoingMessage -> MessageListItem.Message.OutgoingMessage(messageInfo.chatId, messageInfo.messageId, messageInfo.messageText, messageInfo.messageStatus, messageInfo.messageTemporaryId)
            is MessageInfo.IncomingMessage -> MessageListItem.Message.IncomingMessage(messageInfo.chatId, messageInfo.messageId, messageInfo.messageText, messageInfo.messageSenderName)
        }
    }

}