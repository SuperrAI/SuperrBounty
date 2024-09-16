package com.superr.bounty.ui.common.deck.cards.mtf

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.superr.bounty.R
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContent
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContentState
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.MatchTheFollowingCardContentUI"

@Composable
fun MatchTheFollowingCardContentUI(
    content: MatchTheFollowingCardContent,
    state: MatchTheFollowingCardContentState
) {
    Log.i(
        TAG,
        "MatchTheFollowingCardContentUI: Matched pairs are: ${state.response.connectedPairs}"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(764.fdp)
        ) {
            Text(
                text = content.question,
                style = SuperrTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(52.fdp))

            Text(
                text = "Match the following",
                style = SuperrTheme.typography.bodyMedium,
                color = SuperrTheme.colorScheme.Gray500
            )

            val leftItems = content.pairs.map { it.first }
            val rightItems = content.pairs.map { it.second }
            val shuffledRightItems = MutableList(content.pairs.size) { "" }
            content.pairs.forEachIndexed { index, pair ->
                shuffledRightItems[state.shuffledPairs[index]] = pair.second
            }
            Row(modifier = Modifier.heightIn(min = Dp.Unspecified, max = 750.fdp)) {
                    MatchTheFollowingGrid(
                        isTeacher = state.isTeacher,
                        leftItems = leftItems,
                        rightItems = if (state.isTeacher) rightItems else shuffledRightItems,
                        connectedPairs = state.response.connectedPairs
                    )
            }

            Spacer(modifier = Modifier.height(16.fdp))

            if (!state.isTeacher) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = state.onSubmit,
                        enabled = state.response.connectedPairs.size == state.shuffledPairs.size && !state.isSubmitted,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(12.fdp)
                            .alpha(if (state.isSubmitted) 0f else 1f),
                        shape = RoundedCornerShape(12.fdp),
                        contentPadding = PaddingValues(12.fdp)
                    ) {
                        Text(
                            stringResource(R.string.mtf_submit),
                            style = SuperrTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = state.onShowResult,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(12.fdp)
                            .border(
                                2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(12.fdp)
                            ),
                        shape = RoundedCornerShape(12.fdp),
                        contentPadding = PaddingValues(12.fdp)
                    ) {
                        Text(
                            stringResource(R.string.mtf_show_results),
                            style = SuperrTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (!state.isTeacher && state.isSubmitted) {
            val isCorrect = state.shuffledPairs == state.response.connectedPairs
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.fdp),
                horizontalArrangement = Arrangement.spacedBy(20.fdp)
            ) {
                Icon(
                    painter = painterResource(id = if (isCorrect) R.drawable.ic_rough_circle_tick else R.drawable.ic_rough_cross),
                    contentDescription = ""
                )
                Text(
                    text = stringResource(if (isCorrect) R.string.mcq_correct_answer else R.string.mcq_incorrect_answer),
                    style = SuperrTheme.typography.titleMedium,
                    color = SuperrTheme.colorScheme.Black,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
fun MatchTheFollowingGrid(
    isTeacher: Boolean,
    leftItems: List<String>,
    rightItems: List<String>,
    connectedPairs: List<Int>
) {

    Column {
        leftItems.indices.forEach { i ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.fdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MatchTheFollowingItem(
                    item = leftItems[i],
                    index = i,
                    isLeft = true,
                    connectedPairs = connectedPairs,
                )

                if (isTeacher) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = ""
                    )
                }

                MatchTheFollowingItem(
                    item = rightItems[i],
                    index = i,
                    isLeft = false,
                    connectedPairs = connectedPairs,
                )
            }
        }
    }
}

@Composable
fun RowScope.MatchTheFollowingItem(
    item: String,
    index: Int,
    isLeft: Boolean,
    connectedPairs: List<Int>,
) {
    val isConnected = if (isLeft) {
        connectedPairs[index] != -1
    } else {
        connectedPairs.any { it == index }
    }

    Box(
        modifier = Modifier
            .height(150.fdp)
            .weight(1f)
            .padding(8.fdp)
            .border(
                if (isConnected) 4.fdp else 1.fdp,
                SuperrTheme.colorScheme.Black,
                RoundedCornerShape(16.fdp)
            )
            .padding(top = 24.fdp, end = 28.fdp, bottom = 24.fdp, start = 20.fdp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item,
            style = SuperrTheme.typography.bodyMedium,
            color = SuperrTheme.colorScheme.Black
        )
    }
}