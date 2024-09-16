package com.superr.bounty.domain.repository

import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SessionStatus
import java.util.Date

private const val TAG = "Superr.Domain.Repository.Session"

interface SessionRepository {
    suspend fun getSession(id: String): Result<Session>
    suspend fun createSession(session: Session): Result<Unit>
    suspend fun updateSession(session: Session): Result<Unit>
    suspend fun deleteSession(id: String): Result<Unit>
    suspend fun getSessionsForClass(classId: String): Result<List<Session>>
    suspend fun getSessionsByStatus(status: SessionStatus): Result<List<Session>>
    suspend fun getSessionsInTimeRange(startTime: Date, endTime: Date): Result<List<Session>>
    suspend fun getSessionsByDeckId(deckId: String): Result<List<Session>>
    suspend fun addDeckToSession(sessionId: String, deckId: String): Result<Unit>
    suspend fun removeDeckFromSession(sessionId: String, deckId: String): Result<Unit>
    suspend fun verifySessionCode(sessionId: String, code: String): Result<Boolean>
    suspend fun startLiveSession(sessionId: String): Result<Unit>
    suspend fun endLiveSession(sessionId: String): Result<Unit>
    suspend fun updateCurrentCardIndex(sessionId: String, index: Int): Result<Unit>
}