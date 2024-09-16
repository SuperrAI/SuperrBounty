package com.superr.bounty.data

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.superr.bounty.data.repository.FirebaseCardRepository
import com.superr.bounty.data.repository.FirebaseDeckRepository
import com.superr.bounty.data.repository.FirebaseSessionRepository
import com.superr.bounty.data.repository.FirebaseSubjectClassRepository
import com.superr.bounty.data.repository.FirebaseUserRepository
import com.superr.bounty.domain.repository.CardRepository
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.domain.repository.SubjectClassRepository
import com.superr.bounty.domain.repository.UserRepository
import com.superr.bounty.utils.EncryptedPreferencesHelper

private const val TAG = "Superr.AppModule"

object AppModule {

    private const val FIREBASE_FIRESTORE_DB = ""
    private const val FIREBASE_REALTIME_DB = ""
    private const val FIREBASE_STORAGE = ""

    fun provideCardRepository(): CardRepository {
        return FirebaseCardRepository(FirebaseFirestore.getInstance(FIREBASE_FIRESTORE_DB))
    }

    fun provideDeckRepository(): DeckRepository {
        return FirebaseDeckRepository(FirebaseFirestore.getInstance(FIREBASE_FIRESTORE_DB))
    }

    fun provideEncryptedPreferencesHelper(context: Context): EncryptedPreferencesHelper {
        return EncryptedPreferencesHelper(context)
    }

    fun provideSessionRepository(): SessionRepository {
        return FirebaseSessionRepository(
            FirebaseFirestore.getInstance(FIREBASE_FIRESTORE_DB),
            FirebaseDatabase.getInstance(FIREBASE_REALTIME_DB).reference
        )
    }

    fun provideSubjectClassRepository(): SubjectClassRepository {
        return FirebaseSubjectClassRepository(FirebaseFirestore.getInstance(FIREBASE_FIRESTORE_DB))
    }

    fun provideUserRepository(): UserRepository {
        return FirebaseUserRepository(FirebaseFirestore.getInstance(FIREBASE_FIRESTORE_DB))
    }
}