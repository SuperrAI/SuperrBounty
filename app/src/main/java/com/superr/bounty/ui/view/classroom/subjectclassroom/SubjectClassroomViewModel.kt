package com.superr.bounty.ui.view.classroom.subjectclassroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SubjectClass
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.utils.EncryptedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "Superr.SubjectClassroomViewModel"

class SubjectClassroomViewModel(
    subjectClass: SubjectClass,
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val deckRepository: DeckRepository,
    private val sessionRepository: SessionRepository,
    private val onSessionJoin: (String) -> Unit,
    private val onNavigateToHomeworkSubmissionReviewScreen: (String) -> Unit
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubjectClassroomUiState(subjectClass = subjectClass))
    val uiState: StateFlow<SubjectClassroomUiState> = _uiState

    fun onSessionJoinAcceptance(sessionId: String) {
        onSessionJoin(sessionId)
    }

    fun onTeacherHomeworkClick(homeworkId: String) {
        onNavigateToHomeworkSubmissionReviewScreen(homeworkId)
    }

    fun encryptedPreferencesHelper(): EncryptedPreferencesHelper = encryptedPreferencesHelper

    fun deckRepository(): DeckRepository = deckRepository

    fun sessionRepository(): SessionRepository = sessionRepository

    fun isTeacher(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.TEACHER

    fun isStudent(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.STUDENT
}

data class SubjectClassroomUiState(
    val isLoading: Boolean = false,
    val subjectClass: SubjectClass? = null,
    val error: String? = null
)

data class SessionCategories(
    val todaySessions: List<Session>,
    val weekSessions: List<Session>,
    val monthSessions: List<Session>,
    val allTimeSessions: List<Session>
)

class SubjectClassroomViewModelFactory(
    private val subjectClass: SubjectClass,
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val deckRepository: DeckRepository,
    private val sessionRepository: SessionRepository,
    private val onSessionJoin: (String) -> Unit,
    private val onNavigateToHomeworkSubmissionReviewScreen: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectClassroomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SubjectClassroomViewModel(
                subjectClass,
                encryptedPreferencesHelper,
                deckRepository,
                sessionRepository,
                onSessionJoin,
                onNavigateToHomeworkSubmissionReviewScreen
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}