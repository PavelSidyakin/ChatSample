package com.example.chatsample.chatlist.view.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.chatsample.chatlist.view.recycler.delegates.directChatAdapterDelegate
import com.example.chatsample.chatlist.view.recycler.delegates.fallbackAdapterDelegate
import com.example.chatsample.chatlist.view.recycler.delegates.groupChatAdapterDelegate
import com.example.chatsample.chatlist.view.recycler.delegates.loadingAdapterDelegate
import com.example.chatsample.chatlist.view.recycler.delegates.placeHolderAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.paging3.PagingDelegationAdapter

class ChatListDelegationAdapter(clickListeners: ChatListClickListeners) : PagingDelegationAdapter<ChatListItem>(ChatListDiffUtilItemCallback(),
    placeHolderAdapterDelegate(),
    directChatAdapterDelegate(clickListeners.directChatItemClickedListener),
    groupChatAdapterDelegate(clickListeners.groupChatItemClickedListener),
    loadingAdapterDelegate(clickListeners.loadingItemClickListener),
) {
    init {
        delegatesManager.fallbackDelegate = fallbackAdapterDelegate()
    }

    override fun toString(): String {
        return "items: ${snapshot()}"
    }
}

interface ChatListClickListeners {
    val loadingItemClickListener: () -> Unit
    val directChatItemClickedListener: (ChatListItem.Chat.Direct) -> Unit
    val groupChatItemClickedListener: (ChatListItem.Chat.Group) -> Unit
}

class ChatListDiffUtilItemCallback : DiffUtil.ItemCallback<ChatListItem>() {

    override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        if (oldItem is ChatListItem.Chat && newItem is ChatListItem.Chat) {
            return oldItem.chatId == newItem.chatId
        }
        return false
    }

    override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem == newItem
    }
}
