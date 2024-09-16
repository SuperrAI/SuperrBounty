package com.superr.bounty.ui.common.deck

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.superr.bounty.R
import com.superr.bounty.domain.model.Deck
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.ui.theme.withColor
import com.superr.bounty.ui.theme.withFontSize
import com.superr.bounty.ui.theme.withFontWeight
import com.superr.bounty.ui.theme.withTextAlign
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.flatClickable
import com.superr.bounty.utils.fsp

private const val TAG = "Superr.DeckButton"

@Composable
fun DeckButton(
    deck: Deck, onClick: () -> Unit, isSelected: Boolean = false
) {
    Row(modifier = Modifier
        .padding(top = 10.fdp, bottom = 10.fdp, start = 16.fdp, end = 16.fdp)
        .flatClickable {
            // TODO: Check and verify clickable
            onClick()
        }) {
        Column(
            modifier = Modifier.background(SuperrTheme.colorScheme.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(269.fdp)
                    .width(220.fdp)
            ) {
                Image(
                    painter = painterResource(id = if (isSelected) R.drawable.ic_deck_selected else R.drawable.ic_deck),
                    contentDescription = stringResource(R.string.deck_button_resource_icon),
                    modifier = Modifier.fillMaxSize()
                )

                if (deck.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = deck.coverImage,
                        contentDescription = stringResource(R.string.deck_button_deck_cover_image),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.fdp),
                        contentScale = ContentScale.Fit
                    )
                }

                if (isSelected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_checked_radio_white),
                        tint = SuperrTheme.colorScheme.Black,
                        contentDescription = stringResource(R.string.deck_button_selected_deck_item),
                        modifier = Modifier
                            .padding(bottom = 10.fdp, end = 10.fdp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            Text(
                text = deck.title,
                style = SuperrTheme.typography.labelSmall.withFontSize(16.fsp)
                    .withFontWeight(FontWeight.Medium)
                    .withColor(if (deck.title == "untitled") SuperrTheme.colorScheme.Gray400 else SuperrTheme.colorScheme.Black)
                    .withTextAlign(TextAlign.Center),
                modifier = Modifier
                    .width(150.fdp)
                    .padding(top = 5.fdp)
            )
        }
    }
}
