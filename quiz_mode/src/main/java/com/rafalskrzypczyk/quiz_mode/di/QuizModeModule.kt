package com.rafalskrzypczyk.quiz_mode.di

import androidx.fragment.app.Fragment
import com.rafalskrzypczyk.quiz_mode.data.FirestoreApi
import com.rafalskrzypczyk.quiz_mode.data.FirestoreApiMock
import com.rafalskrzypczyk.quiz_mode.data.QuizModeRepositoryImpl
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsContract
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsContract
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizModeModule {
    @Binds
    @Singleton
    abstract fun bindFirestore(firestore: FirestoreApiMock) : FirestoreApi

    @Binds
    @Singleton
    abstract fun bindRepository(repository: QuizModeRepositoryImpl): QuizModeRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizQuestionDetailsModule {
    @Binds
    abstract fun bindFragment(fragment: QuizQuestionDetailsFragment): QuizQuestionDetailsContract.View

    @Binds
    abstract fun bindPresenter(presenter: QuizQuestionDetailsPresenter): QuizQuestionDetailsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizQuestionDetailsFragmentModule{
    @Provides
    fun provideQuestionDetailsFragment(fragment: Fragment): QuizQuestionDetailsFragment {
        return fragment as QuizQuestionDetailsFragment
    }
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizQuestionsModule {
    @Binds
    abstract fun bindFragment(fragment: QuizQuestionsFragment): QuizQuestionsContract.View

    @Binds
    abstract fun bindPresenter(presenter: QuizQuestionsPresenter): QuizQuestionsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizQuestionsFragmentModule{
    @Provides
    fun provideQuestionsFragment(fragment: Fragment): QuizQuestionsFragment {
        return fragment as QuizQuestionsFragment
    }
}
