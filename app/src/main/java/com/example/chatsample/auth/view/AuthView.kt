package com.example.chatsample.auth.view

import com.arkivanov.mvikotlin.core.view.MviView
import com.example.chatsample.auth.store.AuthStore

interface AuthView : MviView<AuthStore.State, AuthStore.Intent> {
}