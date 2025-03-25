package com.rafalskrzypczyk.swipe_mode.di

import com.rafalskrzypczyk.swipe_mode.data.SwipeModeRepositoryImpl
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.SwipeQuestionsPresenter
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.SwipeQuestionsContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SwipeModeModule {
    @Binds
    @Singleton
    abstract fun bindSwipeModeRepository(swipeModeRepositoryImpl: SwipeModeRepositoryImpl): SwipeModeRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class SwipeQuestionsModule {
    @Binds
    abstract fun bindPresenter(presenter: SwipeQuestionsPresenter): SwipeQuestionsContract.Presenter
}

