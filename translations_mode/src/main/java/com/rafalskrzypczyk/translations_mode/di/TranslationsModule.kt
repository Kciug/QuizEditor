package com.rafalskrzypczyk.translations_mode.di

import com.rafalskrzypczyk.translations_mode.data.TranslationsRepositoryImpl
import com.rafalskrzypczyk.translations_mode.domain.TranslationsRepository
import com.rafalskrzypczyk.translations_mode.presentation.list.TranslationsListContract
import com.rafalskrzypczyk.translations_mode.presentation.list.TranslationsListPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationsModule {

    @Binds
    @Singleton
    abstract fun bindTranslationsRepository(
        impl: TranslationsRepositoryImpl
    ): TranslationsRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class TranslationsListModule {
    @Binds
    abstract fun bindTranslationsListPresenter(
        presenter: TranslationsListPresenter
    ): TranslationsListContract.Presenter
}