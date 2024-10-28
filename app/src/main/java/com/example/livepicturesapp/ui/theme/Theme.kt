package com.example.livepicturesapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun LivePicturesTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLivePicturesColors provides livePicturesColors) {
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
