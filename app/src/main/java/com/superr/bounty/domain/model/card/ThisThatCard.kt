package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

data class ThisThatCardContent(
    val optionA: String, val optionB: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is ThisThatCardContentState) { "Invalid state type for Fill in the Blanks card" }
    }

    override fun toParcelable(): ParcelableCardContent =
        ParcelableThisThatCardContent(optionA, optionB)
}

@Parcelize
data class ParcelableThisThatCardContent(val optionA: String, val optionB: String) :
    ParcelableCardContent()

data class ThisThatCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: ThisThatResponse = ThisThatResponse(),

    val isSubmitted: Boolean = false,
    val onShowResult: () -> Unit = { },
) : CardContentState()

data class ThisThatResponse(val choseThis: Boolean? = null) : CardResponse()