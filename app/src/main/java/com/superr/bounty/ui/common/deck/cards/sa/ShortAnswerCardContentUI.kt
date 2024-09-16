package com.superr.bounty.ui.common.deck.cards.sa

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.superr.bounty.R
import com.superr.bounty.domain.model.card.ShortAnswerCardContent
import com.superr.bounty.domain.model.card.ShortAnswerCardContentState
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp

private const val TAG = "Superr.DeckCardShortAnswerContent"

@Composable
fun ShortAnswerCardContentUI(
    content: ShortAnswerCardContent, state: ShortAnswerCardContentState
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(764.fdp)
        ) {
            Column(modifier = Modifier.padding(16.fdp)) {
                Spacer(modifier = Modifier.height(16.fdp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.fdp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(4.fdp)
                            .padding(vertical = 20.fdp, horizontal = 10.fdp)
                    ) {
                        Text(
                            text = content.question,
                            style = SuperrTheme.typography.bodyLarge,
                            lineHeight = 48.sp,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(16.fdp))

                        val lines = remember(content.answer) {
                            wrapText(
                                content.answer,
                                10
                            ) // Assume 10 words per line, adjust as needed
                        }

                        lines.take(lines.size).forEach { line ->
                            UnderlinedText(
                                text = line,
                                isTeacher = state.isTeacher
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.fdp))

                if (!state.isTeacher) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Button(
                            onClick = {
                                state.onSubmit()
                            },
                            enabled = !state.isSubmitted,
                            shape = RoundedCornerShape(12.fdp),
                            contentPadding = PaddingValues(12.fdp)
                        ) {
                            Text("Submit")
                        }
                    }
                } else {
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
                            onClick = state.onShowResult,
                            shape = RoundedCornerShape(12.fdp),
                            contentPadding = PaddingValues(12.fdp)
                        ) {
                            Text("Show results")
                        }
                    }
                }
            }
        }
    }
}

fun wrapText(text: String, wordsPerLine: Int): List<String> {
    val words = text.split(" ")
    return words.chunked(wordsPerLine).map { it.joinToString(" ") }
}

@Composable
fun UnderlinedText(text: String, isTeacher: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isTeacher) text else "",
            style = SuperrTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.fdp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.fdp)
                .background(SuperrTheme.colorScheme.Black)
        )
        Spacer(modifier = Modifier.height(16.fdp))
    }
}