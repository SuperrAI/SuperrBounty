package com.superr.bounty.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.superr.bounty.data.dto.CardDTO
import com.superr.bounty.data.mapper.CardMapper
import com.superr.bounty.domain.model.card.Card
import com.superr.bounty.domain.repository.CardRepository
import kotlinx.coroutines.tasks.await
import java.util.Date

private const val TAG = "Superr.FirebaseCardRepository"

class FirebaseCardRepository(private val firestore: FirebaseFirestore) : CardRepository {
    private val cardsCollection = firestore.collection("cards")

    override suspend fun getCard(id: String): Result<Card> {
        return try {
            val snapshot = cardsCollection.document(id).get().await()
            val cardDto = snapshot.toObject<CardDTO>()
            cardDto?.let {
                Result.success(CardMapper.dtoToDomain(it))
            } ?: Result.failure(NoSuchElementException("Card not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCard(card: Card): Result<Unit> {
        return try {
            val cardDto = CardMapper.domainToDto(card)
            cardsCollection.document(card.id).set(cardDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCard(card: Card): Result<Unit> {
        return try {
            val cardDto = CardMapper.domainToDto(card.copy(updatedAt = Date()))
            cardsCollection.document(card.id).set(cardDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCard(id: String): Result<Unit> {
        return try {
            cardsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCardsByIds(ids: List<String>): Result<List<Card>> {
        return try {
            val cards = ids.mapNotNull { id ->
                cardsCollection.document(id).get().await().toObject<CardDTO>()
            }.map { CardMapper.dtoToDomain(it) }
            Result.success(cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCardsByType(type: String): Result<List<Card>> {
        return try {
            val snapshot = cardsCollection.whereEqualTo("type", type).get().await()
            val cards = snapshot.toObjects(CardDTO::class.java).map { CardMapper.dtoToDomain(it) }
            Result.success(cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}