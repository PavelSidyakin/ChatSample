package com.example.chatsample.chatlist.view.recycler.delegates

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.chatlist.view.recycler.ChatListItem
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterItemProvider
import com.hannesdorfmann.adapterdelegates4.dsl.adapterLayoutContainer

fun loadingAdapterDelegate(itemClickedListener: () -> Unit): AdapterDelegate<AdapterItemProvider<ChatListItem>> {
//    return object : AdapterDelegate<AdapterItemProvider<ChatListItem>>() {
//        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
//            return object : RecyclerView.ViewHolder(
//                LayoutInflater.from(parent.context)
//                    .inflate(R.layout.load_state_list_item, parent, false)
//            ) {}
//        }
//
//        override fun isForViewType(p0: AdapterItemProvider<ChatListItem>, p1: Int): Boolean {
//            Log.i("Loading", "isForViewType() ${p0.getAdapterItem(p1)}")
//            return p0.getAdapterItem(p1) == null
//        }
//
//        override fun onBindViewHolder(p0: AdapterItemProvider<ChatListItem>, p1: Int, p2: RecyclerView.ViewHolder, p3: MutableList<Any>) {
//
//            p2.itemView.isVisible = p1 + 1 == p0.adapterItemCount
//        }
//
//
//    }

    return adapterLayoutContainer<ChatListItem.Loading, ChatListItem>(R.layout.load_state_list_item) {
        itemView.setOnClickListener { itemClickedListener() }

        bind { diffPayloads ->
        }

    }
}
