package com.example.livepicturesapp.editor.model

import androidx.compose.ui.graphics.Path

/**
 * Info for one path
 *
 * @property path [Path] for drawing
 * @property properties [PathProperties] for modification path
 */
data class DrawingPath(
    val path: Path,
    val properties: PathProperties,
)