package com.rafalskrzypczyk.core.di

import android.content.Context
import android.content.SharedPreferences
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesService
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.user_management.UserManagerImpl
import com.rafalskrzypczyk.core.utils.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider = ResourceProvider(context)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDatabaseManager(
        sharedPreferencesApi: SharedPreferencesApi,
        userManager: UserManager
    ) : DatabaseManager = DatabaseManager(sharedPreferencesApi, userManager)

    @Provides
    @Singleton
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModuleBinds {
    @Binds
    @Singleton
    abstract fun bindSharedPreferencesApi(sharedPreferencesService: SharedPreferencesService): SharedPreferencesApi

    @Binds
    @Singleton
    abstract fun bindUserManager(manager: UserManagerImpl): UserManager
}