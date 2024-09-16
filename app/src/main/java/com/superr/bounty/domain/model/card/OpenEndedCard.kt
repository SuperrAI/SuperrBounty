package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

data class OpenEndedCardContent(
    val question: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is OpenEndedCardContentState) { "Invalid state type for Fill in the Blanks card" }
    }

    override fun toParcelable(): ParcelableCardContent = ParcelableOpenEndedCardContent(question)
}

@Parcelize
data class ParcelableOpenEndedCardContent(val question: String) : ParcelableCardContent()

data class OpenEndedCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: OpenEndedResponse = OpenEndedResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },
) : CardContentState()

data class OpenEndedResponse(val answer: String = "") : CardResponse()