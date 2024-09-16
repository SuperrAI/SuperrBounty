package com.superr.bounty.data.mapper

import com.superr.bounty.data.dto.KvDTO
import com.superr.bounty.domain.model.Kv

private const val TAG = "Superr.KvMapper"

object KvMapper {
    fun mapToDto(kv: Kv): KvDTO {
        return KvDTO(
            key = kv.key,
            value = kv.value
        )
    }

    fun mapToDomain(kvDto: KvDTO): Kv {
        return Kv(
            key = kvDto.key,
            value = kvDto.value
        )
    }
}