package com.example.chatsample.auth.store

import com.example.chatsample.model.AuthResult

sealed class AuthStateChanges {
    data class AuthenticationResult(val authResult: AuthResult) : AuthStateChanges()
    data class SendPhoneEnabled(val enabled: Boolean) : AuthStateChanges()
    data class SendCodeEnabled(val enabled: Boolean) : AuthStateChanges()
    data class Error(val throwable: Throwable) : AuthStateChanges()
}