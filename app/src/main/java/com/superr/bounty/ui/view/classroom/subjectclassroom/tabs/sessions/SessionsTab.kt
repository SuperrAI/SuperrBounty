package com.superr.bounty.ui.view.classroom.subjectclassroom.tabs.sessions

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.superr.bounty.R
import com.superr.bounty.activities.LiveSessionDeckPlayerActivity
import com.superr.bounty.domain.model.Session
import com.superr.bounty.domain.model.SessionStatus
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.fdp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "Superr.SubjectClassroom.SessionsTab"

@Composable
fun SessionsTab(
    viewModel: SessionsTabViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var joinSessionModalOpen by remember { mutableStateOf(false) }

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 56.fdp)
        ) {
            item {
                Text(
                    text = if (viewModel.isTeacher()) stringResource(id = R.string.sessions_tab_upcoming_sessions) else stringResource(
                        id = R.string.sessions_tab_today
                    ),
                    style = SuperrTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .width(724.fdp)
                        .padding(top = 12.fdp, end = 12.fdp, bottom = 12.fdp, start = 20.fdp)
                )
            }
            if (uiState.todaySessions.isNotEmpty()) {
                items(uiState.todaySessions) { session ->
                    TodaySessionItem(
                        session,
                        viewModel.isTeacher(),
                        // TODO: Check and verify clickable
                        onJoinSessionClick = { s ->
                            joinSessionModalOpen = true
                            viewModel.setCurrentSession(s)
                        },
                        // TODO: Check and verify clickable
                        onStartSessionClick = { s ->
                            viewModel.setCurrentSession(s)
                            context.startActivity(
                                Intent(
                                    context,
                                    LiveSessionDeckPlayerActivity::class.java
                                ).apply {
                                    putExtra("SESSION_ID", s.id)
                                })
                        })
                }
            } else {
                item {
                    Text(
                        text = stringResource(id = R.string.sessions_tab_no_sessions_today),
                        style = SuperrTheme.typography.labelMedium,
                        color = SuperrTheme.colorScheme.Gray500,
                        modifier = Modifier
                            .width(724.fdp)
                            .padding(top = 12.fdp, end = 12.fdp, bottom = 12.fdp, start = 20.fdp)
                    )
                }
            }

            if (uiState.weekSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.fdp))
                    Text(
                        text = stringResource(id = R.string.sessions_tab_this_week),
                        style = SuperrTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .width(724.fdp)
                            .padding(top = 12.fdp, end = 12.fdp, bottom = 12.fdp, start = 20.fdp)
                    )
                }
                items(uiState.weekSessions) { session ->
                    HorizontalDivider(color = SuperrTheme.colorScheme.Gray300)
                    PastSessionItem(session)
                }
            }

            if (uiState.monthSessions.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.sessions_tab_this_month),
                        style = SuperrTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .width(724.fdp)
                            .padding(top = 12.fdp, end = 12.fdp, bottom = 12.fdp, start = 20.fdp)
                    )
                }
                items(uiState.monthSessions) { session ->
                    HorizontalDivider(color = SuperrTheme.colorScheme.Gray300)
                    PastSessionItem(session)
                }
            }

            if (uiState.allTimeSessions.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.sessions_tab_all_time),
                        style = SuperrTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .width(724.fdp)
                            .padding(top = 12.fdp, end = 12.fdp, bottom = 12.fdp, start = 20.fdp)
                    )
                }
                items(uiState.allTimeSessions) { session ->
                    HorizontalDivider(color = SuperrTheme.colorScheme.Gray300)
                    PastSessionItem(session)
                }
            }
        }

        if (joinSessionModalOpen) {
            JoinSessionCodeModal(onDismissRequest = { joinSessionModalOpen = false },
                onCodeEntered = { code ->
                    viewModel.verifySessionCode(code)
                    joinSessionModalOpen = false
                })
        }
    }
}

@Composable
fun TodaySessionItem(
    session: Session,
    isTeacher: Boolean,
    onJoinSessionClick: (Session) -> Unit,
    onStartSessionClick: (Session) -> Unit
) {

    Row(
        modifier = Modifier
            .size(724.fdp, 108.fdp)
            .padding(bottom = 16.fdp)
            .border(
                width = 2.fdp,
                color = SuperrTheme.colorScheme.Gray300,
                shape = RoundedCornerShape(20.fdp)
            )
            .padding(top = 12.fdp, end = 16.fdp, bottom = 12.fdp, start = 20.fdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.fdp)) {
            Text(
                text = session.title, style = SuperrTheme.typography.bodySmall
            )
            Text(
                text = getSessionStatusText(session.status),
                style = SuperrTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        if (isTeacher) {
            Button(
                modifier = Modifier.alpha(if (session.status == SessionStatus.IN_PROGRESS) 1f else 0f),
                // TODO: Check and verify clickable
                onClick = {
                    Log.d(TAG, "TodaySessionItem: Start session clicked.")
                    onStartSessionClick(session)
                },
                shape = RoundedCornerShape(12.fdp)
            ) {
                Text(
                    text = "Resume",
                    style = SuperrTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                modifier = Modifier.alpha(if (session.status == SessionStatus.IN_PROGRESS) 1f else 0f),
                // TODO: Check and verify clickable
                onClick = { onJoinSessionClick(session) },
                shape = RoundedCornerShape(12.fdp)
            ) {
                Text(
                    text = "Join",
                    style = SuperrTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

    }

}

@Composable
fun PastSessionItem(session: Session) {
    Row(
        modifier = Modifier
            .width(724.fdp)
            .padding(top = 12.fdp, end = 16.fdp, bottom = 12.fdp, start = 20.fdp),
        horizontalArrangement = Arrangement.spacedBy(16.fdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.fdp)
        ) {
            Text(
                text = session.title, style = SuperrTheme.typography.bodySmall
            )
            Text(
                text = formatDate(session.startTime),
                style = SuperrTheme.typography.bodySmall,
                color = SuperrTheme.colorScheme.Gray500
            )
        }
        if (session.deckIds.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.fdp)) {
                repeat(session.deckIds.size.coerceAtMost(3)) {
                    Box(
                        modifier = Modifier
                            .size(52.fdp, 64.fdp)
                            .background(
                                SuperrTheme.colorScheme.Gray500, RoundedCornerShape(4.fdp)
                            )
                    )
                }
            }
        }
    }

}

// Helper functions
@Composable
fun getSessionStatusText(status: SessionStatus): String {
    return when (status) {
        SessionStatus.SCHEDULED -> stringResource(id = R.string.sessions_tab_scheduled)
        SessionStatus.IN_PROGRESS -> stringResource(id = R.string.sessions_tab_in_progress)
        SessionStatus.COMPLETED -> stringResource(id = R.string.sessions_tab_completed)
        SessionStatus.CANCELLED -> stringResource(id = R.string.sessions_tab_cancelled)
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMMM d, EEEE", Locale.getDefault())
    return formatter.format(date)
}