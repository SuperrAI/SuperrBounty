package com.superr.bounty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

private const val TAG = "Superr.Domain.Deck"

data class Deck(
    val id: String,
    val title: String,
    val description: String,
    val coverImage: String,
    val cardIds: List<String>,
    val createdAt: Date,
    val updatedAt: Date
)

@Parcelize
data class ParcelableDeck(
    val id: String,
    val title: String,
    val description: String,
    val coverImage: String,
    val cardIds: List<String>,
    val createdAt: Long,
    val updatedAt: Long
) : Parcelable

fun Deck.toParcelable() =
    ParcelableDeck(id, title, description, coverImage, cardIds, createdAt.time, updatedAt.time)

fun ParcelableDeck.toDeck() =
    Deck(id, title, description, coverImage, cardIds, Date(createdAt), Date(updatedAt))