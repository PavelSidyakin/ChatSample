package com.example.chatsample.auth.store

import com.arkivanov.mvikotlin.core.store.Store

interface AuthStore : Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> {

    sealed class Intent {
        data class TypingPhoneNumber(val phoneNumber: String) : Intent()
        data class TypingCode(val code: String) : Intent()
        data class SendPhoneNumber(val phoneNumber: String) : Intent()
        data class SendCode(val code: String) : Intent()
    }

    data class State(
        val currentPhoneNumber: String = "",
        val authCompleted: Boolean = false,
        val isWaitingForCode: Boolean = false,
        val sendPhoneNumberEnabled: Boolean = false,
        val sendCodeEnabled: Boolean = false,
        val error: Throwable? = null
    )

    sealed class Label {
    }
}