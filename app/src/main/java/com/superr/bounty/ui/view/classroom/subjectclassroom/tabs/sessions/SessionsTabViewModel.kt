package com.superr.bounty.ui.view.classroom.subjectclassroom.tabs.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.superr.bounty.domain.model.Deck
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SessionStatus
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.ui.view.classroom.subjectclassroom.SessionCategories
import com.superr.bounty.utils.EncryptedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


class SessionsTabViewModel(
    private val classId: String,
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val deckRepository: DeckRepository,
    private val sessionRepository: SessionRepository,
    private val onSessionJoin: (String) -> Unit
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionsTabUiState())
    val uiState: StateFlow<SessionsTabUiState> = _uiState

    init {
        loadSessionsForClass()
    }

    private fun loadSessionsForClass() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val sessions = sessionRepository.getSessionsForClass(classId).getOrThrow()
                val (todaySessions, weekSessions, monthSessions, allTimeSessions) = categorizeSessionsMutuallyExclusive(
                    sessions
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todaySessions = todaySessions,
                        weekSessions = weekSessions,
                        monthSessions = monthSessions,
                        allTimeSessions = allTimeSessions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun setCurrentSession(session: Session) {
        _uiState.update { it.copy(currentSession = session) }
    }

    fun verifySessionCode(code: String) {
        viewModelScope.launch {
            val currentSession = _uiState.value.currentSession
            if (currentSession == null) {
                _uiState.update { it.copy(error = "Invalid way to reach session verification") }
                return@launch
            }

            try {
                val isValid =
                    sessionRepository.verifySessionCode(currentSession.id, code).getOrThrow()
                if (isValid) {
                    onSessionJoin(currentSession.id)
                } else {
                    _uiState.update { it.copy(error = "Invalid session code") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error verifying session code") }
            }
        }
    }

    private suspend fun getDeck(deckId: String): Deck {
        return deckRepository.getDeck(deckId).getOrThrow()
    }

    private fun categorizeSessionsMutuallyExclusive(sessions: List<Session>): SessionCategories {
        val now = Calendar.getInstance()
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val weekAgo = today.clone() as Calendar
        weekAgo.add(Calendar.DAY_OF_YEAR, -7)

        val monthAgo = today.clone() as Calendar
        monthAgo.add(Calendar.MONTH, -1)

        val todaySessions = sessions.findTodaySessions(now.time)
        val weekSessions = mutableListOf<Session>()
        val monthSessions = mutableListOf<Session>()
        val allTimeSessions = mutableListOf<Session>()

        sessions.filter { it.startTime.before(now.time) || it.startTime == now.time }
            .sortedByDescending { it.startTime }.forEach { session ->
                when {
                    session in todaySessions -> {} // Skip, already handled
                    session.startTime.after(weekAgo.time) -> weekSessions.add(session)
                    session.startTime.after(monthAgo.time) -> monthSessions.add(session)
                    else -> allTimeSessions.add(session)
                }
            }

        return SessionCategories(todaySessions, weekSessions, monthSessions, allTimeSessions)
    }

    private fun List<Session>.findTodaySessions(now: Date): List<Session> {

        return filter {
            val startDate = it.startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

            startDate == currentDate && it.startTime <= now && it.status != SessionStatus.COMPLETED
        }
    }

    fun encryptedPreferencesHelper(): EncryptedPreferencesHelper = encryptedPreferencesHelper

    fun deckRepository(): DeckRepository = deckRepository

    fun isTeacher(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.TEACHER

    fun isStudent(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.STUDENT
}

data class SessionsTabUiState(
    val isLoading: Boolean = false,
    val todaySessions: List<Session> = emptyList(),
    val weekSessions: List<Session> = emptyList(),
    val monthSessions: List<Session> = emptyList(),
    val allTimeSessions: List<Session> = emptyList(),
    val currentSession: Session? = null,
    val error: String? = null
)

class SessionsTabViewModelFactory(
    private val classId: String,
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val deckRepository: DeckRepository,
    private val sessionRepository: SessionRepository,
    private val onSessionJoin: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionsTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SessionsTabViewModel(
                classId,
                encryptedPreferencesHelper,
                deckRepository,
                sessionRepository,
                onSessionJoin
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}