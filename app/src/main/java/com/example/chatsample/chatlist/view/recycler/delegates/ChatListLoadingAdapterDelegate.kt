package com.example.chatsample.chatlist.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.deleates_dsl.dsl.adapterLayoutContainer
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider

fun loadingAdapterDelegate(itemClickedListener: () -> Unit): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
    return adapterLayoutContainer<ChatListItem.Loading, ChatListItem>(R.layout.load_state_list_item) {
        itemView.setOnClickListener { itemClickedListener() }
    }
}
