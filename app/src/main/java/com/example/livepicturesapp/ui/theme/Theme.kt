package com.example.livepicturesapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun LivePicturesTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) {
        darkThemeLivePicturesColors
    } else {
        lightThemeLivePicturesColors
    }
    CompositionLocalProvider(LocalLivePicturesColors provides colors) {
        MaterialTheme(
            content = content
        )
    }
}

object LivePicturesTheme {
    val colors: LivePicturesColors
        @Composable
        get() = LocalLivePicturesColors.current
}
