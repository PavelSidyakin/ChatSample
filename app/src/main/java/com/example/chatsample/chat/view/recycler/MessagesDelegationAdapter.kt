package com.example.chatsample.chat.view.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.chatsample.chat.view.recycler.delegates.fallbackAdapterDelegate
import com.example.chatsample.chat.view.recycler.delegates.incomingMessageAdapterDelegate
import com.example.chatsample.chat.view.recycler.delegates.outgoingMessageAdapterDelegate
import com.example.chatsample.chat.view.recycler.delegates.placeHolderAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.paging3.PagingDelegationAdapter

class MessagesDelegationAdapter(clickListeners: MessagesClickListeners) : PagingDelegationAdapter<MessageListItem>(
    MessageDiffUtilItemCallback(),
    placeHolderAdapterDelegate(),
    incomingMessageAdapterDelegate(),
    outgoingMessageAdapterDelegate(clickListeners.onRetrySendClicked),
) {
    init {
        delegatesManager.fallbackDelegate = fallbackAdapterDelegate()
    }

    override fun toString(): String {
        return "items: ${snapshot()}"
    }
}

interface MessagesClickListeners {
    val onRetrySendClicked: (MessageListItem.Message.OutgoingMessage) -> Unit
}

class MessageDiffUtilItemCallback : DiffUtil.ItemCallback<MessageListItem>() {

    override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
        if (oldItem is MessageListItem.Message.OutgoingMessage && newItem is MessageListItem.Message.OutgoingMessage) {
            return oldItem.chatId == newItem.chatId
                    && oldItem.messageId == newItem.messageId
                    && oldItem.temporaryId == newItem.temporaryId
        }
        if (oldItem is MessageListItem.Message && newItem is MessageListItem.Message) {
            return oldItem.chatId == newItem.chatId
                    && oldItem.messageId == newItem.messageId
        }
        return false
    }

    override fun areContentsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
        return oldItem == newItem
    }
}
