package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

data class SimpleVoteCardContent(
    val question: String, val options: List<String>
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is SimpleVoteCardContentState) { "Invalid state type for Fill in the Blanks card" }
    }

    override fun toParcelable(): ParcelableCardContent =
        ParcelableSimpleVoteCardContent(question, options)
}

@Parcelize
data class ParcelableSimpleVoteCardContent(val question: String, val options: List<String>) :
    ParcelableCardContent()

data class SimpleVoteCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: SimpleVoteResponse = SimpleVoteResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },
) : CardContentState()

data class SimpleVoteResponse(val selectedOption: Int? = null) : CardResponse()