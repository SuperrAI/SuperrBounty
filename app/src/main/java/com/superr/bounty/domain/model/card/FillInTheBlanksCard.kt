package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.fitb.FillInTheBlanksCardContentUI
import kotlinx.parcelize.Parcelize

data class FillInTheBlanksCardContent(
    val beforeText: String,
    val afterText: String,
    val answer: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is FillInTheBlanksCardContentState) { "Invalid state type for Fill in the Blanks card" }
        FillInTheBlanksCardContentUI(
            content = this,
            state = state
        )
    }

    override fun toParcelable() =
        ParcelableFillInTheBlanksCardContent(beforeText, afterText, answer)
}

@Parcelize
data class ParcelableFillInTheBlanksCardContent(
    val beforeText: String,
    val afterText: String,
    val answer: String
) : ParcelableCardContent()

data class FillInTheBlanksCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: FillInTheBlanksResponse = FillInTheBlanksResponse(),

    val totalResponses: Int = 0,
    val totalActiveStudents: Int = 0,

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },
    val onAnswerChange: (String) -> Unit = {}
) : CardContentState()

data class FillInTheBlanksResponse(val answer: String = "") : CardResponse()