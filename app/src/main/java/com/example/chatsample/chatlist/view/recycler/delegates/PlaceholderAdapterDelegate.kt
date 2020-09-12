package com.example.chatsample.chatlist.view.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.example.chatsample.deleates_dsl.dsl.AdapterDelegateLayoutContainerViewHolder
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider

fun placeHolderAdapterDelegate(): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
    return object : AdapterDelegate<AdapterItemProvider<ChatListItem>>() {
        override fun onCreateViewHolder(parent: ViewGroup): AdapterDelegateLayoutContainerViewHolder<ChatListItem> {
            return AdapterDelegateLayoutContainerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fallback_item_view, parent, false)
            )
        }

        override fun isForViewType(adapterItemProvider: AdapterItemProvider<ChatListItem>, position: Int): Boolean {
            return adapterItemProvider.getAdapterItem(position) == null
        }

        override fun onBindViewHolder(p0: AdapterItemProvider<ChatListItem>, p1: Int, p2: RecyclerView.ViewHolder, p3: MutableList<Any>) {
        }

    }
}
