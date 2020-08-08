package com.example.chatsample.data

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.chatsample.domain.ChatRepository
import com.example.chatsample.model.AuthResult
import kotlinx.coroutines.coroutineScope
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import javax.inject.Inject
import kotlin.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramChatRepositoryImpl @Inject constructor(
    private val contextProvider: ContextProvider
) : ChatRepository {

    private var client: Client? = null

    override fun init() {
    }

    override suspend fun authenticate(phoneNumber: String): AuthResult {
        var tdApiResult: TdApi.Object? = null

        Log.i(TAG, "authenticate() started")

        if (client == null) {
            tdApiResult = createClient()
            Log.i(TAG, "authenticate() create client result: $tdApiResult")
            tdApiResult = sendTdLibParams()
            tdApiResult = sendTdApiRequest(TdApi.CheckDatabaseEncryptionKey(byteArrayOf()))
        }

        sendTdApiRequest(TdApi.TerminateSession())

        sendTdApiRequest(TdApi.SetAuthenticationPhoneNumber(phoneNumber, TdApi.PhoneNumberAuthenticationSettings(
            false,
            true,
            false
        )))

//        if (tdApiResult is TdApi.UpdateAuthorizationState) {
//            if (tdApiResult.authorizationState.constructor == TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR) {
//                tdApiResult = sendTdLibParams()
//                Log.i(TAG, "authenticate() sendTdLibParams result: $tdApiResult")
//            }
//        }

        if (tdApiResult?.constructor == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
            return AuthResult.Ok()
        }

        if (tdApiResult?.constructor == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR) {
            return AuthResult.PasswordRequired()
        }

        throw RuntimeException("authenticate() failed. $tdApiResult")
    }

    override suspend fun authenticateWithCode(code: String): AuthResult {
        var tdApiResult: TdApi.Object = sendTdApiRequest(TdApi.CheckAuthenticationCode(code))

        if (tdApiResult.constructor == TdApi.Ok.CONSTRUCTOR) {
            return AuthResult.Ok()
        }

        throw RuntimeException("authenticateWithCode() failed. $tdApiResult ")
    }

    private suspend fun sendTdLibParams(): TdApi.Object {
        val context = contextProvider.appContext

        return sendTdApiRequest(TdApi.SetTdlibParameters(
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
            )))
    }

    private suspend fun sendTdApiRequest(function: TdApi.Function): TdApi.Object {
        return suspendCoroutine<TdApi.Object> { continuation ->
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
        return suspendCoroutine { continuation ->
            client = Client.create({ tdApiObject ->
                continuation.resume(tdApiObject)
            }, { continuation.resumeWithException(it) },  { continuation.resumeWithException(it) })
        }

    }

    companion object {
        private const val TAG = "TelegramChatRepository"
    }
}