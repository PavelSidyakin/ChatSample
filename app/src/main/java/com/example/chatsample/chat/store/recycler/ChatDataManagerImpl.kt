package com.example.chatsample.chat.store.recycler

import android.os.SystemClock
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatsample.chat.model.MessageInfo
import com.example.chatsample.chat.model.MessageStatus
import com.example.chatsample.chat.store.data.ChatDbRepository
import com.example.chatsample.chat.store.data.ChatRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.delay

class ChatDataManagerImpl @Inject constructor(
    private val chatRemoteRepository: ChatRemoteRepository,
    private val chatDbRepository: ChatDbRepository,
) : ChatDataManager {

    private val pageListConfig =
        PagingConfig(
            pageSize = 10,
            initialLoadSize = 30
        )

    @ExperimentalPagingApi
    override fun observeMessageList(chatId: Long): Flow<PagingData<MessageInfo>> {
        return Pager(
            config = pageListConfig,
            pagingSourceFactory = { chatDbRepository.getAllMessages(chatId) },
            remoteMediator = ChatRemoteMediator(
                chatId,
                chatDbRepository,
                chatRemoteRepository,
                pageListConfig
            )
        )
            .flow
    }

    override suspend fun sendMessage(chatId: Long, message: String) {
        val temporaryId = Random(System.currentTimeMillis()).nextLong()

        // Add to the db in a separate transaction to update the list immediately
        chatDbRepository.insertMessage(
            chatId, MessageInfo.OutgoingMessage(
                chatId,
                Long.MAX_VALUE,
                message,
                MessageStatus.SENDING,
                temporaryId,
            )
        )

        delay(2000) // Just for test
        chatDbRepository.withTransaction {
            val sentMessage = chatRemoteRepository.sendMessage(chatId, message)
            chatDbRepository.deleteMessageWithTemporaryId(chatId, temporaryId)
            chatDbRepository.insertMessage(chatId, sentMessage)
        }
    }
}