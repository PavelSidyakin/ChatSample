package com.example.chatsample.chatlist.view.recycler.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider

fun placeHolderAdapterDelegate(): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
    return object : AdapterDelegate<AdapterItemProvider<ChatListItem>>() {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fallback_item_view, parent, false)
            ) {}
        }

        override fun isForViewType(p0: AdapterItemProvider<ChatListItem>, p1: Int): Boolean {
            return p0.getAdapterItem(p1) == null
        }

        override fun onBindViewHolder(p0: AdapterItemProvider<ChatListItem>, p1: Int, p2: RecyclerView.ViewHolder, p3: MutableList<Any>) {
        }

    }
}
