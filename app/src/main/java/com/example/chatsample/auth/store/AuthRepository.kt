package com.example.chatsample.auth.store

import com.example.chatsample.model.AuthResult

interface AuthRepository {
    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun continueWithCode(code: String): AuthResult
}