package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import com.superr.bounty.ui.common.deck.cards.image.ImageCardContentUI
import kotlinx.parcelize.Parcelize

data class ImageCardContent(
    val imageUrl: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is ImageCardContentState) { "Invalid state type for Image card" }
        ImageCardContentUI(
            content = this,
            state = state
        )
    }

    override fun toParcelable() = ParcelableImageCardContent(imageUrl)
}

@Parcelize
data class ParcelableImageCardContent(val imageUrl: String) : ParcelableCardContent()

data class ImageCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: ImageResponse = ImageResponse
) : CardContentState()

object ImageResponse : CardResponse()