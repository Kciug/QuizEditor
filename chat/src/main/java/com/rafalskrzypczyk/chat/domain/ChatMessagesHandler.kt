package com.rafalskrzypczyk.chat.domain

import android.content.Context
import com.rafalskrzypczyk.chat.R
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.internal_notifications.InAppNotificationManager
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user.UserData
import com.rafalskrzypczyk.core.user.UserRole
import com.rafalskrzypczyk.core.user_management.UserManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

class ChatMessagesHandler @Inject constructor(
    private val chatRepository: ChatRepository,
    private val sharedPreferencesApi: SharedPreferencesApi,
    private val userManager: UserManager,
    @ApplicationContext private val context: Context
) {
    private val _newMessages = MutableSharedFlow<List<Message>>()
    val newMessages: SharedFlow<List<Message>> = _newMessages.asSharedFlow()

    private val _hasNewMessages = MutableStateFlow(false)
    val hasNewMessages: StateFlow<Boolean> = _hasNewMessages.asStateFlow()

    private var currentUser: UserData? = null
    private var chatOpened: Boolean = false
    private var inAppNotificationManager: InAppNotificationManager? = null
    private var navigateToChat: (() -> Unit)? = null

    suspend fun observeNewMessages() {
        currentUser = userManager.getCurrentLoggedUser()
        if(currentUser?.role == UserRole.USER) return

        chatRepository.getLatestMessages().collect {
            if(it is Response.Success) {
                attachMessagesListener()
            }
        }
    }

    private suspend fun attachMessagesListener() {
        chatRepository.getUpdatedMessages().collect {
            if(chatOpened) {
                _newMessages.emit(it)
                return@collect
            }

            val newestMessage = it.sortedByDescending { it.timestamp }.first()
            if(newestMessage.senderId == currentUser?.id) return@collect

            if(newestMessage.timestamp > sharedPreferencesApi.getLastDisplayedMessageTimestamp()) {
                _hasNewMessages.value = true
                inAppNotificationManager?.show(
                    title = context.getString(R.string.notification_title_new_messages),
                    message = context.getString(R.string.notification_message_new_messages),
                    iconRes = com.rafalskrzypczyk.core.R.drawable.ic_chat_unread_24,
                ) { navigateToChat?.invoke() }
            }
        }
    }

    fun onChatOpened() {
        chatOpened = true
    }

    fun onChatClosed() {
        chatOpened = false
    }

    fun updateLastReadMessage(messageTimestamp: Date) {
        sharedPreferencesApi.setLastDisplayedMessageTimestamp(messageTimestamp)
        _hasNewMessages.value = false
    }

    fun setInAppNotificationManager(notificationManager: InAppNotificationManager, navigateToChat: () -> Unit) {
        inAppNotificationManager = notificationManager
        this.navigateToChat = navigateToChat
    }
}