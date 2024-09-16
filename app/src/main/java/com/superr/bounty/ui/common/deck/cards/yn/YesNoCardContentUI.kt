package com.superr.bounty.ui.common.deck.cards.yn

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.superr.bounty.R
import com.superr.bounty.domain.model.card.YesNoCardContent
import com.superr.bounty.domain.model.card.YesNoCardContentState
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.YesNoCardContentUI"

@Composable
fun YesNoCardContentUI(
    content: YesNoCardContent,
    state: YesNoCardContentState
) {
    Log.i(TAG, "YesNoCardContentUI: state: $state, content: $content")

    val totalResponses = state.responseCounts.sum()
    val yesResponses = state.responseCounts[0]
    val noResponses = state.responseCounts[1]
    val maybeResponses = state.responseCounts[2]

    val yesPercentage =
        if (totalResponses > 0) (yesResponses.toFloat() / totalResponses) * 100f else 0f
    val noPercentage =
        if (totalResponses > 0) (noResponses.toFloat() / totalResponses) * 100f else 0f
    val maybePercentage =
        if (totalResponses > 0) (100 - (yesPercentage + noPercentage)) else 0f

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(560.fdp),
        ) {

            Text(
                text = content.question,
                style = SuperrTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = RawNoteFontFamily
            )

            Spacer(modifier = Modifier.height(16.fdp))

            Text(
                text = if (state.isSubmitted) stringResource(R.string.yesno_youve_voted) else stringResource(
                    R.string.yesno_select_your_answer
                ),
                style = SuperrTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.fdp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.fdp)
            ) {
                YesNoOption(
                    text = "Yes",
                    onClick = { state.onOptionSelected(0) },

                    isTeacher = state.isTeacher,
                    isSubmitted = state.isSubmitted,
                    isSelected = state.response.selectedOption == 0,

                    totalVoteCount = totalResponses,
                    percentage = yesPercentage,
                    isMax = totalResponses > 0 && yesResponses > noResponses && yesResponses > maybeResponses
                )
                YesNoOption(
                    text = "No",
                    onClick = { state.onOptionSelected(1) },

                    isTeacher = state.isTeacher,
                    isSubmitted = state.isSubmitted,
                    isSelected = state.response.selectedOption == 1,

                    totalVoteCount = totalResponses,
                    percentage = noPercentage,
                    isMax = totalResponses > 0 && noResponses > yesResponses && noResponses > maybeResponses
                )
                YesNoOption(
                    text = "Maybe",
                    onClick = { state.onOptionSelected(2) },

                    isTeacher = state.isTeacher,
                    isSubmitted = state.isSubmitted,
                    isSelected = state.response.selectedOption == 2,

                    totalVoteCount = totalResponses,
                    percentage = maybePercentage,
                    isMax = totalResponses > 0 && maybeResponses > yesResponses && maybeResponses > noResponses
                )
            }

            Spacer(modifier = Modifier.height(16.fdp))

            if (state.isTeacher) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.fdp, horizontal = 16.fdp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.yesno_students_answered,
                            state.responseCounts.sum(),
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
                        shape = RoundedCornerShape(12.fdp),
                        contentPadding = PaddingValues(12.fdp)
                    ) {
                        Text(
                            stringResource(R.string.yesno_submit),
                            style = SuperrTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YesNoOption(
    text: String,
    onClick: () -> Unit,

    isTeacher: Boolean,
    isSubmitted: Boolean,
    isSelected: Boolean,

    totalVoteCount: Int,
    percentage: Float,
    isMax: Boolean,
) {

    var backgroundColor = SuperrTheme.colorScheme.White
    var progressColor = SuperrTheme.colorScheme.Gray300
    var textColor = SuperrTheme.colorScheme.Black

    if (isTeacher) {
        if (totalVoteCount > 0) {
            if (isMax) {
                progressColor = SuperrTheme.colorScheme.Black
                textColor = SuperrTheme.colorScheme.White
            }
        }
    } else {
        if (isSubmitted) {
            if (isSelected) {
                progressColor = SuperrTheme.colorScheme.Black
                textColor = SuperrTheme.colorScheme.White
            }
        } else {
            if (isSelected) {
                backgroundColor = SuperrTheme.colorScheme.Black
                textColor = SuperrTheme.colorScheme.White
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isSubmitted
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.fdp)
                .clip(RoundedCornerShape(16.fdp))
                .background(backgroundColor)
                .border(1.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(16.fdp))
        ) {
            // Background percentage fill
            if (isTeacher || (isSubmitted)) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percentage / 100f)
                        .background(progressColor, RoundedCornerShape(16.fdp))
                )
            }

            // Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.fdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    color = textColor,
                    style = SuperrTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (isSubmitted) {
                    Spacer(modifier = Modifier.width(8.fdp))
                    Text(
                        text = if (isSelected) "You" else "${
                            String.format(
                                "%.0f",
                                percentage
                            )
                        }%",
                        color = SuperrTheme.colorScheme.Black.copy(alpha = 0.7f),
                        style = SuperrTheme.typography.labelLarge,
                        fontFamily = RawNoteFontFamily,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}