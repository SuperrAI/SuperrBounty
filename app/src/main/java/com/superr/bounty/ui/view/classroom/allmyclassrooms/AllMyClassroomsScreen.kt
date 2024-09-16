package com.superr.bounty.ui.view.classroom.allmyclassrooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superr.bounty.R
import com.superr.bounty.domain.model.SubjectClass
import com.superr.bounty.domain.model.User
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.ui.theme.withColor
import com.superr.bounty.ui.theme.withFontWeight
import com.superr.bounty.ui.theme.withLineHeight
import com.superr.bounty.ui.theme.withTextAlign
import com.superr.bounty.utils.fdp
import com.superr.bounty.utils.flatClickable
import com.superr.bounty.utils.fsp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.min

private const val TAG = "Superr.AllMyClassroomsScreen"

@Composable
fun AllMyClassroomsScreen(viewModel: AllMyClassroomsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(56.fdp)
    ) {
        TopAppBar(user = uiState.user)
        Spacer(modifier = Modifier.height(72.fdp))
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text(
                stringResource(
                    id = R.string.all_my_classrooms_error_text,
                    uiState.error!!
                )
            )

            else -> SubjectClassesGrid(
                uiState.classes,
                viewModel::onClassroomClick,
                viewModel.isTeacher()
            )
        }
    }
}

@Composable
fun TopAppBar(
    user: User?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.fdp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.all_my_classrooms_title),
                style = SuperrTheme.typography.titleLarge,
            )

        }
        val currentDayOfWeek =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(
            text = stringResource(
                id = R.string.all_my_classrooms_greeting_text,
                currentDayOfWeek,
                user?.name ?: stringResource(id = R.string.all_my_classrooms_default_user)
            ),
            style = SuperrTheme.typography.bodySmall,
            lineHeight = 28.fsp
        )
    }

}

@Composable
fun SubjectClassesGrid(
    classes: List<SubjectClass>, onSubjectClassroomClick: (SubjectClass) -> Unit, isTeacher: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(32.fdp),
        verticalArrangement = Arrangement.spacedBy(32.fdp)
    ) {
        items(classes) { classItem ->
            SubjectClassCard(classItem, onSubjectClassroomClick, isTeacher)
        }
    }
}

@Composable
fun SubjectClassCard(
    subjectClassItem: SubjectClass,
    onSubjectClassroomClick: (SubjectClass) -> Unit,
    isTeacher: Boolean
) {
    Box(
        modifier = Modifier
            .size(220.fdp, 248.fdp)
            .border(1.fdp, SuperrTheme.colorScheme.Black, RoundedCornerShape(20.fdp))
            .flatClickable {
                // TODO: Check and verify clickable
                onSubjectClassroomClick(subjectClassItem)
            },
    ) {
        Box(modifier = Modifier.size(220.dp, 124.dp)) {
            CurvedBox(
                modifier = Modifier.fillMaxSize(),
                strokeColor = SuperrTheme.colorScheme.Gray400,
                strokeWidth = 2f,
                image = ImageBitmap.imageResource(id = getSubjectIcon(subjectClassItem.subject))
            )
        }
        Column(
            modifier = Modifier
                .padding(bottom = 14.fdp, start = 20.fdp)
                .align(Alignment.BottomStart), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isTeacher) subjectClassItem.subject else stringResource(
                    id = R.string.all_my_classrooms_grade_section,
                    subjectClassItem.grade,
                    subjectClassItem.section
                ),
                style = SuperrTheme.typography.bodySmall
                    .withTextAlign(TextAlign.Left)
                    .withLineHeight(22.32.fsp)
                    .withFontWeight(FontWeight.SemiBold)
                    .withColor(SuperrTheme.colorScheme.Gray500)
            )
            Text(
                text = if (isTeacher) stringResource(
                    id = R.string.all_my_classrooms_grade_section,
                    subjectClassItem.grade,
                    subjectClassItem.section
                ) else subjectClassItem.subject,
                style = SuperrTheme.typography.bodyLarge.withTextAlign(TextAlign.Left),
            )
        }

    }
}

@Composable
fun CurvedBox(
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 1f,
    image: ImageBitmap
) {
    Box(modifier = modifier.clipToBounds()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val wholePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height)
                cubicTo(
                    x1 = width * 0.75f,
                    y1 = height * 0.5f,
                    x2 = width * 0.25f,
                    y2 = height * 0.5f,
                    x3 = 0f,
                    y3 = height
                )
                close()
            }

            clipPath(wholePath) {
                // Calculate scaling factor to fit the image without distortion
                val scaleFactor = min(
                    width / image.width, height / image.height
                )

                // Calculate new dimensions
                val newWidth = image.width * scaleFactor
                val newHeight = image.height * scaleFactor

                // Calculate offset to center the image
                val offsetX = (width - newWidth) / 2f
                val offsetY = (height - newHeight) / 2f

                // Draw the image
                drawImage(
                    image = image,
                    topLeft = Offset(offsetX, offsetY),
                )
            }

            // Draw the white borders
            val whiteBorderPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = whiteBorderPath, color = Color.White, style = Stroke(width = strokeWidth)
            )

            // Draw the colored bottom curve
            val curvePath = Path().apply {
                moveTo(0f, height)
                cubicTo(
                    x1 = width * 0.25f,
                    y1 = height * 0.5f,
                    x2 = width * 0.75f,
                    y2 = height * 0.5f,
                    x3 = width,
                    y3 = height
                )
            }
            drawPath(
                path = curvePath, color = strokeColor, style = Stroke(width = strokeWidth)
            )
        }
    }
}

fun getSubjectIcon(subject: String): Int {
    return when (subject.lowercase(Locale.getDefault())) {
        "english" -> R.drawable.sample_subject_mini_english
        "math" -> R.drawable.sample_subject_mini_mathematics
        "science" -> R.drawable.sample_subject_mini_science
        "individuals and societies" -> R.drawable.sample_subject_mini_individual_and_societies
        "french" -> R.drawable.sample_subject_mini_french
        "design" -> R.drawable.sample_subject_mini_design
        "wellness" -> R.drawable.sample_subject_mini_wellness
        "visual arts" -> R.drawable.sample_subject_mini_visual_arts
        else -> R.drawable.sample_subject_mini_english
    }
}