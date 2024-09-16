package com.superr.bounty.ui.view.classroom.subjectclassroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superr.bounty.R
import com.superr.bounty.domain.model.SubjectClass
import com.superr.bounty.domain.repository.DeckRepository
import com.superr.bounty.domain.repository.SessionRepository
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.ui.view.classroom.subjectclassroom.tabs.sessions.SessionsTab
import com.superr.bounty.ui.view.classroom.subjectclassroom.tabs.sessions.SessionsTabViewModelFactory
import com.superr.bounty.utils.EncryptedPreferencesHelper
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.flatClickable

private const val TAG = "Superr.SubjectClassroomScreen"

@Composable
fun SubjectClassroomScreen(viewModel: SubjectClassroomViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(
                        id = R.string.subject_classroom_error_text, uiState.error!!
                    )
                )
            }
        }

        else -> {
            SubjectClassroomContent(
                uiState = uiState,
                isTeacher = viewModel.isTeacher(),
                encryptedPreferencesHelper = viewModel.encryptedPreferencesHelper(),
                sessionRepository = viewModel.sessionRepository(),
                deckRepository = viewModel.deckRepository(),
                onSessionJoin = viewModel::onSessionJoinAcceptance,
                onNavigateToHomeworkSubmissionReviewScreen = viewModel::onTeacherHomeworkClick
            )
        }
    }
}

@Composable
fun SubjectClassroomContent(
    isTeacher: Boolean,
    uiState: SubjectClassroomUiState,
    encryptedPreferencesHelper: EncryptedPreferencesHelper,
    sessionRepository: SessionRepository,
    deckRepository: DeckRepository,
    onSessionJoin: (String) -> Unit,
    onNavigateToHomeworkSubmissionReviewScreen: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuperrTheme.colorScheme.White)
    ) {
        uiState.subjectClass?.let { subjectClass ->
            Header(
                subjectClass,
                isTeacher,
                selectedTabIndex,
                onTabSelected = { selectedTabIndex = it })
        }
        when (selectedTabIndex) {
            0 -> {
                SessionsTab(
                    viewModel(
                        factory = SessionsTabViewModelFactory(
                            classId = uiState.subjectClass?.id ?: "",
                            encryptedPreferencesHelper,
                            deckRepository,
                            sessionRepository,
                            onSessionJoin
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun Header(
    subjectClass: SubjectClass,
    isTeacher: Boolean,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.math_cover), // You might want to map this based on the subject
            contentDescription = stringResource(id = R.string.subject_classroom_cover_image_desc),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.fdp),
            contentScale = ContentScale.Crop
        )
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(12.fdp),
            modifier = Modifier.padding(vertical = 8.fdp)
        ) {
            Text(
                text = if (isTeacher) subjectClass.subject else stringResource(
                    id = R.string.subject_classroom_class, subjectClass.grade, subjectClass.section
                ),
                style = SuperrTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.fdp, vertical = 6.fdp)
            )
        }
        Text(
            text = if (isTeacher) stringResource(
                id = R.string.subject_classroom_class, subjectClass.grade, subjectClass.section
            ) else subjectClass.subject,
            style = SuperrTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.fdp)
        )
        CenteredTabRow(
            tabs = if (isTeacher) listOf(
                stringResource(id = R.string.subject_classroom_tab_sessions),
                stringResource(id = R.string.subject_classroom_tab_homework),
                stringResource(id = R.string.subject_classroom_tab_attendance),
                stringResource(id = R.string.subject_classroom_tab_files)
            ) else listOf(
                stringResource(id = R.string.subject_classroom_tab_sessions),
                stringResource(id = R.string.subject_classroom_tab_homework),
                stringResource(id = R.string.subject_classroom_tab_files),
            ), selectedTabIndex = selectedTabIndex, onTabSelected = onTabSelected
        )
    }
}

@Composable
fun CenteredTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.fdp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .background(
                    color = SuperrTheme.colorScheme.White
                )
                .padding(4.fdp)
        ) {
            tabs.forEachIndexed { index, tab ->
                CustomTab(
                    text = tab, selected = index == selectedTabIndex,
                    // TODO: Check and verify clickable
                    onClick = { onTabSelected(index) })
            }
        }
    }
}

@Composable
fun CustomTab(
    text: String, selected: Boolean, onClick: () -> Unit
) {
    val primaryColor = SuperrTheme.colorScheme.Black
    Box(modifier = Modifier
        .flatClickable(
            // TODO: Check and verify clickable
            onClick = onClick
        )
        .padding(horizontal = 16.fdp, vertical = 8.fdp)
        .drawWithContent {
            drawContent()
            if (selected) {
                val borderSize = 3.fdp.toPx()
                drawLine(
                    color = primaryColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderSize
                )
            }
        }) {
        Text(
            text = text,
            style = SuperrTheme.typography.bodySmall,
            color = if (selected) SuperrTheme.colorScheme.Black else SuperrTheme.colorScheme.Gray500,
            modifier = Modifier.padding(bottom = 6.fdp)
        )
    }
}
