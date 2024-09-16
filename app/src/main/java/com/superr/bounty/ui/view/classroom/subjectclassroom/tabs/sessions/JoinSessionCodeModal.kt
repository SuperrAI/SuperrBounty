package com.superr.bounty.ui.view.classroom.subjectclassroom.tabs.sessions

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.superr.bounty.R
import com.superr.bounty.ui.theme.RawNoteFontFamily
import com.superr.bounty.ui.theme.SuperrTheme
import com.superr.bounty.utils.FlatIconButton
import com.superr.bounty.utils.fdp

@Composable
fun JoinSessionCodeModal(
    onDismissRequest: () -> Unit,
    onCodeEntered: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    val maxLength = 6
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.fdp)
                .clip(RoundedCornerShape(16.fdp))
                .background(SuperrTheme.colorScheme.White)
                .border(
                    width = 2.fdp,
                    color = SuperrTheme.colorScheme.Gray300,
                    shape = RoundedCornerShape(16.fdp)
                )
                .padding(24.fdp)
        ) {
            Column(
                modifier = Modifier
                    .width(636.fdp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "What's the secret code?",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = RawNoteFontFamily,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.fdp))
                Text(
                    text = "Enter the code shared by your teacher to join the session",
                    fontSize = 16.sp,
                    color = SuperrTheme.colorScheme.Gray500,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.fdp))
                PinInput(
                    pin = code,
                    onPinChange = { newPin ->
                        if (newPin.length <= maxLength) code = newPin
                    },
                    pinLength = maxLength,
                    onDone = { if (code.length == maxLength) onCodeEntered(code) },
                    focusRequester = focusRequester
                )
            }

            FlatIconButton(
                // TODO: Check and verify clickable
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = SuperrTheme.colorScheme.Gray500
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinInput(
    pin: String,
    onPinChange: (String) -> Unit,
    pinLength: Int,
    onDone: () -> Unit,
    focusRequester: FocusRequester
) {
    BasicTextField(
        value = pin,
        onValueChange = onPinChange,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                    onDone()
                    true
                } else {
                    false
                }
            },
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.fdp)
            ) {
                repeat(pinLength) { index ->
                    PinCell(
                        isFilled = index < pin.length,
                        modifier = Modifier.weight(1f),
                        filledWith = if (index < pin.length) pin[index] else ' '
                    )
                }
            }
        }
    )
}

@Composable
fun PinCell(
    isFilled: Boolean, filledWith: Char, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(96.fdp, 104.fdp)
            .padding(0.fdp)
            .border(
                width = 2.fdp,
                color = SuperrTheme.colorScheme.Gray300,
                shape = RoundedCornerShape(24.fdp)
            )
            .padding(horizontal = 32.fdp, vertical = 24.fdp), contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = filledWith.toString(),
                style = SuperrTheme.typography.titleLarge,
                color = SuperrTheme.colorScheme.Black
            )
        }
    }
}