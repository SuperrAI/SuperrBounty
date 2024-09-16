package com.superr.bounty.ui.common.deckplayer.livesession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.User
import com.superr.bounty.domain.model.card.Card
import com.superr.bounty.domain.model.card.CardContent
import com.superr.bounty.domain.model.card.CardContentState
import com.superr.bounty.domain.model.card.CardResponse
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContent
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContentState
import com.superr.bounty.domain.model.card.FillInTheBlanksResponse
import com.superr.bounty.domain.model.card.ImageCardContent
import com.superr.bounty.domain.model.card.ImageCardContentState
import com.superr.bounty.domain.model.card.ImageResponse
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContent
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContentState
import com.superr.bounty.domain.model.card.MatchTheFollowingResponse
import com.superr.bounty.domain.model.card.ShortAnswerCardContent
import com.superr.bounty.domain.model.card.ShortAnswerCardContentState
import com.superr.bounty.domain.model.card.ShortAnswerResponse
import com.superr.bounty.domain.model.card.SimpleMCQCardContent
import com.superr.bounty.domain.model.card.SimpleMCQCardContentState
import com.superr.bounty.domain.model.card.SimpleMCQResponse
import com.superr.bounty.domain.model.card.YesNoCardContent
import com.superr.bounty.domain.model.card.YesNoCardContentState
import com.superr.bounty.domain.model.card.YesNoResponse
import com.superr.bounty.domain.repository.CardRepository
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.domain.repository.UserRepository
import com.superr.bounty.utils.EncryptedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.toImmutableList

private const val TAG = "Superr.LiveSessionDeckPlayerViewModel"

class LiveSessionDeckPlayerViewModel(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val sessionRepository: SessionRepository,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
    private val firebaseDatabase: DatabaseReference,
    private val onCloseLiveSession: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveSessionDeckPlayerUiState())
    val uiState: StateFlow<LiveSessionDeckPlayerUiState> = _uiState

    private lateinit var sessionId: String
    private lateinit var userId: String
    private lateinit var currentSessionReference: DatabaseReference
    private lateinit var activeStudentsReference: DatabaseReference
    private lateinit var leftStudentsReference: DatabaseReference
    private lateinit var studentResponsesReference: DatabaseReference
    private lateinit var handRaiseReference: DatabaseReference
    private var isTeacher: Boolean = false
    private val userDetailsMap = mutableMapOf<String, User>()

    fun initializeSession(sessionId: String, isTeacher: Boolean) {
        this.sessionId = sessionId
        this.userId = encryptedPreferencesHelper.getUser().id
        this.isTeacher = isTeacher
        currentSessionReference = firebaseDatabase.child("sessions").child(sessionId)
        activeStudentsReference = currentSessionReference.child("active_students")
        leftStudentsReference = currentSessionReference.child("left_students")
        studentResponsesReference = currentSessionReference.child("student_responses")
        handRaiseReference = currentSessionReference.child("hand_raises")
        loadSession()
        if (!isTeacher) {
            addStudentToActiveList()
        }
    }

    fun moveToNextCard() {
        val nextIndex =
            (_uiState.value.currentCardIndex + 1).coerceAtMost(_uiState.value.cards.size - 1)
        if (isTeacher) {
            updateCurrentCardIndex(nextIndex)
        } else {
            // FOR DEMO
            _uiState.update { currentState ->
                val newCard = currentState.cards.getOrNull(nextIndex)
                currentState.copy(
                    currentCardIndex = nextIndex,
                    currentCard = newCard,
                    currentCardState = newCard?.let { createCardState(it) }
                )
            }
        }
    }

    fun moveToPreviousCard() {
        val previousIndex = (_uiState.value.currentCardIndex - 1).coerceAtLeast(0)
        if (isTeacher) {
            updateCurrentCardIndex(previousIndex)
        } else {
            // FOR DEMO
            _uiState.update { currentState ->
                val newCard = currentState.cards.getOrNull(previousIndex)
                currentState.copy(
                    currentCardIndex = previousIndex,
                    currentCard = newCard,
                    currentCardState = newCard?.let { createCardState(it) }
                )
            }
        }
    }

    fun onCloseSession() {
        viewModelScope.launch {
            if (!isTeacher) {
                moveStudentToLeftList()
            } else {
                sessionRepository.endLiveSession(sessionId)
            }
            onCloseLiveSession()
        }
    }

    private suspend fun handleStudentJoining() {
        val wasInLeftList = removeFromLeftListIfPresent()
        addStudentToActiveList(wasInLeftList)
    }

    private suspend fun removeFromLeftListIfPresent(): Boolean {
        val snapshot = leftStudentsReference.child(userId).get().await()
        if (snapshot.exists()) {
            leftStudentsReference.child(userId).removeValue().await()
            return true
        }
        return false
    }

    private suspend fun addStudentToActiveList(wasInLeftList: Boolean) {
        val studentEntry = mapOf(
            "joinTime" to ServerValue.TIMESTAMP, "rejoined" to wasInLeftList
        )
        activeStudentsReference.child(userId).setValue(studentEntry).await()
    }

    private fun moveStudentToLeftList() {
        activeStudentsReference.child(userId).removeValue()
        val leftEntry = mapOf(
            "leftTime" to ServerValue.TIMESTAMP
        )
        leftStudentsReference.child(userId).setValue(leftEntry)
    }

    private fun loadSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            sessionRepository.getSession(sessionId).onSuccess { session ->
                _uiState.update { it.copy(session = session) }
                loadDecksAndCards(session)
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun loadDecksAndCards(session: Session) {
        viewModelScope.launch {
            deckRepository.getDecksByIds(session.deckIds).onSuccess { decks ->
                val allCardIds = decks.flatMap { it.cardIds }
                cardRepository.getCardsByIds(allCardIds).onSuccess { cards ->
                    _uiState.update {
                        it.copy(isLoading = false,
                            cards = cards,
                            currentCard = cards.firstOrNull(),
                            currentCardState = cards.firstOrNull()
                                ?.let { card -> createCardState(card) })
                    }
                    if (isTeacher) {
                        initializeRealtimeDatabase(session)
                        observeActiveStudents()
                    }
                    observeHandRaises()
                    observeStudentResponses()
                    observeCurrentCardIndex()
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun initializeRealtimeDatabase(session: Session) {
        currentSessionReference.setValue(
            mapOf(
                "currentCardIndex" to 0,
                "title" to session.title,
                "status" to "IN_PROGRESS",
                "active_students" to mapOf<String, Any>(),
                "left_students" to mapOf<String, Any>(),
                "hand_raises" to mapOf<String, Any>()
            )
        )
    }

    /* Card Control */
    private fun createCardState(card: Card): CardContentState {
        return when (val content = card.content) {
            is ImageCardContent -> ImageCardContentState(
                isTeacher = isTeacher, onSubmit = { }, response = ImageResponse
            )

            is SimpleMCQCardContent -> SimpleMCQCardContentState(
                isTeacher = isTeacher,
                response = SimpleMCQResponse(),
                responseCounts = List(content.options.size) { 0 },
                totalActiveStudents = _uiState.value.studentCount,
                onOptionSelected = ::onSimpleMCQOptionSelected,
                onShowResult = ::showResultsModal,
                onSubmit = ::onSimpleMCQAnswerSubmitted,
            )

            is FillInTheBlanksCardContent -> FillInTheBlanksCardContentState(
                isTeacher = isTeacher,
                onSubmit = ::onFillInTheBlanksAnswerSubmitted,
                totalResponses = 0,
                totalActiveStudents = _uiState.value.studentCount,
                response = FillInTheBlanksResponse(answer = ""),
                onAnswerChange = ::onFillInTheBlanksAnswerChange,
                onShowResult = ::showResultsModal,
            )

            is MatchTheFollowingCardContent -> {
                val cardContent = card.content as MatchTheFollowingCardContent
                val shuffledRightIndices = (0 until cardContent.pairs.size).shuffled()
                return MatchTheFollowingCardContentState(
                    isTeacher = isTeacher,
                    onSubmit = ::onMatchTheFollowingAnswerSubmitted,
                    response = MatchTheFollowingResponse(connectedPairs = List(cardContent.pairs.size) { -1 }),
                    isSubmitted = false,
                    shuffledPairs = shuffledRightIndices,
                    onShowResult = ::showResultsModal,
                )
            }

            is ShortAnswerCardContent -> ShortAnswerCardContentState(
                isTeacher = isTeacher,
                onSubmit = ::onShortAnswerSubmitted,
                response = ShortAnswerResponse(answer = ""),
                totalResponses = 0,
                totalActiveStudents = _uiState.value.studentCount,
                onAnswerChange = ::onShortAnswerChange,
                onShowResult = ::showResultsModal
            )

            is YesNoCardContent -> YesNoCardContentState(
                isTeacher = isTeacher,
                response = YesNoResponse(),
                responseCounts = listOf(0, 0, 0),
                totalActiveStudents = _uiState.value.studentCount,
                onOptionSelected = ::onYesNoOptionSelected,
                onShowResult = ::showResultsModal,
                onSubmit = ::onYesNoAnswerSubmitted
            )

            // Add more cases for other card types
            else -> throw IllegalStateException("Unsupported card type: ${content::class.simpleName}")
        }
    }

    private fun updateCardState(newState: CardContentState) {
        _uiState.update { currentState ->
            currentState.copy(currentCardState = newState)
        }
    }

    private fun observeCurrentCardIndex() {
        currentSessionReference.child("currentCardIndex")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val index = snapshot.getValue(Int::class.java) ?: 0
                    _uiState.update { currentState ->
                        val newCard = currentState.cards.getOrNull(index)
                        currentState.copy(
                            currentCardIndex = index,
                            currentCard = newCard,
                            currentCardState = newCard?.let { createCardState(it) }
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.update { it.copy(error = error.message) }
                }
            })
    }

    private fun updateCurrentCardState() {
        val currentCard = _uiState.value.currentCard
        val currentCardState = _uiState.value.currentCardState
        if (currentCard != null && currentCardState != null) {
            val newState: CardContentState = when (currentCard.content) {
                is FillInTheBlanksCardContent -> {
                    (currentCardState as FillInTheBlanksCardContentState).copy(
                        totalResponses = calculateFillInTheBlanksResponseCounts(),
                        totalActiveStudents = _uiState.value.studentCount
                    )
                }

                is SimpleMCQCardContent -> {
                    (currentCardState as SimpleMCQCardContentState).copy(
                        responseCounts = calculateSimpleMCQResponseCounts(),
                        totalActiveStudents = _uiState.value.studentCount
                    )
                }

                is ShortAnswerCardContent -> {
                    (currentCardState as ShortAnswerCardContentState).copy(
                        totalResponses = calculateShortAnswerResponseCounts(),
                        totalActiveStudents = _uiState.value.studentCount
                    )
                }

                is YesNoCardContent -> {
                    (currentCardState as YesNoCardContentState).copy(
                        responseCounts = calculateYesNoResponseCounts(),
                        totalActiveStudents = _uiState.value.studentCount
                    )
                }

                else -> {
                    currentCardState
                }
            }
            _uiState.update { it.copy(currentCardState = newState) }
        }
    }

    private fun updateCurrentCardIndex(index: Int) {
        val currentCardRef = currentSessionReference.child("currentCardIndex")
        currentCardRef.setValue(index)
    }


    /* Simple MCQ */
    private fun onSimpleMCQOptionSelected(optionIndex: Int) {
        val currentState = _uiState.value.currentCardState
        if (currentState is SimpleMCQCardContentState) {
            updateCardState(
                currentState.copy(response = SimpleMCQResponse(optionIndex))
            )
        }
    }

    private fun onSimpleMCQAnswerSubmitted() {
        val currentState = _uiState.value.currentCardState
        if (currentState is SimpleMCQCardContentState) {
            val cardIndex = _uiState.value.currentCardIndex
            val selectedOption = currentState.response.selectedOption

            viewModelScope.launch {
                selectedOption?.let { option ->
                    updateCardState(currentState.copy(isSubmitted = true))
                    studentResponsesReference.child(cardIndex.toString()).child(userId)
                        .setValue(option).await()
                }
            }
        }
    }

    private fun calculateSimpleMCQResponseCounts(): List<Int> {
        val currentCardIndex = _uiState.value.currentCardIndex
        val currentCardContent = _uiState.value.currentCard?.content as SimpleMCQCardContent
        val responses = _uiState.value.studentResponses[currentCardIndex]
        val responseCounts = List(currentCardContent.options.size) { _ -> 0 }.toMutableList()
        if (responses != null) {
            (responses as Map<String, SimpleMCQResponse>).values.forEach { response ->
                if (response.selectedOption != null) {
                    responseCounts[response.selectedOption]++
                }
            }
        }
        return responseCounts.toImmutableList()
    }


    /* Match The Following */
    private fun onMatchTheFollowingPairConnected(leftIndex: Int, rightIndex: Int) {
        // TODO: Bolster this logic
        val currentState = _uiState.value.currentCardState
        if (currentState is MatchTheFollowingCardContentState) {
            val newConnectedPairs = currentState.response.connectedPairs.toMutableList()
            newConnectedPairs[leftIndex] = rightIndex
            updateCardState(
                currentState.copy(
                    response = MatchTheFollowingResponse(connectedPairs = newConnectedPairs.toImmutableList()),
                )
            )
        }
    }

    private fun onMatchTheFollowingAnswerSubmitted() {
        val currentState = _uiState.value.currentCardState
        if (currentState is MatchTheFollowingCardContentState) {
            viewModelScope.launch {
                val cardIndex = _uiState.value.currentCardIndex
                val userResponse = denormalizePairs(currentState)
                updateCardState(currentState.copy(isSubmitted = true))
                studentResponsesReference.child(cardIndex.toString()).child(userId)
                    .setValue(userResponse).await()
            }
        }
    }

    private fun denormalizePairs(state: MatchTheFollowingCardContentState): List<Int> {
        return state.response.connectedPairs.map { connectedIndex ->
            state.shuffledPairs.indexOf(connectedIndex)
        }
    }

    private fun checkCorrectness(denormalizedPairs: List<Int>): Boolean {
        return denormalizedPairs == denormalizedPairs.indices.toList()
    }


    /* Fill In The Blanks */
    private fun onFillInTheBlanksAnswerChange(answer: String) {
        val currentState = _uiState.value.currentCardState
        if (currentState is FillInTheBlanksCardContentState) {
            updateCardState(currentState.copy(response = FillInTheBlanksResponse(answer)))
        }
    }

    private fun onFillInTheBlanksAnswerSubmitted() {
        val currentState = _uiState.value.currentCardState
        if (currentState is FillInTheBlanksCardContentState) {
            viewModelScope.launch {
                val cardIndex = _uiState.value.currentCardIndex
                updateCardState(currentState.copy(isSubmitted = true))
                studentResponsesReference.child(cardIndex.toString()).child(userId)
                    .setValue(currentState.response.answer).await()
            }
        }
    }

    private fun calculateFillInTheBlanksResponseCounts(): Int {
        val currentCardIndex = _uiState.value.currentCardIndex
        val responses =
            _uiState.value.studentResponses[currentCardIndex]
        return if (responses != null) {
            (responses as Map<String, FillInTheBlanksResponse>).size
        } else {
            0
        }
    }

    /* Short Answer */
    private fun onShortAnswerChange(answer: String) {
        val currentState = _uiState.value.currentCardState
        if (currentState is ShortAnswerCardContentState) {
            updateCardState(currentState.copy(response = ShortAnswerResponse(answer)))
        }
    }

    private fun onShortAnswerSubmitted() {
        val currentState = _uiState.value.currentCardState
        if (currentState is ShortAnswerCardContentState) {
            viewModelScope.launch {
                val cardIndex = _uiState.value.currentCardIndex
                updateCardState(currentState.copy(isSubmitted = true))
                studentResponsesReference.child(cardIndex.toString()).child(userId)
                    .setValue(currentState.response.answer).await()
            }
        }
    }

    private fun calculateShortAnswerResponseCounts(): Int {
        val currentCardIndex = _uiState.value.currentCardIndex
        val responses =
            _uiState.value.studentResponses[currentCardIndex]
        return if (responses != null) {
            (responses as Map<String, ShortAnswerResponse>).size
        } else {
            0
        }
    }

    /* Yes No */
    private fun onYesNoOptionSelected(selectedOption: Int?) {
        val currentState = _uiState.value.currentCardState
        if (currentState is YesNoCardContentState) {
            updateCardState(
                currentState.copy(response = YesNoResponse(selectedOption))
            )
        }
    }

    private fun onYesNoAnswerSubmitted() {
        val currentState = _uiState.value.currentCardState
        if (currentState is YesNoCardContentState) {
            val cardIndex = _uiState.value.currentCardIndex
            val selectedOption = currentState.response.selectedOption
            viewModelScope.launch {
                selectedOption?.let { choice ->
                    updateCardState(currentState.copy(isSubmitted = true))
                    studentResponsesReference.child(cardIndex.toString()).child(userId)
                        .setValue(choice).await()


                }
            }
        }
    }

    private fun calculateYesNoResponseCounts(): List<Int> {
        val currentCardIndex = _uiState.value.currentCardIndex
        val responses = _uiState.value.studentResponses[currentCardIndex]
        val responseCounts = mutableListOf(0, 0, 0)
        if (responses != null) {
            (responses as Map<String, YesNoResponse>).values.forEach { response ->
                if (response.selectedOption != null) {
                    responseCounts[response.selectedOption]++
                }
            }
        }
        return responseCounts.toImmutableList()
    }


    /* Student Attendance */
    private fun addStudentToActiveList() {
        val studentEntry = mapOf(
            "joinTime" to ServerValue.TIMESTAMP
        )
        activeStudentsReference.child(userId).setValue(studentEntry)
    }

    private fun observeActiveStudents() {
        activeStudentsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeStudentsCount = snapshot.childrenCount.toInt()
                val activeStudentIds = snapshot.children.mapNotNull { it.key }.toSet()
                _uiState.update {
                    it.copy(
                        studentCount = activeStudentsCount,
                        activeStudentIds = activeStudentIds
                    )
                }
                updateCurrentCardState()
                viewModelScope.launch {
                    fetchUserDetails(activeStudentIds.toList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update { it.copy(error = error.message) }
            }
        })
    }


    /* Student Responses */
    private fun observeStudentResponses() {
        // if (isTeacher) {
        studentResponsesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val processedResponses = processStudentResponses(snapshot)
                _uiState.update { it.copy(studentResponses = processedResponses) }
                updateCurrentCardState()
                viewModelScope.launch {
                    fetchUserDetails(processedResponses.values.flatMap { it.keys }.distinct())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update { it.copy(error = error.message) }
            }
        })
        // }
    }

    private fun processStudentResponses(snapshot: DataSnapshot): Map<Int, Map<String, CardResponse>> {
        return snapshot.children.associate { cardSnapshot ->
            val cardIndex = cardSnapshot.key?.toIntOrNull() ?: -1
            val cardResponses = processCardResponses(cardIndex, cardSnapshot)
            cardIndex to cardResponses
        }.toMap()
    }

    private fun processCardResponses(
        cardIndex: Int,
        cardSnapshot: DataSnapshot
    ): Map<String, CardResponse> {
        val card = _uiState.value.cards.getOrNull(cardIndex) ?: return emptyMap()
        return cardSnapshot.children.associate { studentSnapshot ->
            val studentId = studentSnapshot.key ?: "GHOST"
            val response = createCardResponse(card.content, studentSnapshot)
            studentId to response
        }.toMap()
    }

    private fun createCardResponse(
        cardContent: CardContent,
        studentSnapshot: DataSnapshot
    ): CardResponse {
        when (cardContent) {
            is SimpleMCQCardContent -> {
                val selectedOption = studentSnapshot.getValue(Int::class.java)
                return SimpleMCQResponse(selectedOption)
            }

            is FillInTheBlanksCardContent -> {
                val answer = studentSnapshot.getValue(String::class.java)
                return FillInTheBlanksResponse(answer!!)
            }

            is MatchTheFollowingCardContent -> {
                val connectedPairs =
                    studentSnapshot.getValue(object : GenericTypeIndicator<List<Int>>() {})
                return MatchTheFollowingResponse(connectedPairs!!)
            }

            is ShortAnswerCardContent -> {
                val answer = studentSnapshot.getValue(String::class.java)
                return ShortAnswerResponse(answer!!)
            }

            is YesNoCardContent -> {
                val selectedOption = studentSnapshot.getValue(Int::class.java)
                return YesNoResponse(selectedOption)
            }

            else -> throw Exception("Card response cannot be parsed from RTDB")
        }
    }

    /* Teacher-Student Support Tools */
    fun toggleTeacherUIControls() {
        _uiState.update { it.copy(showTeacherUIControls = !it.showTeacherUIControls) }
    }

    fun onHandRaiseToggle() {
        _uiState.update { it.copy(raisedHand = !it.raisedHand) }
        viewModelScope.launch {
            val handRaiseRef = handRaiseReference.child(userId)
            if (_uiState.value.raisedHand) {
                handRaiseRef.setValue(ServerValue.TIMESTAMP).await()
            } else {
                handRaiseRef.removeValue().await()
            }
        }
    }

    private fun observeHandRaises() {
        handRaiseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val handRaisedUserIds = snapshot.children.map { it.key }
                if (isTeacher) {
                    viewModelScope.launch {
                        val handRaisedUsers = handRaisedUserIds.mapNotNull { userId ->
                            userRepository.getUser(userId!!).getOrNull()?.let { user ->
                                userDetailsMap[userId] = user
                                user
                            }
                        }
                        _uiState.update { it.copy(handRaisedStudents = handRaisedUsers) }
                    }
                } else {
                    viewModelScope.launch {
                        if (handRaisedUserIds.contains(userId)) {
                            _uiState.update { it.copy(raisedHand = true) }
                        } else {
                            _uiState.update { it.copy(raisedHand = false) }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update { it.copy(error = error.message) }
            }
        })
    }

    fun toggleStudentDoubtsDialog() {
        _uiState.update { it.copy(showStudentDoubtsDialog = !it.showStudentDoubtsDialog) }
    }

    fun onHandRaiseAcknowledged(userId: String) {
        viewModelScope.launch {
            handRaiseReference.child(userId).removeValue().await()
        }
    }

    fun onAllHandRaiseAcknowledged() {
        viewModelScope.launch {
            handRaiseReference.removeValue().await()
            toggleStudentDoubtsDialog()
        }
    }

    /* Lifecycle-related methods */
    override fun onCleared() {
        super.onCleared()
        if (!isTeacher) {
            moveStudentToLeftList()
        }
    }

    fun showResultsModal() {
        _uiState.update { it.copy(showResultsModal = true) }
    }

    fun dismissResultModal() {
        _uiState.update { it.copy(showResultsModal = false) }
    }

    /* Utility methods */
    private suspend fun fetchUserDetails(userIds: List<String>) {
        userIds.forEach { userId ->
            if (!userDetailsMap.containsKey(userId)) {
                userRepository.getUser(userId).onSuccess { user ->
                    userDetailsMap[userId] = user
                }
            }
        }
        _uiState.update { it.copy(userDetailsMap = userDetailsMap.toMap()) }
    }
}

data class LiveSessionDeckPlayerUiState(
    val error: String? = null,

    val isLoading: Boolean = true,
    val showHandRaiseDialog: Boolean = false,
    val showStudentDoubtsDialog: Boolean = false,
    val showTeacherUIControls: Boolean = true,
    val showResultsModal: Boolean = false,
    val raisedHand: Boolean = false,

    val session: Session? = null,
    val cards: List<Card> = emptyList(),

    val currentCardIndex: Int = 0,
    val currentCard: Card? = null,
    val currentCardState: CardContentState? = null,

    val studentCount: Int = 0,
    val activeStudentIds: Set<String> = emptySet(),
    val studentResponses: Map<Int, Map<String, CardResponse>> = emptyMap(),
    val handRaisedStudents: List<User> = listOf(),
    val userDetailsMap: Map<String, User> = emptyMap()
)

class LiveSessionDeckPlayerViewModelFactory(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val sessionRepository: SessionRepository,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
    private val firebaseDatabase: DatabaseReference,
    private val onCloseLiveSession: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveSessionDeckPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LiveSessionDeckPlayerViewModel(
                encryptedPreferencesHelper,
                sessionRepository,
                deckRepository,
                cardRepository,
                userRepository,
                firebaseDatabase,
                onCloseLiveSession
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}