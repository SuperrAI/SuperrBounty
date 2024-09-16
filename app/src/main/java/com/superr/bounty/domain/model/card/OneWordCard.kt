package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

data class OneWordCardContent(
    val prompt: String,
    val correctAnswer: String
) : CardContent() {

    @Composable
    override fun getContentView(
        state: CardContentState
    ) {
        require(state is OneWordCardContentState) { "Invalid state type for OneWord card" }
    }

    override fun toParcelable(): ParcelableCardContent {
        return ParcelableOneWordCardContent(prompt, correctAnswer)
    }
}

@Parcelize
data class ParcelableOneWordCardContent(val prompt: String, val answer: String) :
    ParcelableCardContent()

data class OneWordCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: OneWordResponse = OneWordResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },
) : CardContentState()

data class OneWordResponse(val answer: String = "") : CardResponse()