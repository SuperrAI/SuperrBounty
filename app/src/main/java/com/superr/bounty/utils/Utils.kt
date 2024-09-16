package com.superr.bounty.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import com.superr.bounty.ui.theme.SuperrTheme

private const val TAG = "Superr.Utils.Utils"

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.flatClickable(
    onClick: () -> Unit
): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun FlatTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val contentColor = when {
        !enabled -> SuperrTheme.colorScheme.Black.copy(alpha = 0.38f)
        selected -> SuperrTheme.colorScheme.Black
        else -> SuperrTheme.colorScheme.Black.copy(alpha = 0.60f)
    }

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.flatClickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .alpha(if (enabled) 1f else 0.38f),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(value = SuperrTheme.typography.bodyMedium) {
                content()
            }
        }
    }
}

@Composable
fun FlatIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.flatClick(onClick)
                } else {
                    Modifier
                }
            )
            .size(48.fdp) // Default size of IconButton
            .alpha(if (enabled) 1f else 0.38f), // 0.38f is the default disabled alpha
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// Existing flatClick modifier
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.flatClick(
    onClick: () -> Unit
): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

private const val SWIPE_THRESHOLD = 100f
private const val SWIPE_COOLDOWN = 1000L // Cooldown in milliseconds

@Composable
fun SwipeGestureHandler(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    var lastSwipeTime by remember { mutableStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastSwipeTime > SWIPE_COOLDOWN) {
                        when {
                            dragAmount < -SWIPE_THRESHOLD -> {
                                onSwipeLeft()
                                lastSwipeTime = currentTime
                            }

                            dragAmount > SWIPE_THRESHOLD -> {
                                onSwipeRight()
                                lastSwipeTime = currentTime
                            }
                        }
                    }
                }
            }
    ) {
        content()
    }
}