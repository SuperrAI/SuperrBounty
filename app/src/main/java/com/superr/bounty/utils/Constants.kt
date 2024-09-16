package com.superr.bounty.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val TAG = "Superr.Utils.Constants"

class Constants {
    companion object
}

const val SCALING_FACTOR: Float = 1.0f
const val TEXTUNIT_SCALING_FACTOR: Float = 1.1f

@Stable
inline val Int.fdp: Dp get() = this.dp.times(SCALING_FACTOR)

@Stable
inline val Float.fdp: Dp get() = this.dp.times(SCALING_FACTOR)

@Stable
inline val Double.fdp: Dp get() = this.dp.times(SCALING_FACTOR)

@Stable
inline val Int.fsp: TextUnit get() = this.sp.times(TEXTUNIT_SCALING_FACTOR)

@Stable
inline val Float.fsp: TextUnit get() = this.sp.times(TEXTUNIT_SCALING_FACTOR)

@Stable
inline val Double.fsp: TextUnit get() = this.sp.times(TEXTUNIT_SCALING_FACTOR)