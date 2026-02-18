package com.rafalskrzypczyk.cem_mode.di

import com.rafalskrzypczyk.cem_mode.data.CemModeRepositoryImpl
import com.rafalskrzypczyk.cem_mode.domain.CemModeRepository
import com.rafalskrzypczyk.cem_mode.presentation.categories_list.CemCategoriesContract
import com.rafalskrzypczyk.cem_mode.presentation.categories_list.CemCategoriesPresenter
import com.rafalskrzypczyk.cem_mode.presentation.category_details.CemCategoryDetailsContract
import com.rafalskrzypczyk.cem_mode.presentation.category_details.CemCategoryDetailsPresenter
import com.rafalskrzypczyk.cem_mode.presentation.question_details.CemQuestionDetailsContract
import com.rafalskrzypczyk.cem_mode.presentation.question_details.CemQuestionDetailsPresenter
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.CemQuestionsContract
import com.rafalskrzypczyk.cem_mode.presentation.questions_list.CemQuestionsPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CemModeModule {

    @Binds
    @Singleton
    abstract fun bindCemModeRepository(impl: CemModeRepositoryImpl): CemModeRepository
}

@Module
@InstallIn(FragmentComponent::class)
abstract class CemModePresenterModule {

    @Binds
    abstract fun bindCemCategoriesPresenter(impl: CemCategoriesPresenter): CemCategoriesContract.Presenter

    @Binds
    abstract fun bindCemQuestionsPresenter(impl: CemQuestionsPresenter): CemQuestionsContract.Presenter

    @Binds
    abstract fun bindCemCategoryDetailsPresenter(impl: CemCategoryDetailsPresenter): CemCategoryDetailsContract.Presenter

    @Binds
    abstract fun bindCemQuestionDetailsPresenter(impl: CemQuestionDetailsPresenter): CemQuestionDetailsContract.Presenter
}
