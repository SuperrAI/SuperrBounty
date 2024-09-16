package com.superr.bounty.ui.common.deckplayer.selfpaced

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.superr.bounty.domain.model.UserRole
import com.superr.bounty.domain.model.card.Card
import com.superr.bounty.domain.model.card.CardContentState
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
import com.superr.bounty.utils.EncryptedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

private const val TAG = "Superr.DeckPlayerViewModel"

class SelfPacedDeckPlayerViewModel(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val onClose: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelfPacedDeckPlayerUiState())
    val uiState: StateFlow<SelfPacedDeckPlayerUiState> = _uiState

    fun loadDeck(deckId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deckRepository.getDeck(deckId).onSuccess { deck ->
                Log.i(TAG, "loadDeck: Fetched Deck with ${deck.cardIds.size} cards")
                loadCards(deck.cardIds)
            }.onFailure { throwable ->
                Log.e(TAG, "Error loading deck: ${throwable.message}", throwable)
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }

    fun moveToNextCard() {
        _uiState.update { currentState ->
            if (currentState.currentCardIndex < currentState.cards.size - 1) {
                val nextIndex = currentState.currentCardIndex + 1
                val nextCard = currentState.cards[nextIndex]
                currentState.copy(
                    currentCardIndex = nextIndex,
                    currentCard = nextCard,
                    currentCardState = createCardState(nextCard)
                )
            } else {
                currentState
            }
        }
    }

    fun moveToPreviousCard() {
        _uiState.update { currentState ->
            if (currentState.currentCardIndex > 1) {
                val prevIndex = currentState.currentCardIndex - 1
                val prevCard = currentState.cards[prevIndex]
                currentState.copy(
                    currentCardIndex = prevIndex,
                    currentCard = prevCard,
                    currentCardState = createCardState(prevCard)
                )
            } else {
                currentState
            }
        }

    }

    fun onSubmit() {
        moveToNextCard()
    }

    fun onCloseClick() {
        onClose()
    }

    fun onNavigateBack() {
        // Implement navigate back logic
    }

    private fun loadCards(cardIds: List<String>) {
        viewModelScope.launch {
            cardRepository.getCardsByIds(cardIds).onSuccess { cardList ->
                Log.i(TAG, "loadCards: Fetched ${cardList.size} cards")
                val firstCard = cardList.firstOrNull()
                _uiState.update {
                    it.copy(isLoading = false,
                        cards = cardList,
                        currentCardIndex = 0,
                        currentCard = firstCard,
                        currentCardState = firstCard?.let { card -> createCardState(card) })
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Error loading cards: ${throwable.message}", throwable)
                _uiState.update { it.copy(isLoading = false, error = throwable.message) }
            }
        }
    }

    private fun createCardState(card: Card): CardContentState {
        return when (val content = card.content) {
            is ImageCardContent -> ImageCardContentState(
                isTeacher = isTeacher(), onSubmit = { }, response = ImageResponse
            )


            is SimpleMCQCardContent -> SimpleMCQCardContentState(
                isTeacher = isTeacher(),
                response = SimpleMCQResponse(selectedOption = (_uiState.value.currentCardState as? SimpleMCQCardContentState)?.response?.selectedOption),
                onOptionSelected = ::onSimpleMCQOptionSelected,
            )

            is FillInTheBlanksCardContent -> FillInTheBlanksCardContentState(
                isTeacher = isTeacher(),
                response = FillInTheBlanksResponse(answer = ""),
                onAnswerChange = ::onFillInTheBlanksAnswerChange
            )

            is MatchTheFollowingCardContent -> {
                val cardContent = card.content as MatchTheFollowingCardContent
                val shuffledRightIndices = (0 until cardContent.pairs.size).shuffled()
                return MatchTheFollowingCardContentState(
                    isTeacher = isTeacher(),
                    onSubmit = ::onMatchTheFollowingAnswerSubmitted,
                    response = MatchTheFollowingResponse(connectedPairs = List(cardContent.pairs.size) { -1 }),
                    isSubmitted = false,
                    shuffledPairs = shuffledRightIndices
                )
            }

            is ShortAnswerCardContent -> ShortAnswerCardContentState(
                isTeacher = isTeacher(),
                onSubmit = { },
                response = ShortAnswerResponse(answer = ""),
                totalResponses = 0,
                totalActiveStudents = 0,
                onAnswerChange = { },
                onShowResult = { }
            )

            is YesNoCardContent -> YesNoCardContentState(
                isTeacher = isTeacher(),
                response = YesNoResponse(),
                responseCounts = listOf(0, 0, 0),
                totalActiveStudents = 0,
                onOptionSelected = {},
                onShowResult = {},
                onSubmit = { }
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

    private fun onSimpleMCQOptionSelected(optionIndex: Int) {
        val currentState = _uiState.value.currentCardState
        if (currentState is SimpleMCQCardContentState) {
            updateCardState(
                currentState.copy(
                    response = SimpleMCQResponse(selectedOption = optionIndex)
                )
            )
        }
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
            val isCorrect = currentState.shuffledPairs.equals(currentState.response.connectedPairs)
            Log.i(
                TAG,
                "onMatchTheFollowingAnswerSubmitted: originalPairs: ${currentState.shuffledPairs}, submittedPairs: ${currentState.response.connectedPairs}, isCorrect: $isCorrect"
            )
        }
    }

    private fun onFillInTheBlanksAnswerChange(answer: String) {
        val currentState = _uiState.value.currentCardState
        if (currentState is FillInTheBlanksCardContentState) {
            updateCardState(currentState.copy(response = FillInTheBlanksResponse(answer = answer)))
        }
    }

    fun encryptedPreferencesHelper(): EncryptedPreferencesHelper = encryptedPreferencesHelper

    fun cardRepository(): CardRepository = cardRepository

    fun deckRepository(): DeckRepository = deckRepository

    fun isTeacher(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.TEACHER

    fun isStudent(): Boolean = encryptedPreferencesHelper.getUser().role == UserRole.STUDENT
}

data class SelfPacedDeckPlayerUiState(
    val isLoading: Boolean = false,
    val cards: List<Card> = emptyList(),
    val currentCardIndex: Int = 0,
    val currentCard: Card? = null,
    val currentCardState: CardContentState? = null,
    val error: String? = null
)

class SelfPacedDeckPlayerViewModelFactory(
    private val encryptedPreferencesHelper: EncryptedPreferencesHelper,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val onClose: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SelfPacedDeckPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return SelfPacedDeckPlayerViewModel(
                encryptedPreferencesHelper, cardRepository, deckRepository, onClose
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}