@file:Suppress("unused")

package com.rafalskrzypczyk.migration.di

import com.rafalskrzypczyk.migration.data.MigrationRepositoryImpl
import com.rafalskrzypczyk.migration.domain.MigrationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MigrationModule {

    @Binds
    @Singleton
    abstract fun bindMigrationRepository(
        migrationRepositoryImpl: MigrationRepositoryImpl
    ): MigrationRepository
}
