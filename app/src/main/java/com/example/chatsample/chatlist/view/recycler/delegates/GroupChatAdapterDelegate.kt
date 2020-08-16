package com.example.chatsample.chatlist.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_name
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_type

fun groupChatAdapterDelegate(itemClickedListener: (ChatListItem.Group) -> Unit): AdapterDelegate<List<ChatListItem>> {
    return adapterDelegateLayoutContainer<ChatListItem.Group, ChatListItem>(R.layout.chat_list_item) {

        itemView.setOnClickListener { itemClickedListener(item) }

        bind { diffPayloads ->
            itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_group)
            itemView.chat_list_item_name.text = item.name
        }
    }
}
