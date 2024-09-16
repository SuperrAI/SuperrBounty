package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.mcq.SimpleMCQCardContentUI
import kotlinx.parcelize.Parcelize

data class SimpleMCQCardContent(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is SimpleMCQCardContentState) { "Invalid state type for MCQ card" }
        SimpleMCQCardContentUI(
            content = this,
            state = state
        )
    }

    override fun toParcelable() = ParcelableSimpleMCQCardContent(question, options, correctAnswer)
}

@Parcelize
data class ParcelableSimpleMCQCardContent(
    val question: String,
    val options: List<String>,
    val answer: Int
) : ParcelableCardContent()

data class SimpleMCQCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = { },
    override val response: SimpleMCQResponse = SimpleMCQResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },

    val onOptionSelected: (Int) -> Unit = { },
    val responseCounts: List<Int> = emptyList(),
    val totalActiveStudents: Int = 0
) : CardContentState()

data class SimpleMCQResponse(val selectedOption: Int? = null) : CardResponse()