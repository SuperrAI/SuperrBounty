package com.superr.bounty.ui.common.deck.cards.mcq

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.superr.bounty.R
import com.superr.bounty.domain.model.card.SimpleMCQCardContent
import com.superr.bounty.domain.model.card.SimpleMCQCardContentState
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.DeckCardMCQContent"

@Composable
fun SimpleMCQCardContentUI(
    content: SimpleMCQCardContent,
    state: SimpleMCQCardContentState
) {
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

            Spacer(modifier = Modifier.height(16.fdp))

            Text(
                text = stringResource(R.string.mcq_select_the_correct_answer),
                style = SuperrTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.fdp))

            content.options.forEachIndexed { index, option ->
                DeckCardMCQOption(
                    text = option,
                    isSelected = state.response.selectedOption == index,
                    isCorrect = index == content.correctAnswer,
                    onClick = {
                        if (!state.isTeacher && !state.isSubmitted) {
                            state.onOptionSelected(index)
                        }
                    },
                    studentCount = state.responseCounts[index],
                    isTeacher = state.isTeacher,
                    isSubmitted = state.isSubmitted
                )
                Spacer(modifier = Modifier.height(8.fdp))
            }

            Spacer(modifier = Modifier.height(16.fdp))

            if (state.isTeacher) {
                val totalResponses = state.responseCounts.sum()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.fdp, horizontal = 16.fdp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.mcq_students_answered,
                            totalResponses,
                            state.totalActiveStudents
                        ),
                        style = SuperrTheme.typography.bodyMedium,
                        color = SuperrTheme.colorScheme.Gray500
                    )
                    Button(
                        onClick = { state.onShowResult() },
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
                            stringResource(R.string.show_results),
                            style = SuperrTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (!state.isTeacher) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Button(
                        onClick = state.onSubmit,
                        enabled = state.response.selectedOption != null && !state.isSubmitted,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(12.fdp)
                            .alpha(if (state.isSubmitted) 0f else 1f),
                        shape = RoundedCornerShape(12.fdp),
                        contentPadding = PaddingValues(12.fdp)
                    ) {
                        Text(
                            stringResource(R.string.mcq_submit),
                            style = SuperrTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (!state.isTeacher && state.isSubmitted) {
            val isCorrect = state.response.selectedOption == content.correctAnswer
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
fun DeckCardMCQOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    studentCount: Int,
    isTeacher: Boolean,
    isSubmitted: Boolean
) {
    val backgroundColor = when {
        isSelected -> SuperrTheme.colorScheme.Black
        isTeacher && isCorrect -> SuperrTheme.colorScheme.Black
        else -> SuperrTheme.colorScheme.White
    }

    val textColor = when {
        isSelected -> SuperrTheme.colorScheme.White
        isTeacher && isCorrect -> SuperrTheme.colorScheme.White
        else -> SuperrTheme.colorScheme.Black
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isSubmitted
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(16.fdp))
                .border(1.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(16.fdp))
                .padding(20.fdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    id = when {
                        isSelected -> R.drawable.ic_checked_radio_white
                        isCorrect -> R.drawable.ic_checked_radio_white
                        else -> R.drawable.ic_unchecked_radio_grey
                    }
                ),
                contentDescription = stringResource(
                    when {
                        isSelected -> R.string.mcq_white_checked_radio
                        isCorrect -> R.string.mcq_white_checked_radio
                        else -> R.string.mcq_grey_unchecked_radio
                    }
                ),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.fdp))
            Text(
                text = text,
                color = textColor,
                style = SuperrTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (isTeacher) {
                Spacer(modifier = Modifier.width(8.fdp))
                Text(
                    text = "$studentCount ${if (studentCount == 1) "student" else "students"}",
                    color = textColor.copy(alpha = 0.7f),
                    style = SuperrTheme.typography.bodyMedium,
                    textAlign = TextAlign.End
                )
            } else {
                Spacer(modifier = Modifier.width(8.fdp))
                Text(
                    text = if (isCorrect) "Correct" else "Incorrect",
                    color = textColor.copy(alpha = if (!isSubmitted) 0f else 0.7f),
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}