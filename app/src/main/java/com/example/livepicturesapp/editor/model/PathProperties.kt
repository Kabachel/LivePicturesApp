package com.example.livepicturesapp.editor.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

/**
 * Properties for modification path
 */
data class PathProperties(
    var strokeWidth: Float = 15f,
    var color: Color = Color.Black,
    var strokeCap: StrokeCap = StrokeCap.Round,
    var strokeJoin: StrokeJoin = StrokeJoin.Round,
    var isErase: Boolean = false
)