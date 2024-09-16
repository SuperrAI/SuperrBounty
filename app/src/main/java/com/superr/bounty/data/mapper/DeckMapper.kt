package com.superr.bounty.data.mapper

import com.google.firebase.Timestamp
import com.superr.bounty.data.dto.DeckDTO
import com.superr.bounty.domain.model.Deck

private const val TAG = "Superr.DeckMapper"

object DeckMapper {
    fun dtoToDomain(dto: DeckDTO): Deck {
        return Deck(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            coverImage = dto.coverImage,
            cardIds = dto.cardIds,
            createdAt = dto.createdAt.toDate(),
            updatedAt = dto.updatedAt.toDate()
        )
    }

    fun domainToDto(deck: Deck): DeckDTO {
        return DeckDTO(
            id = deck.id,
            title = deck.title,
            description = deck.description,
            coverImage = deck.coverImage,
            cardIds = deck.cardIds,
            createdAt = Timestamp(deck.createdAt),
            updatedAt = Timestamp(deck.updatedAt)
        )
    }
}