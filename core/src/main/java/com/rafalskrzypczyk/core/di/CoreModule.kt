package com.rafalskrzypczyk.core.di

import android.content.Context
import android.content.SharedPreferences
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesApi
import com.rafalskrzypczyk.core.local_preferences.SharedPreferencesService
import com.rafalskrzypczyk.core.utils.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


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
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModuleBinds {
    @Binds
    @Singleton
    abstract fun bindSharedPreferencesApi(sharedPreferencesService: SharedPreferencesService): SharedPreferencesApi
}