package com.example.chatsample.chat.view.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.chatsample.chat.view.recycler.delegates.fallbackAdapterDelegate
import com.example.chatsample.chat.view.recycler.delegates.placeHolderAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.paging3.PagingDelegationAdapter

class MessagesDelegationAdapter(clickListeners: MessagesClickListeners) : PagingDelegationAdapter<MessageItem>(
    ChatDiffUtilItemCallback(),
    placeHolderAdapterDelegate(),
) {
    init {
        delegatesManager.fallbackDelegate = fallbackAdapterDelegate()
    }

    override fun toString(): String {
        return "items: ${snapshot()}"
    }
}

interface MessagesClickListeners {
    val loadingItemClickListener: () -> Unit
    val onDeleteMessageClicked: (MessageItem.Message) -> Unit
}

class ChatDiffUtilItemCallback : DiffUtil.ItemCallback<MessageItem>() {

    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        if (oldItem is MessageItem.Message && newItem is MessageItem.Message) {
            return oldItem.messageId == newItem.messageId
        }
        return false
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem == newItem
    }
}
