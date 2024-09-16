package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.sa.ShortAnswerCardContentUI
import kotlinx.parcelize.Parcelize

data class ShortAnswerCardContent(
    val question: String, val answer: String, val maxLength: Int
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is ShortAnswerCardContentState) { "Invalid state type for Short Answer card" }
        ShortAnswerCardContentUI(
            content = this,
            state = state
        )
    }

    override fun toParcelable(): ParcelableCardContent =
        ParcelableShortAnswerCardContent(question, answer, maxLength)
}

@Parcelize
data class ParcelableShortAnswerCardContent(
    val question: String,
    val answer: String,
    val maxLength: Int
) :
    ParcelableCardContent()

data class ShortAnswerCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: ShortAnswerResponse = ShortAnswerResponse(),

    val totalResponses: Int = 0,
    val totalActiveStudents: Int = 0,

    val isSubmitted: Boolean = false,
    val onAnswerChange: (String) -> Unit = {},
    val onShowResult: () -> Unit = { }
) : CardContentState()

data class ShortAnswerResponse(val answer: String = "") : CardResponse()