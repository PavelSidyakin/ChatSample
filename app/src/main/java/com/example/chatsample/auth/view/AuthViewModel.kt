package com.example.chatsample.auth.view

data class AuthViewModel(
    val authCompleted: Boolean = false,
    val isWaitingForCode: Boolean = false,
    val error: Throwable? = null
)