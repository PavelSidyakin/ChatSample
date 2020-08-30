package com.example.chatsample.chatlist.view.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import com.example.chatsample.model.ChatType
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_name
import kotlinx.android.synthetic.main.chat_list_item.view.chat_list_item_type
import kotlinx.android.synthetic.main.load_state_list_item.view.loading_item_error_text
import kotlinx.android.synthetic.main.load_state_list_item.view.loading_item_progress

class ChatListAdapter(private val clickListeners: ChatListClickListeners) : PagingDataAdapter<ChatListItem, ChatListItemViewHolder>(ChatListDiffUtilItemCallback()) {

    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {
        return ChatListItemViewHolder.create(parent, clickListeners)
    }
}

class ChatListItemViewHolder(view: View, private val clickListeners: ChatListClickListeners)
    : RecyclerView.ViewHolder(view) {

    fun bind(chatListItem: ChatListItem?) {
        if (chatListItem == null) {
            return
        }

        when (chatListItem) {
            is ChatListItem.Direct -> {
                itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_direct)
                itemView.chat_list_item_name.text = chatListItem.name
                itemView.setOnClickListener { clickListeners.directChatItemClickedListener(chatListItem) }
            }
            is ChatListItem.Group ->  {
                itemView.chat_list_item_type.text = itemView.context.getString(R.string.chat_type_group)
                itemView.chat_list_item_name.text = chatListItem.name
                itemView.setOnClickListener { clickListeners.groupChatItemClickedListener(chatListItem) }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, clickListeners: ChatListClickListeners): ChatListItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_list_item, parent, false)
            return ChatListItemViewHolder(view, clickListeners)
        }
    }

}

class LoadStateViewHolder(
    parent: ViewGroup,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.load_state_list_item, parent, false)
) {
    fun bind(loadState: LoadState) {
        itemView.loading_item_progress.isVisible = loadState is LoadState.Loading
        itemView.loading_item_error_text.isVisible = loadState is LoadState.Error
        itemView.loading_item_error_text.setOnClickListener { retry() }
    }
}

class ChatListLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState

    ) = LoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}
