package com.example.chatsample.data

import android.content.Context
import javax.inject.Inject

class ContextProviderImpl @Inject constructor() : ContextProvider {
    override lateinit var appContext: Context
}