package com.superr.bounty.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.superr.bounty.data.dto.SessionDTO
import com.superr.bounty.data.mapper.SessionMapper
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SessionStatus
import com.superr.bounty.domain.repository.SessionRepository
import kotlinx.coroutines.tasks.await
import java.util.Date

private const val TAG = "Superr.FirebaseSessionRepository"

class FirebaseSessionRepository(
    private val firestore: FirebaseFirestore,
    private val realtimeDatabase: DatabaseReference
) : SessionRepository {
    private val sessionsCollection = firestore.collection("sessions")

    override suspend fun getSession(id: String): Result<Session> {
        return try {
            val snapshot = sessionsCollection.document(id).get().await()
            val sessionDto = snapshot.toObject<SessionDTO>()
            sessionDto?.let {
                Result.success(SessionMapper.dtoToDomain(it))
            } ?: Result.failure(NoSuchElementException("Session not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSession(session: Session): Result<Unit> {
        return try {
            val sessionWithCode = if (session.code.isEmpty()) {
                session.copy(code = Session.generateCode())
            } else {
                session
            }
            val sessionDto = SessionMapper.domainToDto(sessionWithCode)
            sessionsCollection.document(sessionWithCode.id).set(sessionDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSession(session: Session): Result<Unit> {
        return try {
            val sessionDto = SessionMapper.domainToDto(session.copy(updatedAt = Date()))
            sessionsCollection.document(session.id).set(sessionDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(id: String): Result<Unit> {
        return try {
            sessionsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsForClass(classId: String): Result<List<Session>> {
        return try {
            val snapshot = sessionsCollection.whereEqualTo("classId", classId).get().await()
            val sessions =
                snapshot.toObjects(SessionDTO::class.java).map { SessionMapper.dtoToDomain(it) }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsByStatus(status: SessionStatus): Result<List<Session>> {
        return try {
            val snapshot = sessionsCollection.whereEqualTo("status", status.name).get().await()
            val sessions =
                snapshot.toObjects(SessionDTO::class.java).map { SessionMapper.dtoToDomain(it) }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsInTimeRange(
        startTime: Date, endTime: Date
    ): Result<List<Session>> {
        return try {
            val snapshot =
                sessionsCollection.whereGreaterThanOrEqualTo("startTime", Timestamp(startTime))
                    .whereLessThanOrEqualTo("endTime", Timestamp(endTime)).get().await()
            val sessions =
                snapshot.toObjects(SessionDTO::class.java).map { SessionMapper.dtoToDomain(it) }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsByDeckId(deckId: String): Result<List<Session>> {
        return try {
            val snapshot = sessionsCollection.whereArrayContains("deckIds", deckId).get().await()
            val sessions =
                snapshot.toObjects(SessionDTO::class.java).map { SessionMapper.dtoToDomain(it) }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addDeckToSession(sessionId: String, deckId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val sessionRef = sessionsCollection.document(sessionId)
                val sessionSnapshot = transaction.get(sessionRef)
                val currentDeckIds = sessionSnapshot.get("deckIds") as? List<String> ?: listOf()
                if (deckId !in currentDeckIds) {
                    transaction.update(sessionRef, "deckIds", currentDeckIds + deckId)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeDeckFromSession(sessionId: String, deckId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val sessionRef = sessionsCollection.document(sessionId)
                val sessionSnapshot = transaction.get(sessionRef)
                val currentDeckIds = sessionSnapshot.get("deckIds") as? List<String> ?: listOf()
                if (deckId in currentDeckIds) {
                    transaction.update(sessionRef, "deckIds", currentDeckIds - deckId)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifySessionCode(sessionId: String, code: String): Result<Boolean> {
        Log.i(TAG, "verifySessionCode: $sessionId, $code")
        return try {
            val snapshot = sessionsCollection
                .whereEqualTo("id", sessionId)
                .whereEqualTo("code", code)
                .get()
                .await()

            if (snapshot.documents.isEmpty()) {
                Result.failure(Exception("Invalid code for session"))
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startLiveSession(sessionId: String): Result<Unit> {
        return try {
            val sessionRef = realtimeDatabase.child("sessions/$sessionId")
            sessionRef.child("currentCardIndex").setValue(0).await()
            sessionsCollection.document(sessionId)
                .update("status", SessionStatus.IN_PROGRESS.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endLiveSession(sessionId: String): Result<Unit> {
        return try {
            val sessionRef = realtimeDatabase.child("sessions/$sessionId")
            sessionRef.removeValue().await()
            sessionsCollection.document(sessionId)
                .update("status", SessionStatus.COMPLETED.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCurrentCardIndex(sessionId: String, index: Int): Result<Unit> {
        return try {
            val sessionRef = realtimeDatabase.child("sessions/$sessionId")
            sessionRef.child("currentCardIndex").setValue(index).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}