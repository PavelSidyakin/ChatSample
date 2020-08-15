package com.example.chatsample.auth.store

import com.arkivanov.mvikotlin.core.store.Executor

interface AuthIntentExecutor: Executor<AuthStore.Intent, Unit, AuthStore.State, AuthStateChanges, AuthStore.Label>