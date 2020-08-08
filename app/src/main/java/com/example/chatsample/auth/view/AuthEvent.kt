package com.example.chatsample.auth.view

sealed class AuthEvent {
    data class SendPhoneNumber(val phoneNumber: String): AuthEvent()
    data class SendCode(val code: String): AuthEvent()
}