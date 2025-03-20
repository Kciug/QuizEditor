package com.rafalskrzypczyk.login_screen.di

import android.app.Activity
import com.rafalskrzypczyk.login_screen.SuccessLoginHandler
import com.rafalskrzypczyk.login_screen.login.LoginContract
import com.rafalskrzypczyk.login_screen.login.LoginPresenter
import com.rafalskrzypczyk.login_screen.reset_password.ResetPasswordContract
import com.rafalskrzypczyk.login_screen.reset_password.ResetPasswordPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object QuizQuestionDetailsActivityModule {
    @Provides
    @ActivityScoped
    fun provideSuccessLoginHandler(activity: Activity): SuccessLoginHandler {
        return activity as SuccessLoginHandler
    }
}

@Module
@InstallIn(FragmentComponent::class)
abstract class LoginModule {
    @Binds
    abstract fun bindLoginPresenter(presenter: LoginPresenter): LoginContract.Presenter

    @Binds
    abstract fun bindResetPasswordPresenter(presenter: ResetPasswordPresenter): ResetPasswordContract.Presenter
}