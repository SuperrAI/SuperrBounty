package com.superr.bounty.ui.common.deck.cards.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.superr.bounty.domain.model.card.ImageCardContent
import com.superr.bounty.domain.model.card.ImageCardContentState

@Composable
fun ImageCardContentUI(
    content: ImageCardContent,
    state: ImageCardContentState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = content.imageUrl,
            contentDescription = "Card Image",
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(9f / 16f)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )
    }
}