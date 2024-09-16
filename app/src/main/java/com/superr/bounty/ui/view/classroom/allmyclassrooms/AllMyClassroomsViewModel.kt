package com.superr.bounty.ui.view.classroom.allmyclassrooms

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.superr.bounty.domain.model.SubjectClass
import com.superr.bounty.domain.model.User
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.domain.repository.SubjectClassRepository
import com.superr.bounty.utils.EncryptedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "Superr.AllMyClassroomsViewModel"

class AllMyClassroomsViewModel(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val subjectClassRepository: SubjectClassRepository,
    private val onSubjectClassroomClick: (SubjectClass) -> Unit
) : ViewModel() {
    private val _uiState = MutableStateFlow(AllMyClassroomsUiState())
    val uiState: StateFlow<AllMyClassroomsUiState> = _uiState

    init {
        loadUserAndClasses()
    }

    private fun loadUserAndClasses() {
        val user = encryptedPreferencesHelper.getUser()
        user.let {
            _uiState.update { state -> state.copy(user = it) }
            loadClasses(it.id, it.role == UserRole.TEACHER)
        }
    }

    private fun loadClasses(userId: String, isTeacher: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = if (isTeacher) {
                subjectClassRepository.getSubjectClassesForTeacher(userId)
            } else {
                subjectClassRepository.getSubjectClassesForStudent(userId)
            }
            result.onSuccess { classList ->
                _uiState.update { it.copy(classes = classList, isLoading = false) }
            }.onFailure { error ->
                Log.e(TAG, "loadClasses: ", error)
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }

    fun onClassroomClick(subjectClass: SubjectClass) {
        onSubjectClassroomClick(subjectClass)
    }

    fun encryptedPreferencesHelper(): EncryptedPreferencesHelper = encryptedPreferencesHelper

    fun isTeacher(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.TEACHER

    fun isStudent(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.STUDENT
}

enum class SortOrder {
    ALPHABETICAL, REVERSE_ALPHABETICAL
}

data class AllMyClassroomsUiState(
    val user: User? = null,
    val classes: List<SubjectClass> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class AllMyClassroomsViewModelFactory(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val subjectClassRepository: SubjectClassRepository,
    private val onSubjectClassroomClick: (SubjectClass) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllMyClassroomsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllMyClassroomsViewModel(
                encryptedPreferencesHelper,
                subjectClassRepository,
                onSubjectClassroomClick
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}