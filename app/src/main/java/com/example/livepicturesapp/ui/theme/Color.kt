package com.example.livepicturesapp.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LivePicturesColors(
    val green: Color,
    val white: Color,
    val black: Color,
    val blue: Color,
    val red: Color,
    val gray: Color,
)

val LocalLivePicturesColors = staticCompositionLocalOf {
    LivePicturesColors(
        green = Color.Unspecified,
        white = Color.Unspecified,
        black = Color.Unspecified,
        blue = Color.Unspecified,
        red = Color.Unspecified,
        gray = Color.Unspecified,
    )
}

val livePicturesColors = LivePicturesColors(
    green = Color(0xFFA8DB10),
    white = Color(0xFFFFFFFF),
    black = Color(0xFF000000),
    blue = Color(0xFF1976D2),
    red = Color(0xFFFF3D00),
    gray = Color(0xFF8B8B8B),
)