package com.example.chatsample.auth.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.example.chatsample.model.AuthResult

class AuthReducer: Reducer<AuthStore.State, AuthStateChanges> {

    override fun AuthStore.State.reduce(result: AuthStateChanges): AuthStore.State {
        return when (result) {
            is AuthStateChanges.AuthenticationResult -> {
                when(result.authResult) {
                    is AuthResult.Ok -> copy(authCompleted = true)
                    is AuthResult.PasswordRequired -> copy(isWaitingForCode = true)
                }
            }
            is AuthStateChanges.Error -> copy(error = result.throwable)
            is AuthStateChanges.SendPhoneEnabled -> copy(sendPhoneNumberEnabled = result.enabled)
            is AuthStateChanges.SendCodeEnabled -> copy(sendCodeEnabled = result.enabled)
            is AuthStateChanges.SetCurrentPhoneNumber -> copy(currentPhoneNumber = result.phoneNumber)
        }
    }
}