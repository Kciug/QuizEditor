package com.rafalskrzypczyk.quizeditor.di

import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManagerFactory
import com.rafalskrzypczyk.quizeditor.drawerManager.DrawerManagerFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class DrawerModule {

    @Binds
    abstract fun bindDrawerManagerFactory(
        impl: DrawerManagerFactoryImpl
    ) : DrawerManagerFactory
}