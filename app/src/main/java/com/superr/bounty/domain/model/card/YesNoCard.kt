package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.yn.YesNoCardContentUI
import kotlinx.parcelize.Parcelize

data class YesNoCardContent(
    val question: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is YesNoCardContentState) { "Invalid state type for Yes/No card" }
        YesNoCardContentUI(
            content = this,
            state = state
        )
    }

    override fun toParcelable(): ParcelableCardContent = ParcelableYesNoCardContent(question)
}

@Parcelize
data class ParcelableYesNoCardContent(val question: String) : ParcelableCardContent()

data class YesNoCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: YesNoResponse = YesNoResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = {},

    val onOptionSelected: (Int) -> Unit = {},
    val responseCounts: List<Int> = listOf(0, 0, 0),
    val totalActiveStudents: Int = 0
) : CardContentState()

data class YesNoResponse(val selectedOption: Int? = null) : CardResponse()