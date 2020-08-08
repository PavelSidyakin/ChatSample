package com.example.chatsample.model

sealed class AuthResult {
    class Ok : AuthResult()
    class PasswordRequired : AuthResult()
}