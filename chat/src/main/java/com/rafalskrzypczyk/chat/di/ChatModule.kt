@file:Suppress("unused")

package com.rafalskrzypczyk.chat.di

import android.content.Context
import com.rafalskrzypczyk.chat.data.ChatRepositoryImpl
import com.rafalskrzypczyk.chat.domain.ChatMessagesHandler
import com.rafalskrzypczyk.chat.domain.ChatRepository
import com.rafalskrzypczyk.chat.presentation.ChatContract
import com.rafalskrzypczyk.chat.presentation.ChatPresenter
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.user_management.UserManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class ChatFragmentModule {
    @Binds
    abstract fun bindChatPresenter(impl: ChatPresenter): ChatContract.Presenter
}

@Module
@InstallIn(SingletonComponent::class)
class UnreadChatBehaviorModule {
    @Provides
    @Singleton
    fun provideUnreadChatBehavior(
        chatRepository: ChatRepository,
        sharedPreferencesApi: SharedPreferencesApi,
        userManager: UserManager,
        @ApplicationContext context: Context
    ): ChatMessagesHandler = ChatMessagesHandler(
        chatRepository = chatRepository,
        sharedPreferencesApi = sharedPreferencesApi,
        userManager = userManager,
        context = context
    )
}