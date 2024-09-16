package com.superr.bounty.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.superr.bounty.data.dto.DeckDTO
import com.superr.bounty.data.mapper.DeckMapper
import com.superr.bounty.domain.model.Deck
import com.superr.bounty.domain.repository.DeckRepository
import kotlinx.coroutines.tasks.await

private const val TAG = "Superr.FirebaseDeckRepository"

class FirebaseDeckRepository(private val firestore: FirebaseFirestore) : DeckRepository {
    private val decksCollection = firestore.collection("decks")

    override suspend fun getDeck(id: String): Result<Deck> {
        return try {
            val snapshot = decksCollection.document(id).get().await()
            val deckDto = snapshot.toObject<DeckDTO>()
            deckDto?.let {
                Result.success(DeckMapper.dtoToDomain(it))
            } ?: Result.failure(NoSuchElementException("Deck not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createDeck(deck: Deck): Result<Unit> {
        return try {
            val deckDto = DeckMapper.domainToDto(deck)
            decksCollection.document(deck.id).set(deckDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDeck(deck: Deck): Result<Unit> {
        return try {
            val deckDto = DeckMapper.domainToDto(deck)
            decksCollection.document(deck.id).set(deckDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDeck(id: String): Result<Unit> {
        return try {
            decksCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllDecks(): Result<List<Deck>> {
        return try {
            val snapshot = decksCollection.get().await()
            val decks = snapshot.toObjects(DeckDTO::class.java).map { DeckMapper.dtoToDomain(it) }
            Result.success(decks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDecksByIds(ids: List<String>): Result<List<Deck>> {
        return try {
            val decks = ids.mapNotNull { id ->
                decksCollection.document(id).get().await().toObject<DeckDTO>()
            }.map { DeckMapper.dtoToDomain(it) }
            Result.success(decks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}