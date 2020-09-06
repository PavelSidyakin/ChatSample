package com.example.chatsample.chatlist.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider
import com.hannesdorfmann.adapterdelegates4.dsl.adapterLayoutContainer

fun fallbackAdapterDelegate(): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
    return adapterLayoutContainer(R.layout.fallback_item_view) {

        bind { diffPayloads ->
        }
    }
}
