package com.example.chatsample.chatlist.view.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.chatsample.chatlist.view.recycler.delegates.directChatAdapterDelegate
import com.example.chatsample.chatlist.view.recycler.delegates.groupChatAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.paging.PagedListDelegationAdapter

class ChatListAdapter(clickListeners: ChatListClickListeners) : PagedListDelegationAdapter<ChatListItem>(ChatListDiffUtilItemCallback(),
    directChatAdapterDelegate(clickListeners.directChatItemClickedListener),
    groupChatAdapterDelegate(clickListeners.groupChatItemClickedListener)
)

interface ChatListClickListeners {
    val directChatItemClickedListener: (ChatListItem.Direct) -> Unit
    val groupChatItemClickedListener: (ChatListItem.Group) -> Unit
}

class ChatListDiffUtilItemCallback : DiffUtil.ItemCallback<ChatListItem>() {

    override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem == newItem
    }

}
