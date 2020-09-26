package com.example.chatsample.chat.view.recycler.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.chat.view.recycler.MessageItem
import com.example.chatsample.deleates_dsl.dsl.AdapterDelegateLayoutContainerViewHolder
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider

fun placeHolderAdapterDelegate(): AdapterDelegate<AdapterItemProvider<MessageItem>> {
    return object : AdapterDelegate<AdapterItemProvider<MessageItem>>() {
        override fun onCreateViewHolder(parent: ViewGroup): AdapterDelegateLayoutContainerViewHolder<MessageItem> {
            return AdapterDelegateLayoutContainerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fallback_item_view, parent, false)
            )
        }

        override fun isForViewType(adapterItemProvider: AdapterItemProvider<MessageItem>, position: Int): Boolean {
            return adapterItemProvider.getAdapterItem(position) == null
        }

        override fun onBindViewHolder(p0: AdapterItemProvider<MessageItem>, p1: Int, p2: RecyclerView.ViewHolder, p3: MutableList<Any>) {
        }

    }
}
