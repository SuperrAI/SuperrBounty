package com.superr.bounty.domain.repository

import com.superr.bounty.domain.model.card.Card

private const val TAG = "Superr.Domain.Repository.Card"

interface CardRepository {
    suspend fun getCard(id: String): Result<Card>
    suspend fun createCard(card: Card): Result<Unit>
    suspend fun updateCard(card: Card): Result<Unit>
    suspend fun deleteCard(id: String): Result<Unit>
    suspend fun getCardsByIds(ids: List<String>): Result<List<Card>>
    suspend fun getCardsByType(type: String): Result<List<Card>>
}