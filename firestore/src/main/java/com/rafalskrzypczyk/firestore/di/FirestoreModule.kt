package com.rafalskrzypczyk.firestore.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.rafalskrzypczyk.firestore.data.FirestoreService
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirestoreModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}

@InstallIn(SingletonComponent::class)
@Module
abstract class FirestoreModuleBinds {
    @Singleton
    @Binds
    abstract fun bindFirestoreApi(firestoreService: FirestoreService): FirestoreApi
}