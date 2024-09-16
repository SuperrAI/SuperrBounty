package com.superr.bounty.ui.common.deckplayer.livesession

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.superr.bounty.R
import com.superr.bounty.domain.model.User
import com.superr.bounty.domain.model.card.Card
import com.superr.bounty.domain.model.card.CardResponse
import com.superr.bounty.domain.model.card.FillInTheBlanksCardContent
import com.superr.bounty.domain.model.card.FillInTheBlanksResponse
import com.superr.bounty.domain.model.card.MatchTheFollowingCardContent
import com.superr.bounty.domain.model.card.MatchTheFollowingResponse
import com.superr.bounty.domain.model.card.ShortAnswerCardContent
import com.superr.bounty.domain.model.card.ShortAnswerResponse
import com.superr.bounty.domain.model.card.SimpleMCQCardContent
import com.superr.bounty.domain.model.card.SimpleMCQResponse
import com.superr.bounty.domain.model.card.YesNoCardContent
import com.superr.bounty.domain.model.card.YesNoResponse
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.FlatIconButton
import com.superr.bounty.utils.FlatTab
import com.superr.bounty.utils.SwipeGestureHandler
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.flatClickable
import com.superr.bounty.utils.fsp
import kotlin.math.max
import kotlin.random.Random

@Composable
fun LiveSessionDeckPlayerScreen(
    viewModel: LiveSessionDeckPlayerViewModel, sessionId: String, isTeacher: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.initializeSession(sessionId, isTeacher)
    }

    when {
        uiState.isLoading -> LoadingScreen()
        uiState.error != null -> ErrorScreen(error = uiState.error.toString())
        else -> {
            SwipeGestureHandler(
                onSwipeLeft = viewModel::moveToNextCard,
                onSwipeRight = viewModel::moveToPreviousCard
            ) {
                LiveSessionDeckPlayerContent(
                    uiState = uiState,
                    isTeacher = isTeacher,
                    onCloseSession = viewModel::onCloseSession,
                    onHandRaiseToggle = viewModel::onHandRaiseToggle,
                    onStudentDoubtsToggle = viewModel::toggleStudentDoubtsDialog,
                    onHandRaiseResolve = viewModel::onHandRaiseAcknowledged,
                    onAllHandRaiseResolve = viewModel::onAllHandRaiseAcknowledged,
                    onToggleTeacherUIControls = viewModel::toggleTeacherUIControls,
                    onDismissResultModal = viewModel::dismissResultModal
                )
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(error: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.live_session_deck_player_error_text, error))
    }
}

@Composable
fun LiveSessionDeckPlayerContent(
    uiState: LiveSessionDeckPlayerUiState,
    isTeacher: Boolean,
    onCloseSession: () -> Unit,
    onHandRaiseToggle: () -> Unit,
    onStudentDoubtsToggle: () -> Unit,
    onHandRaiseResolve: (String) -> Unit,
    onAllHandRaiseResolve: () -> Unit,
    onToggleTeacherUIControls: () -> Unit,
    onDismissResultModal: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        CurrentCard(uiState)

        if (isTeacher) {
            TeacherUI(
                uiState,
                onCloseSession,
                onStudentDoubtsToggle,
                onHandRaiseResolve,
                onAllHandRaiseResolve,
                onToggleTeacherUIControls,
                onDismissResultModal
            )
        } else {
            StudentUI(uiState, onCloseSession, onHandRaiseToggle)
        }
    }
}


@Composable
fun BoxScope.CurrentCard(uiState: LiveSessionDeckPlayerUiState) {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
    ) {
        uiState.currentCard?.let { card ->
            uiState.currentCardState?.let { state ->
                card.content.getContentView(state)
            }
        }
    }
}

@Composable
fun BoxScope.TeacherUI(
    uiState: LiveSessionDeckPlayerUiState,
    onCloseSession: () -> Unit,
    onStudentDoubtsToggle: () -> Unit,
    onHandRaiseResolve: (String) -> Unit,
    onAllHandRaiseResolve: () -> Unit,
    onToggleTeacherUIControls: () -> Unit,
    onDismissResultModal: () -> Unit
) {
    if (uiState.showTeacherUIControls) {
        // Top bar
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.fdp, start = 12.fdp, end = 12.fdp)
        ) {
            TeacherTopBar(
                currentCardIndex = uiState.currentCardIndex,
                studentCount = uiState.studentCount,
                onClose = onCloseSession
            )
        }

        // Teacher Hand Raise button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.fdp, end = 12.fdp)
                .wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .border(2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(16.fdp))
                    .padding(6.fdp)
            ) {

                FlatIconButton(
                    modifier = Modifier.size(60.fdp),
                    onClick = onStudentDoubtsToggle
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hand),
                        contentDescription = "",
                        tint = SuperrTheme.colorScheme.Black
                    )
                }
            }
            if (uiState.handRaisedStudents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(14.fdp)
                        .border(2.fdp, SuperrTheme.colorScheme.White, RoundedCornerShape(100.fdp))
                        .padding(2.fdp)
                        .border(5.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(100.fdp))
                ) {}
            }
        }


        if (uiState.showStudentDoubtsDialog) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 116.fdp, end = 12.fdp)
            ) {
                Column(
                    modifier = Modifier
                        .size(560.fdp, 992.fdp)
                        .border(
                            2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(24.fdp)
                        )
                        .padding(top = 12.fdp, end = 0.fdp, bottom = 8.fdp, start = 0.fdp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.fdp, bottom = 12.fdp)
                            .padding(vertical = 8.fdp, horizontal = 16.fdp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Doubts",
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(modifier = Modifier
                            .border(
                                2.fdp,
                                SuperrTheme.colorScheme.Gray300,
                                RoundedCornerShape(12.fdp)
                            )
                            .padding(12.fdp)
                            .flatClickable { onAllHandRaiseResolve() }) {
                            Text(
                                text = "Resolve All",
                                style = SuperrTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (uiState.handRaisedStudents.isEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.fdp,
                            color = SuperrTheme.colorScheme.Gray300
                        )
                    }

                    uiState.handRaisedStudents.forEach { it ->
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.fdp,
                            color = SuperrTheme.colorScheme.Gray300
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.fdp)
                                .padding(vertical = 4.fdp, horizontal = 12.fdp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${it.name} raised their hand",
                                style = SuperrTheme.typography.bodySmall
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.fdp)
                                    .border(
                                        2.fdp,
                                        SuperrTheme.colorScheme.Gray300,
                                        RoundedCornerShape(12.fdp)
                                    )
                                    .flatClickable { onHandRaiseResolve(it.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.fdp),
                                    painter = painterResource(id = R.drawable.ic_tick),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom tools
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.fdp)
        ) {
            TeacherBottomToolbar()
        }
    }

    // Bottom left button
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 12.fdp, start = 12.fdp)
    ) {
        FlatIconButton(
            modifier = Modifier.size(60.fdp),
            onClick = { onToggleTeacherUIControls() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_looky_eyes),
                contentDescription = "",
                tint = Color.Unspecified
            )
        }
    }

    if (uiState.showResultsModal) {
        ResultModal(
            currentCard = uiState.currentCard,
            studentResponses = uiState.studentResponses[uiState.currentCardIndex] ?: emptyMap(),
            userDetailsMap = uiState.userDetailsMap,
            activeStudentIds = uiState.activeStudentIds,
            onDismissResultModal = onDismissResultModal
        )
    }

}

@Composable
fun BoxScope.StudentUI(
    uiState: LiveSessionDeckPlayerUiState,
    onCloseSession: () -> Unit,
    onHandRaiseToggle: () -> Unit,
) {

    FlatIconButton(
        // TODO: Check and verify clickable
        onClick = onCloseSession,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 16.fdp, start = 16.fdp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = stringResource(id = R.string.deck_player_close_quiz_desc)
        )
    }

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 12.fdp, end = 12.fdp)
            .wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.fdp))
                .background(if (uiState.raisedHand) SuperrTheme.colorScheme.Black else SuperrTheme.colorScheme.White)
                .border(
                    2.fdp,
                    if (uiState.raisedHand) SuperrTheme.colorScheme.Black else SuperrTheme.colorScheme.Gray300,
                    RoundedCornerShape(16.fdp)
                )
                .padding(6.fdp)
        ) {

            FlatIconButton(
                modifier = Modifier
                    .size(60.fdp)
                    .background(if (uiState.raisedHand) SuperrTheme.colorScheme.Black else SuperrTheme.colorScheme.White),
                onClick = onHandRaiseToggle
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hand),
                    contentDescription = "",
                    tint = if (uiState.raisedHand) SuperrTheme.colorScheme.White else SuperrTheme.colorScheme.Black
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 12.fdp, end = 12.fdp)
    ) {
        StudentCardInfo(
            currentCardIndex = uiState.currentCardIndex + 1, cardCount = uiState.cards.size
        )
    }
}

@Composable
fun TeacherTopBar(
    currentCardIndex: Int, studentCount: Int, onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.fdp)) {
            CloseButton(onClose = onClose)
            TeacherDeckManagementTools()
        }

        PresentationStatus(studentCount = studentCount)

        TeacherCardInfo(
            currentCardIndex = currentCardIndex
        )
    }
}

@Composable
fun TeacherDeckManagementTools() {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(60.fdp)
            .border(2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(16.fdp))
            .padding(horizontal = 10.fdp, vertical = 6.fdp),
        horizontalArrangement = Arrangement.spacedBy(4.fdp)
    ) {
        FlatIconButton(modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
            // TODO: Implement clickable
            onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_grid),
                contentDescription = stringResource(id = R.string.deck_player_close_quiz_desc)
            )
        }
        FlatIconButton(modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
            // TODO: Implement clickable
            onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_page),
                contentDescription = stringResource(id = R.string.deck_player_close_quiz_desc)
            )
        }
    }
}

@Composable
fun PresentationStatus(studentCount: Int) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.fdp))
            .background(SuperrTheme.colorScheme.Black)
            .padding(top = 4.fdp, end = 10.fdp, bottom = 4.fdp, start = 4.fdp),
        horizontalArrangement = Arrangement.spacedBy(8.fdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.fdp))
                .background(SuperrTheme.colorScheme.White)
                .padding(top = 4.fdp, end = 10.fdp, bottom = 4.fdp, start = 4.fdp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Presenting",
                fontSize = 16.fsp,
                lineHeight = 20.fsp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = if (studentCount == 1) "1 student" else "$studentCount students",
            color = SuperrTheme.colorScheme.White,
            style = SuperrTheme.typography.labelMedium,
            lineHeight = 20.fsp
        )
    }
}

@Composable
fun TeacherBottomToolbar() {
    Row(
        modifier = Modifier
            .height(96.fdp)
            .border(2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(24.fdp))
            .padding(horizontal = 12.fdp)
            .clipToBounds(),
        horizontalArrangement = Arrangement.spacedBy(4.fdp),
        verticalAlignment = Alignment.Top
    ) {
        // Tool buttons
        listOf(
            R.drawable.ic_pencil_big,
            R.drawable.ic_eraser_big,
            R.drawable.ic_textbox_add,
            R.drawable.ic_image_add
        ).forEach { iconRes ->
            ToolButton(iconRes)
        }

        // Separator
        Box(
            modifier = Modifier
                .width(2.fdp)
                .fillMaxHeight()
                .border(2.fdp, SuperrTheme.colorScheme.Gray300)
        )

        // File box
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .padding(20.fdp)
                .flatClickable { }, contentAlignment = Alignment.Center
        ) {
            OverlappingRotatedFileBoxes("file", R.drawable.ic_superr_icon)
        }
    }
}

@Composable
fun OverlappingRotatedFileBoxes(text: String, icon: Int) {
    val randomRotations = remember {
        val firstRotation = Random.nextFloat() * 20
        List(3) { index ->
            when (index % 3) {
                0 -> firstRotation
                1 -> -Random.nextFloat() * 20
                else -> Random.nextFloat() * 20
            }
        }
    }

    Box(contentAlignment = Alignment.Center) {
        // Background boxes
        (0..2).forEach { index ->
            val (width, height) = when (index) {
                0 -> Pair(84.05.fdp, 41.05.fdp)
                1 -> Pair(98.67.fdp, 44.03.fdp)
                else -> Pair(116.08.fdp, 43.5.fdp)
            }
            Box(
                modifier = Modifier
                    .size(width = width, height = height)
                    .graphicsLayer(rotationZ = randomRotations[index])
                    .border(3.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
            )
        }

        // Content
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(width = 116.08.fdp, height = 43.5.fdp)
                .padding(4.fdp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "File Icon",
                modifier = Modifier.size(24.fdp),
                tint = SuperrTheme.colorScheme.Black
            )
            Text(
                text = text,
                color = SuperrTheme.colorScheme.Black,
                fontFamily = RawNoteFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.fsp,
                lineHeight = 30.14.fsp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ToolButton(iconRes: Int) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .height(116.fdp)
            .padding(horizontal = 12.fdp)
            .flatClickable { },
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "",
            modifier = Modifier
                .requiredHeight(84.fdp)
                .align(Alignment.BottomCenter)
                .offset(y = 32.fdp),
        )
    }
}

@Composable
fun CloseButton(onClose: () -> Unit) {
    FlatIconButton(
        modifier = Modifier
            .size(60.fdp)
            .border(
                2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(16.fdp)
            )
            .padding(6.fdp),
        // TODO: Check and verify clickable
        onClick = onClose
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = stringResource(id = R.string.deck_player_close_quiz_desc),
            tint = SuperrTheme.colorScheme.Gray500
        )
    }
}

@Composable
fun TeacherCardInfo(currentCardIndex: Int) {
    Row(
        modifier = Modifier
            .height(60.fdp)
            .border(2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(16.fdp))
            .padding(6.fdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.fdp)
    ) {
        FlatIconButton(modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
            onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_speech_bubble),
                contentDescription = stringResource(id = R.string.deck_player_card_info_description)
            )
        }
        Text(
            text = stringResource(
                id = R.string.deck_player_card_progress_teacher,
                currentCardIndex + 1,
            ), style = SuperrTheme.typography.bodySmall
        )
        FlatIconButton(modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(),
            onClick = { /* TODO: Implement clickable */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_timer),
                contentDescription = stringResource(id = R.string.deck_player_timer_description)
            )
        }
    }
}

@Composable
fun StudentCardInfo(currentCardIndex: Int, cardCount: Int) {
    Row(
        modifier = Modifier
            .border(
                1.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(100.fdp)
            )
            .padding(top = 12.fdp, end = 16.fdp, bottom = 12.fdp, start = 16.fdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(
                id = R.string.deck_player_card_progress, currentCardIndex, cardCount
            ), style = SuperrTheme.typography.bodySmall, fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(16.fdp))
        LinearProgressIndicator(
            progress = { currentCardIndex.toFloat() / cardCount.toFloat() },
            modifier = Modifier.width(62.fdp),
        )
    }
}

@Composable
fun HandRaiseDialog(
    onRaiseHand: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.fdp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 4.fdp, horizontal = 20.fdp)
                    .fillMaxWidth()
                    .weight(5f)
            ) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .padding(0.fdp)
                    .border(
                        2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(
                            topStart = 0.fdp,
                            topEnd = 0.fdp,
                            bottomEnd = 24.fdp,
                            bottomStart = 24.fdp
                        )
                    )
                    .padding(vertical = 8.fdp, horizontal = 16.fdp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_keyboard), contentDescription = ""
                )
                Row(
                    modifier = Modifier.wrapContentHeight(),
                    horizontalArrangement = Arrangement.spacedBy(8.fdp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .border(
                                2.fdp,
                                SuperrTheme.colorScheme.Gray300,
                                RoundedCornerShape(12.fdp)
                            )
                            .padding(horizontal = 12.fdp, vertical = 12.fdp)
                            .flatClickable { onRaiseHand() }, contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Raise Hand",
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(12.fdp))
                            .background(SuperrTheme.colorScheme.Black)
                            .padding(horizontal = 12.fdp, vertical = 12.fdp)
                            .flatClickable { /* Implement raise hand logic */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ask",
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SuperrTheme.colorScheme.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultModal(
    currentCard: Card?,
    studentResponses: Map<String, CardResponse>,
    userDetailsMap: Map<String, User>,
    activeStudentIds: Set<String>,
    onDismissResultModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(12.fdp)
            .clip(RoundedCornerShape(20.fdp))
            .background(SuperrTheme.colorScheme.White)
            .border(2.fdp, SuperrTheme.colorScheme.Gray300, RoundedCornerShape(20.fdp))
    ) {
        ResultModalHeader(onDismissResultModal)
        ResultModalContent(
            currentCard,
            studentResponses,
            userDetailsMap,
            activeStudentIds,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun BoxScope.ResultModalHeader(onDismissResultModal: () -> Unit) {
    Text(
        text = stringResource(id = R.string.result_modal_header),
        style = SuperrTheme.typography.titleLarge,
        fontFamily = RawNoteFontFamily,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 40.fdp, start = 48.fdp)
    )
    FlatIconButton(
        onClick = onDismissResultModal,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 24.fdp, end = 18.fdp)
            .padding(vertical = 4.fdp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = stringResource(id = R.string.dismiss_show_result_modal)
        )
    }
}

@Composable
private fun ResultModalContent(
    currentCard: Card?,
    studentResponses: Map<String, CardResponse>,
    userDetailsMap: Map<String, User>,
    activeStudentIds: Set<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 128.fdp),
        verticalArrangement = Arrangement.spacedBy(40.fdp)
    ) {
        currentCard?.let { card ->
            when (card.content) {
                is SimpleMCQCardContent -> SimpleMCQSection(card.content, studentResponses)
                is FillInTheBlanksCardContent -> FillInTheBlanksSection(
                    card.content, studentResponses
                )

                is MatchTheFollowingCardContent -> MatchTheFollowingSection(
                    card.content, studentResponses
                )

                is ShortAnswerCardContent -> ShortAnswerSection(card.content, studentResponses)

                is YesNoCardContent -> YesNoSection(card.content, studentResponses)

                else -> Text("Unsupported card type for results")
            }
        }
        ResultsSection(currentCard, studentResponses, userDetailsMap, activeStudentIds)
    }
}

@Composable
private fun SimpleMCQSection(content: SimpleMCQCardContent, responses: Map<String, CardResponse>) {
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.59.fdp)
                    .fillMaxWidth()
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(24.fdp))
                    .padding(top = 42.fdp, end = 0.fdp, bottom = 24.fdp, start = 24.fdp),
                verticalArrangement = Arrangement.spacedBy(32.fdp)
            ) {

                Text(content.question, style = SuperrTheme.typography.bodyLarge)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.fdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick),
                        contentDescription = null
                    )
                    Text(
                        text = content.options[content.correctAnswer],
                        style = SuperrTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.fdp)
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
                    .padding(vertical = 5.fdp, horizontal = 16.fdp)
            ) {
                Text(
                    "MCQ",
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
private fun FillInTheBlanksSection(
    content: FillInTheBlanksCardContent, responses: Map<String, CardResponse>
) {
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.59.fdp)
                    .fillMaxWidth()
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(24.fdp))
                    .padding(top = 42.fdp, end = 24.fdp, bottom = 24.fdp, start = 24.fdp),
                verticalArrangement = Arrangement.spacedBy(32.fdp)
            ) {

                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = SuperrTheme.colorScheme.Gray500)) {
                        append(content.beforeText)
                    }
                    withStyle(style = SpanStyle(color = SuperrTheme.colorScheme.Black)) {
                        append(" ${content.answer} ")
                    }
                    withStyle(style = SpanStyle(color = SuperrTheme.colorScheme.Gray500)) {
                        append(content.afterText)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.fdp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick),
                        contentDescription = null
                    )
                    Text(
                        annotatedString, style = SuperrTheme.typography.labelLarge
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.fdp)
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
                    .padding(vertical = 5.fdp, horizontal = 16.fdp)
            ) {
                Text(
                    "Fill in the blanks",
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
private fun MatchTheFollowingSection(
    content: MatchTheFollowingCardContent, responses: Map<String, CardResponse>
) {
    val annotatedString = buildAnnotatedString {
        content.pairs.forEachIndexed { index, pair ->
            append("${pair.first} → ${pair.second}")
            if (index != content.pairs.size - 1) {
                append(", ")
            }
        }
    }
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.59.fdp)
                    .fillMaxWidth()
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(24.fdp))
                    .padding(top = 42.fdp, end = 0.fdp, bottom = 24.fdp, start = 24.fdp),
                verticalArrangement = Arrangement.spacedBy(32.fdp)
            ) {

                Text(content.question, style = SuperrTheme.typography.bodyLarge)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.fdp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick),
                        contentDescription = null
                    )
                    Text(
                        text = annotatedString,
                        style = SuperrTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.fdp)
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
                    .padding(vertical = 5.fdp, horizontal = 16.fdp)
            ) {
                Text(
                    "Match the following",
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
private fun ShortAnswerSection(
    content: ShortAnswerCardContent, responses: Map<String, CardResponse>
) {
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.59.fdp)
                    .fillMaxWidth()
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(24.fdp))
                    .padding(top = 42.fdp, end = 24.fdp, bottom = 24.fdp, start = 24.fdp),
                verticalArrangement = Arrangement.spacedBy(32.fdp)
            ) {
                Text(content.question, style = SuperrTheme.typography.bodyLarge)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.fdp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick),
                        contentDescription = null
                    )
                    Text(
                        text = content.answer, style = SuperrTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.fdp)
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
                    .padding(vertical = 5.fdp, horizontal = 16.fdp)
            ) {
                Text(
                    "Short Answer",
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
private fun YesNoSection(content: YesNoCardContent, responses: Map<String, CardResponse>) {
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 18.59.fdp)
                    .fillMaxWidth()
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(24.fdp))
                    .padding(top = 42.fdp, end = 24.fdp, bottom = 24.fdp, start = 24.fdp),
                verticalArrangement = Arrangement.spacedBy(32.fdp)
            ) {
                Text(content.question, style = SuperrTheme.typography.bodyLarge)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.fdp)
                    .border(2.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(10.fdp))
                    .background(SuperrTheme.colorScheme.White)
                    .padding(vertical = 5.fdp, horizontal = 16.fdp)
            ) {
                Text(
                    "Poll",
                    style = SuperrTheme.typography.labelLarge,
                    fontFamily = RawNoteFontFamily
                )
            }
        }
    }
}

@Composable
private fun ResultsSection(
    currentCard: Card?,
    responses: Map<String, CardResponse>,
    userDetailsMap: Map<String, User>,
    activeStudentIds: Set<String>
) {
    Column(
        modifier = Modifier
            .width(796.fdp)
            .wrapContentHeight()
    ) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = when (currentCard?.content) {
            is SimpleMCQCardContent -> {
                val content = currentCard.content as SimpleMCQCardContent
                val correctCount =
                    responses.count { it.value is SimpleMCQResponse && (it.value as SimpleMCQResponse).selectedOption == content.correctAnswer }
                val incorrectCount =
                    responses.count { it.value is SimpleMCQResponse && (it.value as SimpleMCQResponse).selectedOption != null && (it.value as SimpleMCQResponse).selectedOption != content.correctAnswer }
                val didntAnswerCount =
                    max(0, activeStudentIds.size - (correctCount + incorrectCount))
                listOf(
                    "$correctCount correct",
                    "$incorrectCount incorrect",
                    "$didntAnswerCount didn't answer"
                )
            }

            is FillInTheBlanksCardContent -> {
                val content = currentCard.content as FillInTheBlanksCardContent
                val correctCount = responses.count {
                    it.value is FillInTheBlanksResponse && (it.value as FillInTheBlanksResponse).answer.equals(
                        content.answer, ignoreCase = true
                    )
                }
                val incorrectCount = responses.count {
                    it.value is FillInTheBlanksResponse && (it.value as FillInTheBlanksResponse).answer.isNotEmpty() && !(it.value as FillInTheBlanksResponse).answer.equals(
                        content.answer, ignoreCase = true
                    )
                }
                val didntAnswerCount = activeStudentIds.size - (correctCount + incorrectCount)
                listOf(
                    "$correctCount correct",
                    "$incorrectCount incorrect",
                    "$didntAnswerCount didn't answer"
                )
            }

            is MatchTheFollowingCardContent -> {
                val content = currentCard.content as MatchTheFollowingCardContent
                val correctCount =
                    responses.count { it.value is MatchTheFollowingResponse && (it.value as MatchTheFollowingResponse).connectedPairs == (0 until content.pairs.size).toList() }
                val incorrectCount =
                    responses.count { it.value is MatchTheFollowingResponse && (it.value as MatchTheFollowingResponse).connectedPairs.isNotEmpty() && (it.value as MatchTheFollowingResponse).connectedPairs != (0 until content.pairs.size).toList() }
                val didntAnswerCount = activeStudentIds.size - (correctCount + incorrectCount)
                listOf(
                    "$correctCount correct",
                    "$incorrectCount incorrect",
                    "$didntAnswerCount didn't answer"
                )
            }

            is ShortAnswerCardContent -> {
                val content = currentCard.content as ShortAnswerCardContent
                val correctCount = responses.count {
                    it.value is ShortAnswerResponse && (it.value as ShortAnswerResponse).answer.equals(
                        content.answer, ignoreCase = true
                    )
                }
                val incorrectCount = responses.count {
                    it.value is ShortAnswerResponse && (it.value as ShortAnswerResponse).answer.isNotEmpty() && !(it.value as ShortAnswerResponse).answer.equals(
                        content.answer, ignoreCase = true
                    )
                }
                val didntAnswerCount = activeStudentIds.size - (correctCount + incorrectCount)
                listOf(
                    "$correctCount correct",
                    "$incorrectCount incorrect",
                    "$didntAnswerCount didn't answer"
                )
            }

            is YesNoCardContent -> {
                val yesCount =
                    responses.count { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 0 }
                val noCount =
                    responses.count { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 1 }
                val maybeCount =
                    responses.count { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 2 }
                listOf(
                    "$yesCount Yes",
                    "$noCount No",
                    "$maybeCount Maybe"
                )
            }

            else -> listOf("Responses")
        }

        FullWidthTabRow(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            tabs = tabs
        )

        Spacer(modifier = Modifier.height(16.fdp))

        ResultsList(currentCard, responses, userDetailsMap, activeStudentIds, selectedTabIndex)
    }
}

@Composable
private fun ResultsList(
    currentCard: Card?,
    responses: Map<String, CardResponse>,
    userDetailsMap: Map<String, User>,
    activeStudentIds: Set<String>,
    selectedTabIndex: Int,
) {
    val filteredResponses = when (currentCard?.content) {
        is SimpleMCQCardContent -> {
            val content = currentCard.content as SimpleMCQCardContent
            when (selectedTabIndex) {
                0 -> responses.filter { it.value is SimpleMCQResponse && (it.value as SimpleMCQResponse).selectedOption == content.correctAnswer }
                1 -> responses.filter { it.value is SimpleMCQResponse && (it.value as SimpleMCQResponse).selectedOption != content.correctAnswer && (it.value as SimpleMCQResponse).selectedOption != null }
                2 -> {
                    val answeredStudentIds = responses.keys
                    activeStudentIds.filter { it !in answeredStudentIds }
                        .associateWith { SimpleMCQResponse(null) }
                }

                else -> emptyMap()
            }
        }

        is FillInTheBlanksCardContent -> {
            val content = currentCard.content as FillInTheBlanksCardContent
            when (selectedTabIndex) {
                0 -> responses.filter {
                    it.value is FillInTheBlanksResponse && (it.value as FillInTheBlanksResponse).answer.equals(
                        content.answer, ignoreCase = true
                    )
                }

                1 -> responses.filter {
                    it.value is FillInTheBlanksResponse && !(it.value as FillInTheBlanksResponse).answer.equals(
                        content.answer, ignoreCase = true
                    ) && (it.value as FillInTheBlanksResponse).answer.isNotEmpty()
                }

                2 -> {
                    val answeredStudentIds = responses.keys
                    activeStudentIds.filter { it !in answeredStudentIds }
                        .associateWith { FillInTheBlanksResponse("") }
                }

                else -> emptyMap()
            }
        }

        is MatchTheFollowingCardContent -> {
            val content = currentCard.content as MatchTheFollowingCardContent
            when (selectedTabIndex) {
                0 -> responses.filter { it.value is MatchTheFollowingResponse && (it.value as MatchTheFollowingResponse).connectedPairs == (0 until content.pairs.size).toList() }
                1 -> responses.filter { it.value is MatchTheFollowingResponse && (it.value as MatchTheFollowingResponse).connectedPairs != (0 until content.pairs.size).toList() && (it.value as MatchTheFollowingResponse).connectedPairs.isNotEmpty() }
                2 -> {
                    val answeredStudentIds = responses.keys
                    activeStudentIds.filter { it !in answeredStudentIds }
                        .associateWith { MatchTheFollowingResponse(emptyList()) }
                }

                else -> emptyMap()
            }
        }

        is ShortAnswerCardContent -> {
            val content = currentCard.content as ShortAnswerCardContent
            when (selectedTabIndex) {
                0 -> responses.filter {
                    it.value is ShortAnswerResponse && (it.value as ShortAnswerResponse).answer.equals(
                        content.answer,
                        ignoreCase = true
                    )
                }

                1 -> responses.filter {
                    it.value is ShortAnswerResponse && !(it.value as ShortAnswerResponse).answer.equals(
                        content.answer,
                        ignoreCase = true
                    ) && (it.value as ShortAnswerResponse).answer.isNotEmpty()
                }

                2 -> {
                    val answeredStudentIds = responses.keys
                    activeStudentIds.filter { it !in answeredStudentIds }
                        .associateWith { ShortAnswerResponse("") }
                }

                else -> emptyMap()
            }
        }

        is YesNoCardContent -> {
            when (selectedTabIndex) {
                0 -> responses.filter { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 0 }
                1 -> responses.filter { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 1 }
                2 -> responses.filter { it.value is YesNoResponse && (it.value as YesNoResponse).selectedOption == 2 }
                else -> emptyMap()
            }
        }

        else -> responses
    }

    filteredResponses.forEach { (userId, response) ->
        ResultItem(
            userName = userDetailsMap[userId]?.name ?: userId,
            response = response,
            isIncorrect = (selectedTabIndex == 1),
            currentCard = currentCard
        )
        HorizontalDivider(
            thickness = 2.fdp,
            modifier = Modifier.fillMaxWidth(),
            color = SuperrTheme.colorScheme.Gray300
        )
    }
}

@Composable
fun FullWidthTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val fullWidth = maxWidth

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .width(fullWidth)
                .drawBehind {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, size.height),
                        end = Offset(fullWidth.toPx(), size.height),
                        strokeWidth = 2.fdp.toPx()
                    )
                }
                .padding(vertical = 4.fdp, horizontal = 12.fdp),
            horizontalArrangement = Arrangement.spacedBy(24.fdp),
        ) {
            tabs.forEachIndexed { index, title ->
                TabItem(title = title,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) })
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String, isSelected: Boolean, onClick: () -> Unit
) {
    FlatTab(
        selected = isSelected, onClick = onClick
    ) {
        Box(modifier = Modifier
            .padding(bottom = 12.fdp)
            .drawBehind {
                if (isSelected) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height + 15.fdp.toPx()),
                        end = Offset(size.width, size.height + 15.fdp.toPx()),
                        strokeWidth = 2.fdp.toPx()
                    )
                }
            }) {
            Text(
                text = title,
                style = SuperrTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) SuperrTheme.colorScheme.Black else SuperrTheme.colorScheme.Gray500
            )
        }
    }
}

@Composable
fun ResultItem(
    userName: String,
    response: CardResponse,
    isIncorrect: Boolean,
    currentCard: Card?
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.padding(vertical = 13.fdp, horizontal = 12.fdp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.fdp)
        ) {
            Text(text = userName, style = SuperrTheme.typography.bodySmall)
            if (isIncorrect) {
                when (response) {
                    is SimpleMCQResponse -> {
                        val content = currentCard?.content as? SimpleMCQCardContent
                        Text(text = response.selectedOption?.let {
                            content?.options?.getOrNull(
                                it
                            )
                        } ?: "Not answered",
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = SuperrTheme.colorScheme.Gray500)
                    }

                    is FillInTheBlanksResponse -> {
                        Text(
                            text = response.answer.ifEmpty { "Not answered" },
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = SuperrTheme.colorScheme.Gray500
                        )
                    }

                    is MatchTheFollowingResponse -> {
                        val content = currentCard?.content as? MatchTheFollowingCardContent
                        Text(
                            text = if (response.connectedPairs.isEmpty()) "Not answered" else response.connectedPairs.joinToString(
                                ", "
                            ) {
                                "${
                                    content?.pairs?.getOrNull(it)?.first
                                } → ${
                                    content?.pairs?.getOrNull(it)?.second
                                }"
                            },
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = SuperrTheme.colorScheme.Gray500
                        )
                    }

                    is ShortAnswerResponse -> {
                        Text(
                            text = response.answer.ifEmpty { "Not answered" },
                            style = SuperrTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = SuperrTheme.colorScheme.Gray500
                        )
                    }

                    is YesNoResponse -> {}

                    else -> {

                    }
                }
            }
        }
    }
}