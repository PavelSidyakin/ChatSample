package com.example.chatsample.auth.store

import android.util.Log
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.example.chatsample.data.ChatRepository
import com.example.chatsample.model.RequestChatListResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.drinkless.td.libcore.telegram.TdApi

class AuthIntentExecutor(
    private val chatRepository: ChatRepository
) : SuspendExecutor<AuthStore.Intent, Unit, AuthStore.State, AuthStateChanges, AuthStore.Label>(
        mainContext = Dispatchers.Main
) {
    override suspend fun executeIntent(
        intent: AuthStore.Intent,
        getState: () -> AuthStore.State
    ) = when (intent) {
            is AuthStore.Intent.SendPhoneNumber -> handleSendPhoneNumber(intent.phoneNumber)
            is AuthStore.Intent.SendCode -> handleSendCode(intent.code)
            is AuthStore.Intent.TypingPhoneNumber -> handleTypingPhoneNumber(intent.phoneNumber)
            is AuthStore.Intent.TypingCode -> handleTypingCode(intent.code)
    }

    private suspend fun handleTypingCode(code: String) {
        dispatch(AuthStateChanges.SendCodeEnabled(code.isNotBlank()))
    }

    private suspend fun handleTypingPhoneNumber(phoneNumber: String) {
        dispatch(AuthStateChanges.SendPhoneEnabled(phoneNumber.isNotBlank()))
    }

    private suspend fun handleSendCode(code: String) {
        try {
            val result = withContext(Dispatchers.IO) {
                    return@withContext chatRepository.continueWithCode(code)
                }
            dispatch(AuthStateChanges.AuthenticationResult(result))
        } catch (throwable: Throwable) {
            dispatch(AuthStateChanges.Error(throwable))
        }
    }

    private suspend fun handleSendPhoneNumber(phoneNumber: String) {
        try {
            val result =
                withContext(Dispatchers.IO) {
                    val initialResult: RequestChatListResult = chatRepository.requestInitialChatList(5)
                    Log.i("Chats", "Initial ${chatRepository.requestInitialChatList(5)}")
                    Log.i("Chats", "Next ${chatRepository.requestNextChatList((initialResult as RequestChatListResult.Ok).nextChatListInfo, 5)}")
                    return@withContext chatRepository.authenticate(phoneNumber)
                }
            dispatch(AuthStateChanges.AuthenticationResult(result))
        } catch (throwable: Throwable) {
            dispatch(AuthStateChanges.Error(throwable))
        }

        //publish(AuthStore.Label.Dispatched(result.id.toString())) // WTF ??
    }
}