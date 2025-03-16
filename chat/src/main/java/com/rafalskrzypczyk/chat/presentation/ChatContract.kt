package com.rafalskrzypczyk.chat.presentation

import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.core.base.BaseContract

interface ChatContract {
    interface View : BaseContract.View {
        fun setupMessagesReceiver(currentUserId: String)
        fun displayMessages(messages: List<Message>)
        fun displayOlderMessages(messages: List<Message>)
        fun displayOlderMessagesLoading()
    }
    interface Presenter : BaseContract.Presenter<View> {
        fun sendMessage(message: String)
        fun loadOlderMessages()
    }
}