package com.superr.bounty.domain.model.card

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import java.util.Date

private const val TAG = "Superr.Domain.Card"

// Domain Model
data class Card(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val createdAt: Date,
    val updatedAt: Date,
    val content: CardContent
) {
    fun toParcelable() = ParcelableCard(
        id, type, title, description, createdAt.time, updatedAt.time, content.toParcelable()
    )
}

sealed class CardContent {

    @Composable
    abstract fun getContentView(state: CardContentState)

    abstract fun toParcelable(): ParcelableCardContent
}

sealed class CardContentState {
    abstract val isTeacher: Boolean
    abstract val onSubmit: () -> Unit
    abstract val response: CardResponse
}

sealed class CardResponse

@Parcelize
data class ParcelableCard(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val parcelableCardContent: ParcelableCardContent
) : Parcelable {
    fun toCard() = Card(
        id,
        type,
        title,
        description,
        Date(createdAt),
        Date(updatedAt),
        parcelableCardContent.toCardContent()
    )
}

@Parcelize
sealed class ParcelableCardContent : Parcelable {
    fun toCardContent(): CardContent = when (this) {
        is ParcelableSimpleMCQCardContent -> SimpleMCQCardContent(question, options, answer)
        is ParcelableImageCardContent -> ImageCardContent(imageUrl)
        is ParcelableShortAnswerCardContent -> ShortAnswerCardContent(question, answer, maxLength)
        is ParcelableYesNoCardContent -> YesNoCardContent(question)
        is ParcelableThisThatCardContent -> ThisThatCardContent(optionA, optionB)
        is ParcelableLinkToFileCardContent -> LinkToFileCardContent(fileUrl, fileType)
        is ParcelableOpenEndedCardContent -> OpenEndedCardContent(question)
        is ParcelableOneWordCardContent -> OneWordCardContent(prompt, answer)
        is ParcelableSimpleVoteCardContent -> SimpleVoteCardContent(question, options)
        is ParcelableMatchTheFollowingCardContent -> MatchTheFollowingCardContent(question, pairs)
        is ParcelableFillInTheBlanksCardContent -> FillInTheBlanksCardContent(
            beforeText,
            afterText,
            answer
        )
    }
}

