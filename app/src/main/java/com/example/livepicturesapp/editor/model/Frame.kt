package com.example.livepicturesapp.editor.model

import java.util.UUID

/**
 * Frame with path and path properties info
 */
data class Frame(
    val drawingPaths: List<DrawingPath>,
    val uuid: UUID = UUID.randomUUID(),
)