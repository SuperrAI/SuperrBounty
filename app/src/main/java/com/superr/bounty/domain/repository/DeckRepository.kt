package com.superr.bounty.domain.repository

import com.superr.bounty.domain.model.Deck

private const val TAG = "Superr.Domain.Repository.Deck"

interface DeckRepository {
    suspend fun getDeck(id: String): Result<Deck>
    suspend fun createDeck(deck: Deck): Result<Unit>
    suspend fun updateDeck(deck: Deck): Result<Unit>
    suspend fun deleteDeck(id: String): Result<Unit>
    suspend fun getAllDecks(): Result<List<Deck>>
    suspend fun getDecksByIds(ids: List<String>): Result<List<Deck>>
}