package com.rafalskrzypczyk.quiz_mode.di

import com.rafalskrzypczyk.core.di.IoDispatcher
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.data.QuizModeRepositoryImpl
import com.rafalskrzypczyk.quiz_mode.domain.DataUpdateManager
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsContract
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesContract
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerContract
import com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker.CheckablePickerPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsContract
import com.rafalskrzypczyk.quiz_mode.presentation.question_details.QuizQuestionDetailsPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsContract
import com.rafalskrzypczyk.quiz_mode.presentation.questions_list.QuizQuestionsPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizModeModule {
    @Binds
    @Singleton
    abstract fun bindRepository(repository: QuizModeRepositoryImpl): QuizModeRepository
}

@Module
@InstallIn(SingletonComponent::class)
object QuizModeFragmentModule {
    @Provides
    @Singleton
    fun provideUpdateManager(
        repository: QuizModeRepository,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ) = DataUpdateManager(repository, dispatcher)
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizQuestionDetailsModule {
    @Binds
    abstract fun bindPresenter(presenter: QuizQuestionDetailsPresenter): QuizQuestionDetailsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizQuestionDetailsFragmentModule{
    @Provides
    @FragmentScoped
    fun provideInteractor(
        repository: QuizModeRepository,
        dataUpdateManager: DataUpdateManager
    ) = QuizQuestionDetailsInteractor(repository, dataUpdateManager)
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizQuestionsModule {
    @Binds
    abstract fun bindPresenter(presenter: QuizQuestionsPresenter): QuizQuestionsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizCategoriesModule {
    @Binds
    abstract fun bindPresenter(presenter: QuizCategoriesPresenter): QuizCategoriesContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizCategoryDetailsModule {
    @Binds
    abstract fun bindPresenter(presenter: QuizCategoryDetailsPresenter): QuizCategoryDetailsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizCategoryDetailsFragmentModule{
    @Provides
    @FragmentScoped
    fun provideInteractor(
        repository: QuizModeRepository,
        resourceProvider: ResourceProvider,
        dataUpdateManager: DataUpdateManager
    ) = QuizCategoryDetailsInteractor(repository, resourceProvider, dataUpdateManager)
}

@Module
@InstallIn(FragmentComponent::class)
abstract class CheckablePickerModule{
    @Binds
    abstract fun bindPresenter(presenter: CheckablePickerPresenter): CheckablePickerContract.Presenter
}