package com.example.chatsample.data

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.chatsample.model.AuthResult
import com.example.chatsample.model.ChatInfo
import com.example.chatsample.model.ChatType
import com.example.chatsample.model.NextChatListInfo
import com.example.chatsample.model.RequestChatListResult
import com.example.chatsample.model.UpdateChatListEvent
import com.example.chatsample.model.UpdateChatListEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import javax.inject.Inject
import kotlin.RuntimeException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramChatRepositoryImpl @Inject constructor(
    private val contextProvider: ContextProvider
) : ChatRepository, CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private var client: Client? = null

    private val updatesChannel = Channel<TdApi.Object>()

    private val updatesHandler = object : Client.ResultHandler {
        override fun onResult(o: TdApi.Object?) {
            Log.i(TAG, "UpdatesHandler received $o")
            o?.let {
                launch {
                    updatesChannel.send(it)
                }
            }
        }

    }

    private val exceptionHandler = object : Client.ExceptionHandler {
        override fun onException(e: Throwable?) {
            Log.w(TAG, e)
        }

    }

    override fun init() {
    }

    override suspend fun authenticate(phoneNumber: String): AuthResult {
        var tdApiResult: TdApi.Object? = null

        Log.i(TAG, "authenticate() started")

        tdApiResult = sendTdApiRequest(TdApi.GetAuthorizationState())

        if (tdApiResult is TdApi.AuthorizationStateReady) {
            return AuthResult.Ok() // Already is authenticated
        }

        sendTdApiRequest(TdApi.TerminateSession())

        sendTdApiRequest(
            TdApi.SetAuthenticationPhoneNumber(
                phoneNumber, TdApi.PhoneNumberAuthenticationSettings(
                    false,
                    true,
                    false
                )
            )
        )

//        if (tdApiResult is TdApi.UpdateAuthorizationState) {
//            if (tdApiResult.authorizationState.constructor == TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR) {
//                tdApiResult = sendTdLibParams()
//                Log.i(TAG, "authenticate() sendTdLibParams result: $tdApiResult")
//            }
//        }

        if (tdApiResult.constructor == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
            return AuthResult.Ok()
        }

        if (tdApiResult.constructor == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR) {
            return AuthResult.PasswordRequired()
        }

        throw RuntimeException("authenticate() failed. $tdApiResult")
    }

    override suspend fun continueWithCode(code: String): AuthResult {
        val tdApiResult: TdApi.Object = sendTdApiRequest(TdApi.CheckAuthenticationCode(code))

        if (tdApiResult.constructor == TdApi.Ok.CONSTRUCTOR) {
            return AuthResult.Ok()
        }

        throw RuntimeException("authenticateWithCode() failed. $tdApiResult ")
    }

    private suspend fun sendTdLibParams(): TdApi.Object {
        val context = contextProvider.appContext

        return sendTdApiRequest(
            TdApi.SetTdlibParameters(
                TdApi.TdlibParameters(
                    false,
                    context.getDir("telegram_db", Context.MODE_PRIVATE).absolutePath,
                    context.getDir("telegram_files", Context.MODE_PRIVATE).absolutePath,
                    false,
                    false,
                    false,
                    false,
                    API_ID,
                    API_HASH,
                    "en-US",
                    Build.MODEL,
                    Build.VERSION.RELEASE,
                    "1.0",
                    false,
                    false
                )
            )
        )
    }

    private suspend fun sendTdApiRequest(function: TdApi.Function): TdApi.Object {
        if (client == null) {
            var tdApiResult = createClient()
            Log.i(TAG, "sendTdApiRequest() create client result: $tdApiResult")
            tdApiResult = sendTdLibParams()
            Log.i(TAG, "sendTdApiRequest() sendTdLibParams result: $tdApiResult")
            tdApiResult = sendTdApiRequest(TdApi.CheckDatabaseEncryptionKey(byteArrayOf()))
            Log.i(TAG, "sendTdApiRequest() CheckDatabaseEncryptionKey result: $tdApiResult")
        }

        return suspendCoroutine { continuation ->
            Log.i(TAG, "sendTdApiRequest() sending: $function")
            client?.send(function, {
                Log.i(TAG, "sendTdApiRequest() result: $it")
                continuation.resume(it)
            }, {
                continuation.resumeWithException(it)
            })
        }
    }

    private suspend fun createClient(): TdApi.Object {
        client = Client.create(updatesHandler, exceptionHandler, exceptionHandler)

        return updatesChannel.receive()
    }

    override suspend fun requestInitialChatList(limit: Int): RequestChatListResult {
        val chatMap = mutableMapOf<Long, ChatInfo>()

        val requestInitialChatListResult = sendTdApiRequest(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, limit))

        var lastId = 0L
        if (requestInitialChatListResult is TdApi.Chats) {
            var lastOrder = Long.MAX_VALUE
            lastId = requestInitialChatListResult.chatIds.last()

            for (i: Long in requestInitialChatListResult.chatIds) {

                val chatObject = sendTdApiRequest(TdApi.GetChat(i)) as TdApi.Chat

                chatMap[i] = (ChatInfo(chatObject.title, convertTdChatType2ChatType(chatObject.type)))

                if (i == lastId) {
                    lastOrder = chatObject.order
                }
            }

            return RequestChatListResult.Ok(chatMap, NextChatListInfo(lastOrder, lastId))
        }

        throw RuntimeException("requestInitialChatList() failed")
    }

    override suspend fun requestNextChatList(nextInfo: NextChatListInfo, limit: Int): RequestChatListResult {
        val chatMap = mutableMapOf<Long, ChatInfo>()

        val requestInitialChatListResult = sendTdApiRequest(TdApi.GetChats(TdApi.ChatListMain(), nextInfo.order, nextInfo.chatId, limit))

        var lastId = 0L
        if (requestInitialChatListResult is TdApi.Chats) {
            var lastOrder = Long.MAX_VALUE
            lastId = requestInitialChatListResult.chatIds.last()

            for (i: Long in requestInitialChatListResult.chatIds) {

                val chatObject = sendTdApiRequest(TdApi.GetChat(i)) as TdApi.Chat

                chatMap[i] = (ChatInfo(chatObject.title, convertTdChatType2ChatType(chatObject.type)))

                if (i == lastId) {
                    lastOrder = chatObject.order
                }
            }

            return RequestChatListResult.Ok(chatMap, NextChatListInfo(lastOrder, lastId))
        }

        throw RuntimeException("requestInitialChatList() failed")
    }

    private fun convertTdChatType2ChatType(tdChatType: TdApi.ChatType): ChatType {
        return when (tdChatType) {
            is TdApi.ChatTypeBasicGroup -> ChatType.GROUP
            is TdApi.ChatTypeSupergroup -> ChatType.GROUP
            else -> ChatType.DIRECT
        }
    }

    override suspend fun subscribeChatListUpdates(): Channel<UpdateChatListEvent> {
        val chatListUpdatesChannel = Channel<UpdateChatListEvent>()

        val tdApiObject = updatesChannel.receive()

        if (tdApiObject is TdApi.UpdateNewChat) {

            chatListUpdatesChannel.send(
                UpdateChatListEvent(
                    UpdateChatListEventType.ADDED,
                    tdApiObject.chat.id,
                    ChatInfo(tdApiObject.chat.title, convertTdChatType2ChatType(tdApiObject.chat.type))
                )
            )
        }

        return chatListUpdatesChannel
    }


    protected fun finalize() {
        coroutineContext.cancel()
    }

    companion object {
        private const val TAG = "TelegramChatRepository"
    }
}