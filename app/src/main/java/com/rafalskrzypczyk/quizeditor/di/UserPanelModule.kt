package com.rafalskrzypczyk.quizeditor.di

import com.rafalskrzypczyk.quizeditor.user_panel.UserPanelContract
import com.rafalskrzypczyk.quizeditor.user_panel.UserPanelPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class UserPanelModule {
    @Binds
    abstract fun bindUserPanelPresenter(presenter: UserPanelPresenter): UserPanelContract.Presenter
}