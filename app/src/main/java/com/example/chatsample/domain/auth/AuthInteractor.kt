package com.example.chatsample.domain.auth

import com.example.chatsample.model.AuthResult

interface AuthInteractor {

    suspend fun authenticate(phoneNumber: String): AuthResult
    suspend fun authenticateWithCode(code: String): AuthResult

}