package com.example.chatsample.chatlist.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.deleates_dsl.dsl.adapterLayoutContainer
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_name
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_type

fun directChatAdapterDelegate(itemClickedListener: (ChatListItem.Chat.Direct) -> Unit): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
    return adapterLayoutContainer<ChatListItem.Chat.Direct, ChatListItem>(R.layout.chat_list_item) {

        itemView.setOnClickListener { itemClickedListener(item) }

        bind {
            itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_direct)
            itemView.chat_list_item_name.text = item.name
        }
    }
}
