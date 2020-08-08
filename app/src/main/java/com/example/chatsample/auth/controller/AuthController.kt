package com.example.chatsample.auth.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.example.chatsample.auth.view.AuthView

interface AuthController {

    fun onViewCreated(authView: AuthView, viewLifecycle: Lifecycle)

}