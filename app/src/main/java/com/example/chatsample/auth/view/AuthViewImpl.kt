package com.example.chatsample.auth.view

import android.view.View
import androidx.core.widget.doOnTextChanged
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.example.chatsample.auth.store.AuthStore
import kotlinx.android.synthetic.main.auth_fragment.view.auth_code_entered_button
import kotlinx.android.synthetic.main.auth_fragment.view.auth_enter_code_edit_text
import kotlinx.android.synthetic.main.auth_fragment.view.auth_enter_phone_number_edit_text
import kotlinx.android.synthetic.main.auth_fragment.view.auth_phone_entered_button

class AuthViewImpl(private val rootView: View): BaseMviView<AuthStore.State, AuthStore.Intent>(), AuthView  {

    init {
        with(rootView) {
            auth_enter_phone_number_edit_text.doOnTextChanged { text, start, before, count ->
                dispatch(AuthStore.Intent.TypingPhoneNumber(text.toString()))
            }
            auth_enter_code_edit_text.doOnTextChanged { text, start, before, count ->
                dispatch(AuthStore.Intent.TypingCode(text.toString()))
            }
            auth_phone_entered_button.setOnClickListener {
                dispatch(AuthStore.Intent.SendPhoneNumber(auth_enter_phone_number_edit_text.text.toString()))
            }
            auth_code_entered_button.setOnClickListener {
                dispatch(AuthStore.Intent.SendCode(auth_enter_code_edit_text.text.toString()))
            }
        }
    }

    override val renderer: ViewRenderer<AuthStore.State> = diff {
        diff(get = AuthStore.State::sendPhoneNumberEnabled, set = { rootView.auth_phone_entered_button.isEnabled = it} )
        diff(get = AuthStore.State::sendCodeEnabled, set = { rootView.auth_code_entered_button.isEnabled = it} )
        diff(get = AuthStore.State::currentPhoneNumber, set = { rootView.auth_enter_phone_number_edit_text.setText(it) } )
    }
}