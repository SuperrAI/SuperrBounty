package com.superr.bounty.data.mapper

import com.google.firebase.Timestamp
import com.superr.bounty.data.dto.CardDTO
import com.superr.bounty.domain.model.card.Card
import com.superr.bounty.domain.model.card.CardContent
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContent
import com.superr.bounty.domain.model.card.ImageCardContent
import com.superr.bounty.domain.model.card.LinkToFileCardContent
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContent
import com.superr.bounty.domain.model.card.OneWordCardContent
import com.superr.bounty.domain.model.card.OpenEndedCardContent
import com.superr.bounty.domain.model.card.ShortAnswerCardContent
import com.superr.bounty.domain.model.card.SimpleMCQCardContent
import com.superr.bounty.domain.model.card.SimpleVoteCardContent
import com.superr.bounty.domain.model.card.ThisThatCardContent
import com.superr.bounty.domain.model.card.YesNoCardContent

private const val TAG = "Superr.CardMapper"

object CardMapper {
    fun dtoToDomain(dto: CardDTO): Card {
        return Card(
            id = dto.id,
            type = dto.type,
            title = dto.title,
            description = dto.description,
            createdAt = dto.createdAt.toDate(),
            updatedAt = dto.updatedAt.toDate(),
            content = mapContentToDomain(dto.type, dto.content)
        )
    }

    fun domainToDto(card: Card): CardDTO {
        return CardDTO(
            id = card.id,
            type = card.type,
            title = card.title,
            description = card.description,
            createdAt = Timestamp(card.createdAt),
            updatedAt = Timestamp(card.updatedAt),
            content = mapContentToDto(card.content)
        )
    }

    private fun mapContentToDomain(type: String, content: Map<String, Any>): CardContent {
        return when (type) {
            "FillInTheBlanks" -> FillInTheBlanksCardContent(
                beforeText = content["beforeText"] as String,
                afterText = content["afterText"] as String,
                answer = content["answer"] as String
            )

            "Image" -> ImageCardContent(
                imageUrl = content["imageUrl"] as String
            )

            "LinkToFile" -> LinkToFileCardContent(
                fileUrl = content["fileUrl"] as String,
                fileType = content["fileType"] as String
            )

            "SimpleMCQ" -> SimpleMCQCardContent(
                question = content["question"] as String,
                options = content["options"] as List<String>,
                correctAnswer = (content["correctAnswer"] as Long).toInt()
            )

            "MatchTheFollowing" -> MatchTheFollowingCardContent(
                question = content["question"] as String,
                pairs = (content["pairs"] as List<Map<String, String>>).map {
                    Pair(it["left"] as String, it["right"] as String)
                }
            )

            "ShortAnswer" -> ShortAnswerCardContent(
                question = content["question"] as String,
                answer = content["answer"] as String,
                maxLength = (content["maxLength"] as Long).toInt()
            )

            "OneWord" -> OneWordCardContent(
                prompt = content["prompt"] as String,
                correctAnswer = content["correctAnswer"] as String
            )

            "YesNo" -> YesNoCardContent(
                question = content["question"] as String
            )

            "ThisThat" -> ThisThatCardContent(
                optionA = content["optionA"] as String,
                optionB = content["optionB"] as String
            )

            "OpenEnded" -> OpenEndedCardContent(
                question = content["question"] as String
            )

            "SimpleVote" -> SimpleVoteCardContent(
                question = content["question"] as String,
                options = content["options"] as List<String>
            )

            else -> throw IllegalArgumentException("Unknown card type: $type")
        }
    }

    private fun mapContentToDto(content: CardContent): Map<String, Any> {
        return when (content) {
            is FillInTheBlanksCardContent -> mapOf(
                "beforeText" to content.beforeText,
                "afterText" to content.afterText,
                "answer" to content.answer
            )

            is ImageCardContent -> mapOf(
                "imageUrl" to content.imageUrl
            )

            is LinkToFileCardContent -> mapOf(
                "fileUrl" to content.fileUrl,
                "fileType" to content.fileType
            )

            is SimpleMCQCardContent -> mapOf(
                "question" to content.question,
                "options" to content.options,
                "correctAnswer" to content.correctAnswer
            )

            is MatchTheFollowingCardContent -> mapOf(
                "question" to content.question,
                "pairs" to content.pairs.map { mapOf("left" to it.first, "right" to it.second) }
            )

            is ShortAnswerCardContent -> mapOf(
                "question" to content.question,
                "answer" to content.answer,
                "maxLength" to content.maxLength
            )

            is OneWordCardContent -> mapOf(
                "prompt" to content.prompt,
                "correctAnswer" to content.correctAnswer
            )

            is YesNoCardContent -> mapOf(
                "question" to content.question
            )

            is ThisThatCardContent -> mapOf(
                "optionA" to content.optionA,
                "optionB" to content.optionB
            )

            is OpenEndedCardContent -> mapOf(
                "question" to content.question
            )

            is SimpleVoteCardContent -> mapOf(
                "question" to content.question,
                "options" to content.options
            )
        }
    }
}