package com.example.chatsample.domain

import com.example.chatsample.model.AuthResult

interface ChatRepository {
    fun init()

    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun authenticateWithCode(code: String): AuthResult
}