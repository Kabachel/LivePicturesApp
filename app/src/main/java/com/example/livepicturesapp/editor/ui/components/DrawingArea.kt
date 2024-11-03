package com.example.livepicturesapp.editor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.R
import com.example.livepicturesapp.editor.model.DrawingPath
import com.example.livepicturesapp.editor.model.Frame
import com.example.livepicturesapp.editor.model.InteractType
import com.example.livepicturesapp.editor.model.MotionType
import com.example.livepicturesapp.editor.model.PathProperties
import com.example.livepicturesapp.editor.utils.dragMotionEvent
import kotlin.math.pow

@Composable
internal fun ColumnScope.DrawingArea(
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    motionType: MutableState<MotionType>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    interactMode: MutableState<InteractType>,
    currentPath: MutableState<Path>,
    currentPathProperty: MutableState<PathProperties>,
    isAnimationShowing: Boolean
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_drawing_area_background),
            contentDescription = "Drawing area background",
            contentScale = ContentScale.FillBounds,
        )
        DrawingAreaContent(
            paths,
            previousFrames,
            pathsUndone,
            motionType,
            currentPosition,
            previousPosition,
            interactMode,
            currentPath,
            currentPathProperty,
            isAnimationShowing
        )
    }
}

@Composable
private fun DrawingAreaContent(
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    _motionType: MutableState<MotionType>,
    _currentPosition: MutableState<Offset>,
    _previousPosition: MutableState<Offset>,
    _interactMode: MutableState<InteractType>,
    _currentPath: MutableState<Path>,
    _currentPathProperty: MutableState<PathProperties>,
    isAnimationShowing: Boolean
) {
    val motionType by _motionType
    val currentPosition by _currentPosition
    val previousPosition by _previousPosition
    val interactMode by _interactMode
    val currentPath by _currentPath
    val currentPathProperty by _currentPathProperty

    val drawModifier = Modifier
        .fillMaxSize()
        .then(if (!isAnimationShowing) Modifier.dragMotionEvent(
            onDragStart = { pointerInputChange ->
                _motionType.value = MotionType.Down
                _currentPosition.value = pointerInputChange.position
            },
            onDrag = { pointerInputChange ->
                _motionType.value = MotionType.Move
                _currentPosition.value = pointerInputChange.position

                if (interactMode == InteractType.Move) {
                    val positionChange = pointerInputChange.positionChange()
                    paths.forEach { drawingPath ->
                        val path: Path = drawingPath.path
                        path.translate(positionChange)
                    }
                    previousFrames.forEach { frame ->
                        frame.drawingPaths.forEach { drawingPath ->
                            val path: Path = drawingPath.path
                            path.translate(positionChange)
                        }
                    }
                    currentPath.translate(positionChange)
                }
            },
            onDragEnd = { pointerInputChange ->
                _motionType.value = MotionType.Up
                _currentPosition.value = pointerInputChange.position
            }
        ) else Modifier)

    Canvas(modifier = drawModifier) {
        when (motionType) {
            MotionType.Down -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                _previousPosition.value = currentPosition
            }

            MotionType.Move -> {
                if (interactMode != InteractType.Move) {
                    currentPath.quadraticBezierTo(
                        x1 = previousPosition.x,
                        y1 = previousPosition.y,
                        x2 = (previousPosition.x + currentPosition.x) / 2,
                        y2 = (previousPosition.y + currentPosition.y) / 2,
                    )
                }
                _previousPosition.value = currentPosition
            }

            MotionType.Up -> {
                if (interactMode != InteractType.Move) {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    paths += DrawingPath(currentPath, currentPathProperty.copy())
                    _currentPath.value = Path()
                }

                _currentPosition.value = Offset.Unspecified
                _previousPosition.value = Offset.Unspecified
                _motionType.value = MotionType.Idle
                _currentPath.value = Path()
                _currentPathProperty.value = _currentPathProperty.value.copy()
                if (interactMode != InteractType.Move) {
                    pathsUndone.clear()
                }
            }

            MotionType.Idle -> Unit
        }

        val checkPoint = drawContext.canvas.nativeCanvas.saveLayer(null, null)

        paths.forEach {
            val path = it.path
            val property = it.properties

            if (!property.isErase) {
                drawPath(
                    color = property.color,
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth,
                        cap = property.strokeCap,
                        join = property.strokeJoin,
                    )
                )
            } else {
                drawPath(
                    color = Color.Transparent,
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth,
                        cap = property.strokeCap,
                        join = property.strokeJoin
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }

        previousFrames.forEachIndexed { index, frame ->
            val alpha = 0.25f.pow(index + 1)
            frame.drawingPaths.forEach {
                val path = it.path
                val property = it.properties

                if (!property.isErase) {
                    drawPath(
                        color = property.color,
                        path = path,
                        style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin,
                        ),
                        alpha = alpha,
                    )
                } else {
                    drawPath(
                        color = Color.Transparent,
                        path = path,
                        style = Stroke(
                            width = property.strokeWidth,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        ),
                        blendMode = BlendMode.Clear
                    )
                }
            }
        }

        if (motionType != MotionType.Idle) {
            if (!currentPathProperty.isErase) {
                drawPath(
                    color = currentPathProperty.color,
                    path = currentPath,
                    style = Stroke(
                        width = currentPathProperty.strokeWidth,
                        cap = currentPathProperty.strokeCap,
                        join = currentPathProperty.strokeJoin
                    )
                )
            } else {
                drawPath(
                    color = Color.Transparent,
                    path = currentPath,
                    style = Stroke(
                        width = currentPathProperty.strokeWidth,
                        cap = currentPathProperty.strokeCap,
                        join = currentPathProperty.strokeJoin
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }
        drawContext.canvas.nativeCanvas.restoreToCount(checkPoint)
    }
}