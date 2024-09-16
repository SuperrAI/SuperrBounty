package com.superr.bounty.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.superr.bounty.data.AppModule
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.domain.repository.CardRepository
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.domain.repository.UserRepository
import com.superr.bounty.ui.common.deckplayer.livesession.LiveSessionDeckPlayerScreen
import com.superr.bounty.ui.common.deckplayer.livesession.LiveSessionDeckPlayerViewModel
import com.superr.bounty.ui.common.deckplayer.livesession.LiveSessionDeckPlayerViewModelFactory
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.EncryptedPreferencesHelper

private const val TAG = "Superr.LiveSessionDeckPlayerActivity"

class LiveSessionDeckPlayerActivity : ComponentActivity() {

    private lateinit var deckRepository: DeckRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var cardRepository: CardRepository
    private lateinit var userRepository: UserRepository
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var encryptedPreferencesHelper: EncryptedPreferencesHelper

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionId = intent.getStringExtra("SESSION_ID") ?: ""
        encryptedPreferencesHelper =
            AppModule.provideEncryptedPreferencesHelper(this.applicationContext)
        deckRepository = AppModule.provideDeckRepository()
        cardRepository = AppModule.provideCardRepository()
        firebaseDatabase =
            FirebaseDatabase.getInstance("https://superr-build-system-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        sessionRepository = AppModule.provideSessionRepository()
        userRepository = AppModule.provideUserRepository()

        setContent {
            SuperrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = SuperrTheme.colorScheme.White
                ) {
                    val viewModel: LiveSessionDeckPlayerViewModel = viewModel(
                        factory = LiveSessionDeckPlayerViewModelFactory(
                            encryptedPreferencesHelper,
                            sessionRepository,
                            deckRepository,
                            cardRepository,
                            userRepository,
                            firebaseDatabase,
                            onCloseLiveSession = { finish() }
                        )
                    )
                    LiveSessionDeckPlayerScreen(
                        viewModel,
                        sessionId,
                        encryptedPreferencesHelper.getUser().role == UserRole.TEACHER
                    )
                }
            }
        }
    }
}