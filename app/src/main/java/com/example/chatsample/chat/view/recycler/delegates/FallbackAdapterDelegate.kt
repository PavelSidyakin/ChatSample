package com.example.chatsample.chat.view.recycler.delegates

import com.example.chatsample.R
import com.example.chatsample.chat.view.recycler.MessageListItem
import com.example.chatsample.deleates_dsl.dsl.adapterLayoutContainer
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider

fun fallbackAdapterDelegate(): AdapterDelegate<AdapterItemProvider<MessageListItem>> {
    return adapterLayoutContainer(R.layout.fallback_item_view) {
    }
}
