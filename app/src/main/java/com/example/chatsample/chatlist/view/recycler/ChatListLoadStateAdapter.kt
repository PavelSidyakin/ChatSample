package com.example.chatsample.chatlist.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsample.R
import kotlinx.android.synthetic.main.load_state_list_item.view.loading_item_error_text
import kotlinx.android.synthetic.main.load_state_list_item.view.loading_item_progress

// It's impossible to add loading item as item in the list to use a delegate.
// Use this solution as suggested by paging library 3.
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
