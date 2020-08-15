package com.example.chatsample.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import com.example.chatsample.ChatApplication
import com.example.chatsample.R
import com.example.chatsample.auth.controller.AuthController
import kotlinx.android.synthetic.main.auth_fragment.*
import javax.inject.Inject

class AuthFragment: Fragment() {

    private val controller: AuthController by lazy {
        ChatApplication.getAppComponent().authControllerFactory.create(lifecycle.asMviLifecycle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.auth_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        controller.onViewCreated(AuthViewImpl(auth_root_view), lifecycle.asMviLifecycle())
    }
}