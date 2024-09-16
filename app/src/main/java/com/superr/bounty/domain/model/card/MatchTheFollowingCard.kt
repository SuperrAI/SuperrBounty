package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.mtf.MatchTheFollowingCardContentUI
import kotlinx.parcelize.Parcelize

data class MatchTheFollowingCardContent(
    val question: String, val pairs: List<Pair<String, String>>
) : CardContent() {

    @Composable
    override fun getContentView(
        state: CardContentState
    ) {
        require(state is MatchTheFollowingCardContentState) { "Invalid state type for MatchTheFollowing card" }
        MatchTheFollowingCardContentUI(content = this, state = state)
    }

    override fun toParcelable() = ParcelableMatchTheFollowingCardContent(question, pairs)
}

@Parcelize
data class ParcelableMatchTheFollowingCardContent(
    val question: String,
    val pairs: List<Pair<String, String>>
) : ParcelableCardContent()

data class MatchTheFollowingCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: MatchTheFollowingResponse = MatchTheFollowingResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },

    val shuffledPairs: List<Int>
) : CardContentState()

data class MatchTheFollowingResponse(
    val connectedPairs: List<Int> = listOf()
) : CardResponse()