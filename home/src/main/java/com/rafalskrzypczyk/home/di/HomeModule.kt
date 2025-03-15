package com.rafalskrzypczyk.home.di

import com.rafalskrzypczyk.home.StatisticsRepository
import com.rafalskrzypczyk.home.StatisticsRepositoryImpl
import com.rafalskrzypczyk.home.presentation.HomeScreenContract
import com.rafalskrzypczyk.home.presentation.HomeScreenPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeSingletonModule {
    @Binds
    abstract fun bindStatisticsRepository(repository: StatisticsRepositoryImpl): StatisticsRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class HomeModule {
    @Binds
    abstract fun bindPresenter(presenter: HomeScreenPresenter): HomeScreenContract.Presenter
}