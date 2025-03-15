package com.rafalskrzypczyk.chat.di

import com.rafalskrzypczyk.chat.data.ChatRepositoryImpl
import com.rafalskrzypczyk.chat.domain.ChatRepository
import com.rafalskrzypczyk.chat.presentation.ChatContract
import com.rafalskrzypczyk.chat.presentation.ChatPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {
    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class ChatFragmentModule {
    @Binds
    abstract fun bindChatPresenter(impl: ChatPresenter): ChatContract.Presenter
}