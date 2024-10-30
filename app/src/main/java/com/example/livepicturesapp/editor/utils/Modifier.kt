package com.example.livepicturesapp.editor.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange

private suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) {
    // DragStart
    val firstDownGesture: PointerInputChange = awaitFirstDown()
    onDragStart(firstDownGesture)

    var pointer: PointerInputChange = firstDownGesture

    val dragChange: PointerInputChange? =
        awaitTouchSlopOrCancellation(pointerId = firstDownGesture.id) { change: PointerInputChange, _: Offset ->
            if (change.positionChange() != Offset.Zero) change.consume()
        }

    // Drag
    if (dragChange != null) {
        drag(pointerId = dragChange.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onDrag(pointer)
        }
    }

    // DragEnd
    onDragEnd(pointer)
}

/**
 * [Modifier] for dragging processing
 */
fun Modifier.dragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
): Modifier {
    return then(
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitDragMotionEvent(onDragStart, onDrag, onDragEnd)
            }
        }
    )
}