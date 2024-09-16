package com.superr.bounty.domain.model.card

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

data class LinkToFileCardContent(
    val fileUrl: String, val fileType: String
) : CardContent() {

    @Composable
    override fun getContentView(state: CardContentState) {
        require(state is LinkToFileCardContentState) { "Invalid state type for LinkToFile card" }
    }

    override fun toParcelable(): ParcelableCardContent {
        return ParcelableLinkToFileCardContent(fileUrl, fileType)
    }
}

@Parcelize
data class ParcelableLinkToFileCardContent(val fileUrl: String, val fileType: String) :
    ParcelableCardContent()

data class LinkToFileCardContentState(
    override val isTeacher: Boolean = false,
    override val onSubmit: () -> Unit = {},
    override val response: LinkToFileResponse = LinkToFileResponse,
) : CardContentState()

object LinkToFileResponse : CardResponse()