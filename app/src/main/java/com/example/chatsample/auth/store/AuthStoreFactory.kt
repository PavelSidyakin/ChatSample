package com.example.chatsample.auth.store

interface AuthStoreFactory {
    fun create(): AuthStore
}