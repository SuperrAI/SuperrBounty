package com.superr.bounty.ui.common.deck.cards.fitb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.superr.bounty.R
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContent
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContentState
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.ui.theme.withColor
import com.superr.bounty.ui.theme.withFontWeight
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.fsp
import kotlin.math.roundToInt

private const val TAG = "Superr.DeckCardFillInTheBlanksContent"

@Composable
fun FillInTheBlanksCardContentUI(
    content: FillInTheBlanksCardContent, state: FillInTheBlanksCardContentState
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .width(764.fdp)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(24.fdp)
            ) {
                if (state.isTeacher) {
                    Row(
                        modifier = Modifier
                            .padding(4.fdp)
                            .padding(vertical = 20.fdp, horizontal = 10.fdp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(content.beforeText)
                                append(" ")

                                withStyle(
                                    style = SpanStyle(
                                        color = SuperrTheme.colorScheme.Gray500,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append(content.answer)
                                }
                                append(" ")
                                append(content.afterText)
                            }, style = SuperrTheme.typography.headlineMedium
                        )
                    }
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
                                state.totalResponses,
                                state.totalActiveStudents
                            ),
                            style = SuperrTheme.typography.bodyMedium,
                            color = SuperrTheme.colorScheme.Gray500
                        )
                        Button(
                            // TODO: Check and verify clickable
                            onClick = state.onShowResult,
                            shape = RoundedCornerShape(12.fdp),
                            contentPadding = PaddingValues(12.fdp)
                        ) {
                            Text(
                                "Show Result",
                                style = SuperrTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.deck_card_fill_in_the_blank),
                        style = SuperrTheme.typography.bodyMedium.withColor(SuperrTheme.colorScheme.Gray500),
                        modifier = Modifier
                            .padding(start = 8.fdp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                    Box(modifier = Modifier.height(400.fdp)) {
                        Row(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(4.fdp)
                                .padding(vertical = 20.fdp, horizontal = 10.fdp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append(content.beforeText)
                                    append(" ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = SuperrTheme.colorScheme.Black,
                                            letterSpacing = (-2f).fsp
                                        )
                                    ) {
                                        append("_".repeat((content.answer.length * 1.5).roundToInt()))
                                    }
                                    append(" ")
                                    append(content.afterText)
                                },
                                style = SuperrTheme.typography.headlineMedium,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Button(
                            onClick = {
                                state.onSubmit()
                            },
                            enabled = !state.isSubmitted,
                            shape = RoundedCornerShape(12.fdp),
                            contentPadding = PaddingValues(12.fdp),
                            modifier = Modifier.alpha(if (state.isSubmitted) 0f else 1f)
                        ) {
                            Text(
                                "Submit",
                                style = SuperrTheme.typography.bodySmall.withFontWeight(FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }
        }
        if (!state.isTeacher && state.isSubmitted) {
            val isCorrect = state.response.answer.equals(content.answer, ignoreCase = true)
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
                    text = if (isCorrect) {
                        stringResource(R.string.mcq_correct_answer_format, state.response.answer)
                    } else {
                        stringResource(R.string.mcq_incorrect_answer_format, state.response.answer)
                    }, style = SuperrTheme.typography.titleMedium, fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}