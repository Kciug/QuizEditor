package com.rafalskrzypczyk.chat.presentation

import com.rafalskrzypczyk.chat.domain.ChatMessagesHandler
import com.rafalskrzypczyk.chat.domain.ChatRepository
import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.extensions.generateId
import com.rafalskrzypczyk.core.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class ChatPresenter @Inject constructor(
    private val repository: ChatRepository,
    private val chatMessagesHandler: ChatMessagesHandler
): BasePresenter<ChatContract.View>(), ChatContract.Presenter {
    private var loadOldMessagesTriggered = false

    override fun onViewCreated() {
        super.onViewCreated()
        view.setupMessagesReceiver(repository.getCurrentUserId())

        chatMessagesHandler.onChatOpened()

        presenterScope?.launch{
            delay(Constants.PRESENTER_INITIAL_DELAY)
            repository.getLatestMessages().collectLatest {
                processResponse(it)
            }
        }
    }

    override fun sendMessage(message: String) {
        if(message.isBlank()) return
        presenterScope?.launch{
            repository.sendMessage(Message(
                id = Long.generateId(),
                senderId = repository.getCurrentUserId(),
                senderName = repository.getCurrentUserName(),
                message = message,
                timestamp = Date()
            ))
        }
    }

    override fun loadOlderMessages() {
        if(loadOldMessagesTriggered) return
        loadOldMessagesTriggered = true
        presenterScope?.launch {
            repository.getOlderMessages().collectLatest {
                when (it) {
                    is Response.Success -> {
                        view.displayOlderMessages(it.data.sortedByDescending { it.timestamp })
                        loadOldMessagesTriggered = false
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayOlderMessagesLoading()
                }
            }
        }
    }

    override fun onDestroy() {
        chatMessagesHandler.onChatClosed()
        super.onDestroy()
    }

    private fun processResponse(response: Response<List<Message>>) {
        when(response) {
            is Response.Success -> {
                displayMessages(response.data)
                attachMessagesListener()
            }
            is Response.Error -> view.displayError(response.error)
            is Response.Loading -> view.displayLoading()
        }
    }

    private fun attachMessagesListener() {
        presenterScope?.launch {
            chatMessagesHandler.newMessages.collectLatest {
                displayMessages(it)
            }
        }
    }

    private fun displayMessages(messages: List<Message>) {
        messages.sortedByDescending { it.timestamp }.let {
            view.displayMessages(it)
            chatMessagesHandler.updateLastReadMessage(it.first().timestamp)
        }
    }
}