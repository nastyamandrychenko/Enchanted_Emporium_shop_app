package hu.bme.aut.qrvhfq.EnchantedEmporium.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.bme.aut.qrvhfq.EnchantedEmporium.firebase.FirebaseCommon
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun providerFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    )=FirebaseCommon(firestore,firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}