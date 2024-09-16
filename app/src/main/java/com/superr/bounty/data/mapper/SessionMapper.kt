package com.superr.bounty.data.mapper

import com.google.firebase.Timestamp
import com.superr.bounty.data.dto.SessionDTO
import com.superr.bounty.domain.model.PresentationMode
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SessionStatus

private const val TAG = "Superr.SessionMapper"

object SessionMapper {
    fun dtoToDomain(dto: SessionDTO): Session {
        return Session(
            id = dto.id,
            classId = dto.classId,
            subjectId = dto.subjectId,
            title = dto.title,
            description = dto.description,
            startTime = dto.startTime.toDate(),
            endTime = dto.endTime.toDate(),
            deckIds = dto.deckIds,
            presentationMode = PresentationMode.valueOf(dto.presentationMode),
            timedDurationMinutes = dto.timedDurationMinutes,
            status = SessionStatus.valueOf(dto.status),
            createdAt = dto.createdAt.toDate(),
            updatedAt = dto.updatedAt.toDate(),
            code = dto.code
        )
    }

    fun domainToDto(session: Session): SessionDTO {
        return SessionDTO(
            id = session.id,
            classId = session.classId,
            subjectId = session.subjectId,
            title = session.title,
            description = session.description,
            startTime = Timestamp(session.startTime),
            endTime = Timestamp(session.endTime),
            deckIds = session.deckIds,
            presentationMode = session.presentationMode.name,
            timedDurationMinutes = session.timedDurationMinutes,
            status = session.status.name,
            createdAt = Timestamp(session.createdAt),
            updatedAt = Timestamp(session.updatedAt),
            code = session.code
        )
    }
}