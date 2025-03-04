package com.rafalskrzypczyk.login_screen.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.rafalskrzypczyk.login_screen.SuccessLoginHandler
import com.rafalskrzypczyk.login_screen.login.LoginContract
import com.rafalskrzypczyk.login_screen.login.LoginFragment
import com.rafalskrzypczyk.login_screen.login.LoginPresenter
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
object QuizQuestionDetailsFragmentModule{
    @Provides
    fun provideLoginFragment(fragment: Fragment): LoginFragment {
        return fragment as LoginFragment
    }
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizQuestionsModule {
    @Binds
    abstract fun bindFragment(fragment: LoginFragment): LoginContract.View

    @Binds
    abstract fun bindPresenter(presenter: LoginPresenter): LoginContract.Presenter
}