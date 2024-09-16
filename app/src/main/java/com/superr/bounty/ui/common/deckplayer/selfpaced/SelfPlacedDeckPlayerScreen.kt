package com.superr.bounty.ui.common.deckplayer.selfpaced

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.superr.bounty.R
import com.superr.bounty.domain.model.Deck
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.FlatIconButton
import com.superr.bounty.utils.SwipeGestureHandler
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.DeckPlayerScreen"

@Composable
fun SelfPacedDeckPlayerScreen(
    viewModel: SelfPacedDeckPlayerViewModel,
    deck: Deck
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(deck.id) {
        viewModel.loadDeck(deck.id)
    }

    SwipeGestureHandler(
        onSwipeLeft = viewModel::moveToNextCard,
        onSwipeRight = viewModel::moveToPreviousCard
    ) {
        SelfPacedDeckPlayerContent(
            uiState = uiState,
            onClose = viewModel::onCloseClick,
            onNavigateBack = viewModel::onNavigateBack
        )
    }

}

@Composable
fun SelfPacedDeckPlayerContent(
    uiState: SelfPacedDeckPlayerUiState,
    onClose: () -> Unit,
    onNavigateBack: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.deck_player_error_text, uiState.error))
            }
        }

        uiState.cards.isNotEmpty() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.fdp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SelfPacedDeckPlayerTopBar(
                    currentCardIndex = uiState.currentCardIndex + 1,
                    totalCards = uiState.cards.size,
                    onClose = onClose
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.width(764.fdp)) {
                        uiState.currentCard?.let { card ->
                            uiState.currentCardState?.let { state ->
                                card.content.getContentView(state)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    FlatIconButton(
                        // TODO: Check and verify clickable
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_hand),
                            contentDescription = stringResource(id = R.string.deck_player_navigate_back_desc)
                        )
                    }
                }
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.deck_player_no_cards_text))
            }
        }
    }
}

@Composable
fun SelfPacedDeckPlayerTopBar(currentCardIndex: Int, totalCards: Int, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlatIconButton(
            // TODO: Check and verify clickable
            onClick = onClose
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.deck_player_close_quiz_desc)
            )
        }
        Row(
            modifier = Modifier
                .border(
                    1.fdp,
                    SuperrTheme.colorScheme.Gray300,
                    RoundedCornerShape(100.fdp)
                )
                .padding(top = 12.fdp, end = 16.fdp, bottom = 12.fdp, start = 16.fdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(
                    id = R.string.deck_player_card_progress,
                    currentCardIndex,
                    totalCards
                ),
                style = SuperrTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(16.fdp))
            LinearProgressIndicator(
                progress = { currentCardIndex.toFloat() / totalCards.toFloat() },
                modifier = Modifier.width(62.fdp),
            )
        }
    }
}
