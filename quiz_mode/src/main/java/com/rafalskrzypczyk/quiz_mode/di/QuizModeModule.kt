package com.rafalskrzypczyk.quiz_mode.di

import androidx.fragment.app.Fragment
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.quiz_mode.data.FirestoreApi
import com.rafalskrzypczyk.quiz_mode.data.FirestoreApiMock
import com.rafalskrzypczyk.quiz_mode.data.QuizModeRepositoryImpl
import com.rafalskrzypczyk.quiz_mode.domain.QuizCategoryDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.domain.QuizModeRepository
import com.rafalskrzypczyk.quiz_mode.domain.QuizQuestionDetailsInteractor
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsContract
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsFragment
import com.rafalskrzypczyk.quiz_mode.presentation.categeory_details.QuizCategoryDetailsPresenter
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesContract
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesFragment
import com.rafalskrzypczyk.quiz_mode.presentation.categories_list.QuizCategoriesPresenter
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
import dagger.hilt.android.scopes.FragmentScoped
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
    @FragmentScoped
    fun provideInteractor(repository: QuizModeRepository) = QuizQuestionDetailsInteractor(repository)

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

//@Module
//@InstallIn(FragmentComponent::class)
//abstract class CheckablePickerModule {
//
//}
//
//@Module
//@InstallIn(FragmentComponent::class)
//object CheckablePickerFragmentModule{
//
//}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizCategoriesModule {
    @Binds
    abstract fun bindFragment(fragment: QuizCategoriesFragment): QuizCategoriesContract.View

    @Binds
    abstract fun bindPresenter(presenter: QuizCategoriesPresenter): QuizCategoriesContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizCategoriesFragmentModule{
    @Provides
    fun provideQuestionsFragment(fragment: Fragment): QuizCategoriesFragment {
        return fragment as QuizCategoriesFragment
    }
}

@Module
@InstallIn(FragmentComponent::class)
abstract class QuizCategoryDetailsModule {
    @Binds
    abstract fun bindFragment(fragment: QuizCategoryDetailsFragment): QuizCategoryDetailsContract.View

    @Binds
    abstract fun bindPresenter(presenter: QuizCategoryDetailsPresenter): QuizCategoryDetailsContract.Presenter
}

@Module
@InstallIn(FragmentComponent::class)
object QuizCategoryDetailsFragmentModule{
    @Provides
    @FragmentScoped
    fun provideInteractor(repository: QuizModeRepository, resourceProvider: ResourceProvider) = QuizCategoryDetailsInteractor(repository, resourceProvider)

    @Provides
    fun provideQuestionsFragment(fragment: Fragment): QuizCategoryDetailsFragment {
        return fragment as QuizCategoryDetailsFragment
    }
}