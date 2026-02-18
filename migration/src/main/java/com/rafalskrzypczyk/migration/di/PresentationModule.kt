@file:Suppress("unused")
package com.rafalskrzypczyk.migration.di

import com.rafalskrzypczyk.migration.presentation.migration_details.MigrationDetailsContract
import com.rafalskrzypczyk.migration.presentation.migration_details.MigrationDetailsPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class PresentationModule {

    @Binds
    abstract fun bindMigrationDetailsPresenter(
        migrationDetailsPresenter: MigrationDetailsPresenter
    ): MigrationDetailsContract.Presenter
}
